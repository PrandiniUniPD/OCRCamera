package com.example.imageprocessing;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.util.Log;
import com.example.imageprocessing.exceptions.ConversionFailedException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt4;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_16S;
import static org.opencv.core.CvType.CV_16U;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_32S;
import static org.opencv.core.CvType.CV_64F;
import static org.opencv.core.CvType.CV_8S;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.core.CvType.CV_8UC1;

/**
 * Class used to process the image before passing the image to the OCR
 */
public class leoPreProcessing implements PreProcessingMethods {

    //Tag used to identify the log
    private final String TAG = "PreProcessing";


    /**
     * Constructor of the class which initialize the openCV library
     * @author Thomas Porro (g1)
     */
    public PreProcessing() {
        //TODO verify if the library is correctly loaded

        //Load the openCV library
        LibraryLoaderSingletone.loadLibrary();
    }

    /**
     * Calculate the angle between the text and the horizontal
     * @param image The image you want to analyze
     * @return the angle between the text and the horizontal. 0 if it fails
     * @author Thomas Porro (g1)
     */
    private double computeSkew(Bitmap image){

        //Turns the image into a matrix
        Mat img;
        try{
            img = IPUtils.conversionBitmapToMat(image);
        } catch (ConversionFailedException error){
            Log.e(TAG, error.getErrorMessage());
            return 0;
        }


        Mat grayscale = new Mat();
        Imgproc.cvtColor(img, grayscale, Imgproc.COLOR_RGB2GRAY);
       /*
            Method used for debug
            IPUtils.save(img, "grayScale", ".jpg");
        */

        //Invert the colors of "img" onto itself
        Core.bitwise_not(img, img);

        //Detect the edges in the image
        Mat canny = IPBuilder.doCanny(new IPBuilder.CannyBuilder(grayscale)
                .withMinThreshold(50)
                .withMaxThreshold(200)
                .withApertureSize(3)
                .withL2gradient(false));

        //Create a 4 dimensions vector using matrix
        MatOfInt4 lines = IPBuilder.doHoughLinesP(new IPBuilder.HoughLinesPBuilder(canny)
                .withRho(1)
                .withTheta(Math.PI / 180)
                .withThreshold(50)
                .withMinLineLength(50)
                .withMaxLineGap(10));

        double meanAngle = 0;
        Log.d(TAG, "rows = " + lines.cols() + "\ncols = " + lines.cols());

        //Analyzes the text line per line
        for (int i = 0; i < lines.rows(); i++) {
            //Get points from the beginning and the ending of the line of text
            double[] vec = lines.get(i, 0);

            //First point
            double x1 = vec[0];
            double y1 = vec[1];

            //Second point
            double x2 = vec[2];
            double y2 = vec[3];

            //Sum all the angle of the lines of text
            meanAngle += Math.atan2(y2 - y1, x2 - x1);
        }

        //Calculate the meanAngle by dividing it with the number of rows
        meanAngle /= lines.rows();

        //Transform the angle in degrees
        double degreesAngle = Math.toDegrees(meanAngle);
        Log.i(TAG, "Mean angle=" + degreesAngle);
        return degreesAngle;
    }

    /**
     * Rotate the given bitmap with the given angle
     * @param original The image that we want to corrected, must not be null
     * @param degrees The degrees of the angle we want to rotate
     * @return the rotatedImage
     * @author Thomas Porro(g1), Giovanni Fasan (g1), Oscar Garrido (g1)
     */
    private static Bitmap rotateBitmap(@NonNull Bitmap original, double degrees) {

        //Obtain the dimen of the image
        int width = original.getWidth();
        int height = original.getHeight();

        //Prepare the rotation matrix. The minus before degrees allows us to rotate the
        //image in the right way
        Matrix matrix = new Matrix();
        matrix.preRotate((float)-degrees);

        //Rotate the Bitmap and returns it
        return Bitmap.createBitmap(original, 0, 0, width, height, matrix, true);

    }


    /**
     * Performs the skew correction
     * @param image The image that I want to rotate, must not be null
     * @return The image corrected
     * @author Thomas Porro(g1), Giovanni Fasan (g1), Oscar Garrido (g1)
     */
    private Bitmap editSkew(@NonNull Bitmap image){
        double angle = computeSkew(image);
        return rotateBitmap(image, angle);
    }


    /**
     * Detect if the image is bright
     * @param imageMat the image we want to detect the brightness
     * @return IMAGE_IS_OK if image is neither too bright nor too dark,
     *         IMAGE_IS_BRIGHT if image is too bright,
     *         IMAGE_IS_DARK if image is too dark.
     * @author Thomas Porro(g1), Giovanni Fasan(g1), Leonardo Pratesi(g1)
     */
    private BrightnessValue isBright(Mat imageMat){
        //Converts the image into a matrix
        Mat brightnessMat = new Mat();

        //Changes the format of the matrix into a RGB one
        Imgproc.cvtColor(imageMat, brightnessMat, Imgproc.COLOR_RGBA2RGB);

        //Obtain 3 different matrix with the 3 elemental colors
        List<Mat> color = new ArrayList<>();
        Core.split(brightnessMat, color);

        /*Each color is multiplied with his luminance.
          The colors are in order RGB, so to access the che color I use the number 0, 1, 2 in order
          For more informations see https://en.wikipedia.org/wiki/Relative_luminance*/
        Mat redLuminance = new Mat();
        Core.multiply(color.get(0), new Scalar(0.2126), redLuminance);
        Mat greenLuminance= new Mat();
        Core.multiply(color.get(1), new Scalar(0.7152), greenLuminance);
        Mat blueLuminance = new Mat();
        Core.multiply(color.get(2), new Scalar(0.0722), blueLuminance);

        //Sums the matrix of the colors into a single one
        Mat tempLuminance = new Mat();
        Mat totalLuminance = new Mat();
        Core.add(redLuminance , greenLuminance , tempLuminance); //Red + Green = Temp
        Core.add(tempLuminance , blueLuminance , totalLuminance); //Temp + Blue = Luminance

        //Calculate the sum of the values of all pixels
        Scalar sum = Core.sumElems(totalLuminance);

        //Determines the image's bit
        int bit;
        switch ( brightnessMat.depth() ) {
            case CV_8U:  bit = 8; break;
            case CV_8S:  bit = 8; break;
            case CV_16U: bit = 16; break;
            case CV_16S: bit = 16; break;
            case CV_32S: bit = 32; break;
            case CV_32F: bit = 32; break;
            case CV_64F: bit = 64; break;
            default: return BrightnessValue.IMAGE_IS_OK;
        }
        //Calculate the percentage of the brightness
        double brightness = sum.val[0]/((Math.pow(2,bit)-1)*brightnessMat.rows()
                *brightnessMat.cols())*2;

        Log.d(TAG, "Brightness:"+brightness);

        /*Bounds to define if the image is dark or bright.
          The values were decided on the basis of various tests*/
        double upperBound = 0.9;
        double lowerBound = 0.4;

        if (brightness > upperBound){             //Image is too bright
            return BrightnessValue.IMAGE_TOO_BRIGHT;
        } else if (brightness < lowerBound){        //Image is too dark
            return BrightnessValue.IMAGE_TOO_DARK;
        } else {      //Image is neither too bright nor too dark
            return BrightnessValue.IMAGE_IS_OK;
        }
    }


    /**
     * Change the brightness of the image into an optimal one
     * @param image the image we want to modify the brightness
     * @return the image with the modified brightness
     * @author Thomas Porro(g1), Giovanni Fasan(g1), Oscar Garrido(g1)
     */
    private Bitmap editBright(Bitmap image){
        /*This variable is used to put a limit to the change of the image's brightness.
          The value 240 is derived from the fact that in the for loop we try to modify
          the value of all the pixels of a step, and being the maximum value = 255 (pixel's
          color maximum value, we put the limit on 240*/
        final int maxBrightness = 240;
        final int step = 15;

        //Converts the image into a matrix
        Mat bright;
        try{
            bright = IPUtils.conversionBitmapToMat(image);
        } catch (ConversionFailedException error){
            Log.e(TAG, error.getErrorMessage());
            return image;
        }

        //Call the internal method isBright to detect if the image is bright or dark
        //and change the brightness according to the number obtained
        Mat modifiedMat = new Mat();
        switch (isBright(bright)) {
            case IMAGE_TOO_BRIGHT:
                Log.d(TAG, "Case==IMAGE_TOO_BRIGHT");
                //Darkens the colour's brightness until it's in an optimal value
                for(double changeBrightness = 0; changeBrightness != maxBrightness;
                    changeBrightness -= step){
                   /*This variable is used to select the type of matrix we want to abtain in the
                      the convertTo method. If it's negative the type doesn't change*/
                    int matrixType = -1;

                    /*This variable is used to change the contrast of the matrix, but we want
                      only modify the brightness so we put the value 1 because the method use
                      this formula from the documentation:
                      m(x,y) = saturate _ cast<rType>(alpha(*this)(x,y) + beta)
                      We called beta as changeBrightness*/
                    int alpha = 1;

                    /*Modify the values of all pixels with an alpha and beta value following
                      the formula above.*/
                    bright.convertTo(modifiedMat, matrixType, alpha, changeBrightness);

                    //Verify if the image is good enough
                    if(isBright(modifiedMat) == BrightnessValue.IMAGE_IS_OK){
                        try{
                            return IPUtils.conversionMatToBitmap(modifiedMat);
                        } catch (ConversionFailedException error){
                            Log.e(TAG, error.getErrorMessage());
                            return image;
                        }
                    }
                }
                break;

            case IMAGE_TOO_DARK:
                Log.d(TAG, "Case==IMAGE_TOO_DARK");
                //The variables are explained in the case above
                //Lightens the colour's brightness until it's in an optimal value
                for(double changeBrightness = 0; changeBrightness != maxBrightness;
                    changeBrightness += step){

                    //Converts an array to another data type with optional scaling. The variables's
                    // values are explained in case 1
                    int matrixType = -1;
                    int alpha = 1;
                    bright.convertTo(modifiedMat, matrixType, alpha, changeBrightness);

                    //Verify if the image is good enough
                    if(isBright(modifiedMat) == BrightnessValue.IMAGE_IS_OK){
                        //If the conversion failed it returns the original image
                        try{
                            return IPUtils.conversionMatToBitmap(modifiedMat);
                        } catch (ConversionFailedException error){
                            Log.e(TAG, error.getErrorMessage());
                            return image;
                        }
                    }
                }
                break;

            case IMAGE_IS_OK: //Image is neither too bright nor too dark
                Log.d(TAG, "Case==IMAGE_IS_OK");
                return image;
        }
        return image;
    }


    /**
     * @author Thomas Porro(g1), Oscar Garrido (g1), Giovanni Fasan(g1), Leonardo Pratesi(g1)
     * See PreProcessingMethods.java
     */
    @Override
    public boolean isBlurred(Bitmap image) {

        //Total number of color
        int maxLap = -16777216;

        //Threshold above which the color is out of focus
        final int threshold = -6118750;

        //Converts the image into a matrix
        Mat imageMat;
        try {
            imageMat = IPUtils.conversionBitmapToMat(image);
        } catch (ConversionFailedException error){
            Log.e(TAG, error.getErrorMessage());
            return false;
        }

        //Turn the colored matrix into a grayscale matrix
        Mat grayImageMat = new Mat();
        Imgproc.cvtColor(imageMat, grayImageMat, Imgproc.COLOR_BGR2GRAY);

        /*Use the openCV's Laplacian methods to apply a transformation that allow us to detect
          the image blurriness*/
        Mat laplacianMat = new Mat();
        Imgproc.Laplacian(grayImageMat, laplacianMat, CV_8U);
        //Converts the matrix into another format used to detect the blur
        Mat laplacianMat8Bit = new Mat();
        laplacianMat.convertTo(laplacianMat8Bit, CV_8UC1);

        //Create a Bitmap with the given matrix, and obtain all the pixels from it
        Bitmap laplacianImage;
        try{
            laplacianImage = IPUtils.conversionMatToBitmap(laplacianMat8Bit);
        } catch (ConversionFailedException error){
            Log.e(TAG, error.getErrorMessage());
            return false;
        }

        //Extracts all the pixels of the laplacian image into the array
        int[] pixels = IPBuilder.doGetPixels(new IPBuilder.GetPixelsBuilder(laplacianImage)
                  .withStride(laplacianImage.getWidth())
                  .withWidth(laplacianImage.getWidth())
                  .withHeight(laplacianImage.getHeight())
                  );
                  
        //Searches the pixel that has the highest colour range in the RGB format
        for(int pixel : pixels){
            if(pixel > maxLap){
                maxLap = pixel;
            }
        }

        //Verify if the image is blurred
        if(maxLap < threshold){
            Log.d("Blur", "IS BLURRED");
        } else {
            Log.d("Blur", "IS NOT BLURRED");
        }
        return maxLap < threshold;
    }


    /**
     * @author Thomas Porro (g1), Giovanni Fasan (g1), Oscar Garrido (g1)
     * See PreProcessingMethods.java
     */
    @Override
    public Bitmap doImageProcessing(Bitmap image) {
        //Call methods that perform the image processing
        Bitmap modifiedBright = editBright(image);
        return editSkew(modifiedBright);
    }
}
