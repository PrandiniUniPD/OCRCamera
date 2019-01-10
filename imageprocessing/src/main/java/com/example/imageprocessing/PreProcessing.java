package com.example.imageprocessing;

import android.graphics.Bitmap;
import android.graphics.Matrix;
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
public class PreProcessing implements PreProcessingMethods {

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
        MatOfInt4 lines = new MatOfInt4();

        //Process the image with the Probabilistic Hough Transform
        double rho = 1;
        double theta = Math.PI / 180;
        int threshold = 50;
        double minLineLenght = 50;
        double maxLineGap = 10;
        Imgproc.HoughLinesP(grayscale, lines, rho, theta, threshold, minLineLenght, maxLineGap);

        double meanAngle = 0;
        Log.d(TAG, "rows = " + lines.cols() + "\ncols = " + lines.cols());

        //Analizes the text line per line
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
     * @param original The image that we want to corrected
     * @param degrees The degrees of the angle we want to rotate
     * @return the rotatedImage. If the image is null return the original image
     * @author Thomas Porro(g1), Giovanni Fasan (g1), Oscar Garrido (g1)
     */
    private static Bitmap rotateBitmap(Bitmap original, double degrees) {

        //TODO not correct because it returns null, maybe we should add a @NonNull
        if(original == null){
            return original;
        }

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
     * @param image The image that I want to rotate
     * @return The image corrected
     * @author Thomas Porro(g1), Giovanni Fasan (g1), Oscar Garrido (g1)
     */
    private Bitmap editSkew(Bitmap image){
        double angle = computeSkew(image);
        return rotateBitmap(image, angle);
    }


    /**
     * Detect if the image is bright
     * @param imageMat the image we want to detect the brightness
     * @return 0 if image is neither too bright nor too dark,
     *         1 if image is too bright,
     *         2 if image is too dark.
     * @author Thomas Porro(g1), Giovanni Fasan(g1), Leonardo Pratesi(g1)
     */
    private int isBright(Mat imageMat){
        //Converts the image into a matrix
        Mat brightnessMat = new Mat();

        //Changes the format of the matrix
        Imgproc.cvtColor(imageMat, brightnessMat, Imgproc.COLOR_RGBA2RGB);

        //Obtain 3 different matrix with the 3 elemental colors
        List<Mat> color = new ArrayList<>();
        Core.split(brightnessMat, color);

        /*Each color is multiplied with his luminance.
          For more informarions see https://en.wikipedia.org/wiki/Relative_luminance*/
        Mat lumRed = new Mat();
        Core.multiply(color.get(0), new Scalar(0.2126), lumRed);
        Mat lumGreen = new Mat();
        Core.multiply(color.get(1), new Scalar(0.7152), lumGreen);
        Mat lumBlue = new Mat();
        Core.multiply(color.get(2), new Scalar(0.0722), lumBlue);

        //Sums the matrix of the colors into a single one
        Mat lumTemp = new Mat();
        Mat lum = new Mat();
        Core.add(lumRed , lumGreen , lumTemp); //lumRed + lumGreen = lumTemp
        Core.add(lumTemp , lumBlue , lum); //lumBlue + lumTemp = lum

        //Calculate the sum of the values of all pixels
        Scalar sum = Core.sumElems(lum);

        //Image's bit
        int bit;
        switch ( brightnessMat.depth() ) {
            case CV_8U:  bit = 8; break;
            case CV_8S:  bit = 8; break;
            case CV_16U: bit = 16; break;
            case CV_16S: bit = 16; break;
            case CV_32S: bit = 32; break;
            case CV_32F: bit = 32; break;
            case CV_64F: bit = 64; break;
            default: return 0;
        }
        //Calculate the percentage of the brightness
        double brightness = sum.val[0]/((Math.pow(2,bit)-1)*brightnessMat.rows()*brightnessMat.cols())*2;

        Log.d(TAG, "Brightness:"+brightness);

        /*Bounds to define if the image is dark or bright.
          The values were decided on the basis of various tests*/
        double upperBound = 0.9;
        double lowerBound = 0.4;

        if (brightness > upperBound){             //Image is too bright
            return 1;
        } else if (brightness < lowerBound){        //Image is too dark
            return 2;
        } else {      //Image is neither too bright nor too dark
            return 0;
        }
    }


    /**
     * Change the brightness of the image into an optimal one
     * @param image the image we want to modify the brightness
     * @return the image with the modified brightness
     * @author Thomas Porro(g1), Giovanni Fasan(g1), Oscar Garrido(g1)
     */
    private Bitmap editBright(Bitmap image){
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
            case 1: //If the image is too bright
                Log.d(TAG, "Case==1 ==> Too bright");
                //Darkens the colour's brightness until it's in an optimal value
                for(double changeBrightness=0; changeBrightness!=-240; changeBrightness-=15){
                    //Converts an array to another data type with optional scaling.
                    bright.convertTo(modifiedMat, -1, 1, changeBrightness);
                    if(isBright(modifiedMat)==0){
                        try{
                            return IPUtils.conversionMatToBitmap(modifiedMat);
                        } catch (ConversionFailedException error){
                            Log.e(TAG, error.getErrorMessage());
                            return image;
                        }
                    }
                }
                break;

            case 2: //If the image is too dark
                Log.d(TAG, "Case==2 ==> Too dark");
                //Lightens the colour's brightness until it's in an optimal value
                for(double changeBrightness=0; changeBrightness!=240; changeBrightness+=15){
                    //Converts an array to another data type with optional scaling.
                    bright.convertTo(modifiedMat, -1, 1, changeBrightness);
                    if(isBright(modifiedMat)==0){
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

            case 0: //Image is neither too bright nor too dark
                Log.d(TAG, "Case==0 ==> Perfect image");
                return image;
        }
        return image;
    }


    /**
     * @author Thomas Porro(g1), Oscar Garrido (g1), Giovanni Fasan(g1).
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

        //TODO we have to comment this lines and add getPixels to the builder
        int[] pixels = new int[laplacianImage.getHeight() * laplacianImage.getWidth()];
        laplacianImage.getPixels(pixels, 0, laplacianImage.getWidth(), 0, 0,
                laplacianImage.getWidth(), laplacianImage.getHeight());

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
     * @author Thomas Porro (g1), Giovanni Fasan (g1), Oscar GArrido (g1)
     * See PreProcessingMethods.java
     */
    @Override
    public Bitmap doImageProcessing(Bitmap image) {
        //Call methods that perform the image processing
        Bitmap modifiedBright = editBright(image);
        return editSkew(modifiedBright);
    }

}
