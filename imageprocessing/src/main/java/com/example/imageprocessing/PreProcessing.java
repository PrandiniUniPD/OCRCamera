package com.example.imageprocessing;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.util.Log;
import com.example.imageprocessing.enumClasses.BlurValue;
import com.example.imageprocessing.enumClasses.BrightnessValue;
import com.example.imageprocessing.enumClasses.ProcessingResult;
import com.example.imageprocessing.exceptions.ConversionFailedException;
import com.example.imageprocessing.interfaces.BitmapContainer;
import com.example.imageprocessing.interfaces.PreProcessingMethods;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt4;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

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
        //Load the openCV library
        LibraryLoaderSingleton.loadLibrary();
    }

    /**
     * Calculate the angle between the text and the horizontal, and rotate the image
     * @param image The image you want to analyze
     * @return a BitmapContainer object that contain the image eventually rotated and the result
     *         of the process
     * @author Thomas Porro (g1)
     */
    private BitmapBox computeSkew(Bitmap image){

        //Turns the image into a matrix
        Mat img;
        try{
            img = IPUtils.conversionBitmapToMat(image);
        } catch (ConversionFailedException error){
            Log.e(TAG, error.getErrorMessage());
            return new BitmapBox(image, ProcessingResult.AUTOSKEW_FAILED);
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
        Bitmap rotatedImage = rotateBitmap(image, degreesAngle);
        return new BitmapBox(rotatedImage, ProcessingResult.AUTOSKEW_SUCCESSFUL);
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
     * Detect if the image is bright
     * @param imageMat the image we want to detect the brightness
     * @return IMAGE_IS_OK if image is neither too bright nor too dark,
     *         IMAGE_IS_BRIGHT if image is too bright,
     *         IMAGE_IS_DARK if image is too dark.
     * @author Thomas Porro(g1), Giovanni Fasan(g1), Oscar Garrido (g1)
     */
    private BrightnessValue isBright(Mat imageMat){

        Mat rgbImageMat = new Mat();

        /*
         Changes the format of the matrix into an RGB one, so we are now able to
         split the color with the Core.split method
        */
        Imgproc.cvtColor(imageMat, rgbImageMat, Imgproc.COLOR_RGBA2RGB);

        //Obtain 3 different matrix with the 3 elemental colors

        List<Mat> imageColors = new ArrayList<>();
        Core.split(rgbImageMat, imageColors);


        /*Each color is multiplied with his luminance.
          The colors are in order RGB, so to access the che color I use the number 0, 1, 2 in order
          For more informations see https://en.wikipedia.org/wiki/Relative_luminance
		  */
        final int RED = 0;
        final int GREEN = 1;
        final int BLUE = 2;
        final double RED_LUMINANCE = 0.2126;
        final double GREEN_LUMINANCE = 0.7152;
        final double BLUE_LUMINANCE = 0.0722;
        Mat redLuminance = new Mat();

        Core.multiply(imageColors.get(RED), new Scalar(RED_LUMINANCE), redLuminance);
        Mat greenLuminance= new Mat();
        Core.multiply(imageColors.get(GREEN), new Scalar(GREEN_LUMINANCE), greenLuminance);
        Mat blueLuminance = new Mat();
        Core.multiply(imageColors.get(BLUE), new Scalar(BLUE_LUMINANCE), blueLuminance);

        //Sums the matrix of the colors into a single one
        Mat tempLuminance = new Mat();
        Mat totalLuminance = new Mat();
        Core.add(redLuminance , greenLuminance , tempLuminance); //Red + Green = Temp
        Core.add(tempLuminance , blueLuminance , totalLuminance); //Temp + Blue = Luminance

        //Calculate the sum of the values of all pixels
        Scalar sum = Core.sumElems(totalLuminance);

		/*Calculate the percentage of the brightness. Since the value of the colors go
          from 0 to 255 a pixel can contain the value 255 = 2^8-1*/
        final double PIXEL_MAX_VALUE = (Math.pow(2,8)-1);
        double numberOfBits = PIXEL_MAX_VALUE * rgbImageMat.rows() * rgbImageMat.cols();
        double percentageBrightness = sum.val[0]/numberOfBits;

        Log.d(TAG, "Brightness:"+percentageBrightness);

        /*Bounds to define if the image is dark or bright.
          The values were decided on the basis of various tests*/
        final double UPPER_BOUND = 0.45;
        final double LOWER_BOUND = 0.2;

        if (percentageBrightness > UPPER_BOUND){             //Image is too bright
            return BrightnessValue.IMAGE_TOO_BRIGHT;
        } else if (percentageBrightness < LOWER_BOUND){        //Image is too dark
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
    private BitmapContainer editBright(Bitmap image){
        /*This variable is used to put a limit to the change of the image's brightness.
          The value 240 is derived from the fact that in the for loop we try to modify
          the value of all the pixels of a step, and being the maximum value = 255 (pixel's
          color maximum value, we put the limit on 240*/
        final int STEP = 15;

        //Converts the image into a matrix
        Mat imageMat;
        try{
            imageMat = IPUtils.conversionBitmapToMat(image);
        } catch (ConversionFailedException error){
            Log.e(TAG, error.getErrorMessage());
            return new BitmapBox(image, ProcessingResult.BRIGHTNESS_CONVERSION_ERROR);
        }

		/*This variable is used to select the type of matrix we want to abtain in the
          the convertTo method. If it's negative the type doesn't change*/
        final int MATRIX_TYPE = -1;

        /*This variable is used to change the contrast of the matrix, but we want
          only modify the brightness so we put the value 1 because the method use
          this formula from the documentation:
          m(x,y) = saturate _ cast<rType>(alpha(*this)(x,y) + beta)
          We called beta as STEP*/
        final int ALPHA = 1;

        //Call the internal method isBright to detect if the image is bright or dark
        //and change the brightness according to the number obtained
        while(isBright(imageMat) != BrightnessValue.IMAGE_IS_OK) {
            switch (isBright(imageMat)) {
                case IMAGE_TOO_BRIGHT:
                    Log.d(TAG, "Case==IMAGE_TOO_BRIGHT");
                    /*Modify the values of all pixels with an alpha and beta value following
                      this formula m(x,y) = saturate _ cast<rType>(alpha(*this)(x,y) + beta)*/
                    imageMat.convertTo(imageMat, MATRIX_TYPE, ALPHA, -STEP);
                    break;
                case IMAGE_TOO_DARK:
                    Log.d(TAG, "Case==IMAGE_TOO_DARK");
                    //The same as above
                    imageMat.convertTo(imageMat, MATRIX_TYPE, ALPHA, STEP);
                    break;
            }
        }
        Log.d(TAG, "IMAGE_IS_OK");
        try {
            Bitmap convertedImage = IPUtils.conversionMatToBitmap(imageMat);
            return new BitmapBox(convertedImage, ProcessingResult.BRIGHTNESS_MODIFIED);
        } catch (ConversionFailedException error) {
            Log.e(TAG, error.getErrorMessage());
            return new BitmapBox(image, ProcessingResult.BRIGHTNESS_CONVERSION_ERROR);
        }
    }


    /**
     * @author Thomas Porro(g1), Oscar Garrido (g1), Giovanni Fasan(g1), Leonardo Pratesi(g1)
     * See PreProcessingMethods.java
     */
    @Override
    public BlurValue isBlurred(Bitmap image) {

        //Total number of color of RGB: 256 each color, so 256^3
        int maxLap = -16777216;

        //Threshold above which the color is out of focus
        final int OUT_OF_FOCUS_THRESHOLD = -6118750;

        //Converts the image into a matrix
        Mat imageMat;
        try {
            imageMat = IPUtils.conversionBitmapToMat(image);
        } catch (ConversionFailedException error){
            Log.e(TAG, error.getErrorMessage());
            return BlurValue.IMAGE_NOT_ANALYZED;
        }

        //Turn the colored matrix into a grayscale matrix
        Mat grayImageMat = new Mat();
        Imgproc.cvtColor(imageMat, grayImageMat, Imgproc.COLOR_BGR2GRAY);

        /*Use the openCV's Laplacian methods to apply a Laplacian filter, that allow us to detect
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
            return BlurValue.IMAGE_NOT_ANALYZED;
        }

        //Extracts all the pixels of the laplacian image into the array
        int[] pixels = IPBuilder.doGetPixels(new IPBuilder.GetPixelsBuilder(laplacianImage)
                .withStride(laplacianImage.getWidth())
                .withWidth(laplacianImage.getWidth())
                .withHeight(laplacianImage.getHeight())
        );

        //Searches the maximum value of the pixels in the Laplacin filtered image
        for(int pixel : pixels){
            if(pixel > maxLap){
                maxLap = pixel;
            }
        }

        //Verify if the image is blurred
        if(maxLap < OUT_OF_FOCUS_THRESHOLD){
            Log.d("Blur", "IS BLURRED");
            return BlurValue.IMAGE_BLURRED;
        } else {
            Log.d("Blur", "IS NOT BLURRED");
            return BlurValue.IMAGE_NOT_BLURRED;
        }
    }


    /**
     * @author Thomas Porro (g1), Giovanni Fasan (g1), Oscar Garrido (g1)
     * See PreProcessingMethods.java
     */
    @Override
    public BitmapContainer doImageProcessing(Bitmap image, boolean autoSkew) {
        //Call methods that perform the image processing
        BitmapContainer modifiedBright = editBright(image);
        if(autoSkew) {
            modifiedBright = computeSkew(modifiedBright.getFirstBitmap());
        }
        return modifiedBright;
    }
}
