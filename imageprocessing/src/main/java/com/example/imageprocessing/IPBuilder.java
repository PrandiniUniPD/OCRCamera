package com.example.imageprocessing;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfInt4;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder used to pass to openCV's methods the parameters
 * @author Thomas Porro (g1), Oscar Garrido (g1)
 */
class IPBuilder {

    /*
        Documentation of the Imgproc class available at:
        https://docs.opencv.org/java/2.4.2/org/opencv/imgproc/Imgproc.html

        We referenced a previous instance of the documentation since
        the newer one is still incomplete
    */

    /**
     * Inner class to create an object CannyBuilder that contains
     * all the variables needed from Imgproc.Canny method
     * @author Thomas Porro (g1)
     */
    static class CannyBuilder{

        private Mat source;
        private double minThreshold;
        private double maxThreshold;
        private int apertureSize;
        private boolean l2gradient;

        /**
         * Constructor that initialize the variables of the object
         * with a default value
         * @param src the source matrix
         */
        CannyBuilder(Mat src){
            this.source = src;
            this.minThreshold = 50;
            this.maxThreshold = 200;
            this.apertureSize = 3;
            this.l2gradient = false;
        }

        /**
         * Set minThreshold with the passed value
         * @param value the value you want it to take minThreshold
         * @return returns the current object instance
         */
        CannyBuilder withMinThreshold(double value){
            this.minThreshold = value;
            return this;
        }

        /**
         * Set maxThreshold with the passed value
         * @param value the value you want it to take maxThreshold
         * @return returns the current object instance
         */
        CannyBuilder withMaxThreshold(double value){
            this.maxThreshold = value;
            return this;
        }

        /**
         * Set ApertureSize with the passed value
         * @param value the value you want it to withApertureSize
         * @return returns the current object instance
         */
        CannyBuilder withApertureSize(int value){
            this.apertureSize = value;
            return this;
        }

        /**
         * Set l2gradient with the passed value
         * @param value the value you want it to l2gradient
         * @return returns the current object instance
         */
        CannyBuilder withL2gradient(boolean value){
            this.l2gradient = value;
            return this;
        }
    }

    /**
     * Inner class to create an object adaptiveThresholdBuilder that contains
     * all the variables needed from Imgproc.adaptiveThreshold
     */
    static class AdaptiveThresholdBuilder{
        private Mat source;
        private double maxThreshold;
        private int blockSize;
        private double constant;

        /**
         * Constructor that initialize the variables of the object
         * with a default value
         * @param src the source matrix
         */
        AdaptiveThresholdBuilder(Mat src){
            this.source = src;
            this.maxThreshold = 200;
            this.blockSize = 3;
            this.constant = 0;
        }

        /**
         * Set maxThreshold with the passed value
         * @param value the value you want it to maxThreshold
         * @return returns the current object instance
         */
        AdaptiveThresholdBuilder withMaxThreshold(double value){
            this.maxThreshold = value;
            return this;
        }

        /**
         * Set blockSize with the passed value
         * @param value the value you want it to blockSize
         * @return returns the current object instance
         */
        AdaptiveThresholdBuilder withBlockSize(int value){
            this.blockSize = value;
            return this;
        }

        /**
         * Set constant with the passed value
         * @param value the value you want it to withApertureSize
         * @return returns the current object instance
         */
        AdaptiveThresholdBuilder withConstant(double value){
            this.constant = value;
            return this;
        }
    }



    /**
     * Inner class to create an object FindContoursBuilder that contains
     * all the variables needed from Imgproc.findContours
     * @author Oscar Garrido (g1)
     */
    static class FindContoursBuilder{
        private Mat source;
        private int mode;
        private int method;

        /**
         * Constructor that initialize the variables of the object
         * with a default value
         * @param src the source matrix
         */
        FindContoursBuilder(Mat src){
            this.source = src;
            this.mode = Imgproc.RETR_EXTERNAL;
            this.method = Imgproc.CHAIN_APPROX_SIMPLE;
        }

        /**
         * Set mode with the passed value
         * @param value the value you want it to take mode
         * @return returns the current object instance
         */
        FindContoursBuilder withMode(int value){
            this.mode = value;
            return this;
        }

        /**
         * Set method with the passed value
         * @param value the value you want it to take method
         * @return returns the current object instance
         */
        FindContoursBuilder withMethod(int value){
            this.method = value;
            return this;
        }
    }

    /**
     * Inner class to create an object HoughLinesPBuilder that contains
     * all the variables needed from Imgproc.HoughLinesP
     * @author Oscar Garrido (g1)
     */
    static class HoughLinesPBuilder{
        private Mat source;
        private double rho;
        private double theta;
        private int threshold;
        private double minLineLength;
        private double maxLineGap;

        /**
         * Constructor that initialize the variables of the object
         * with a default value
         * @param img the source matrix
         */
        HoughLinesPBuilder(Mat img){
            this.source = img;
            this.rho = 1;
            this.theta = Math.PI / 180;
            this.threshold = 50;
            this.minLineLength = 50;
            this.maxLineGap = 10;
        }

        /**
         * Set rho with the passed value
         * @param value the value you want it to rho
         * @return returns the current object instance
         */
        HoughLinesPBuilder withRho(double value){
            this.rho = value;
            return this;
        }

        /**
         * Set theta with the passed value
         * @param value the value you want it to theta
         * @return returns the current object instance
         */
        HoughLinesPBuilder withTheta(double value){
            this.theta = value;
            return this;
        }

        /**
         * Set threshold with the passed value
         * @param value the value you want it to threshold
         * @return returns the current object instance
         */
        HoughLinesPBuilder withThreshold(int value){
            this.threshold = value;
            return this;
        }

        /**
         * Set minLineLength with the passed value
         * @param value the value you want it to minLineLength
         * @return returns the current object instance
         */
        HoughLinesPBuilder withMinLineLength(double value){
            this.minLineLength = value;
            return this;
        }

        /**
         * Set maxLineGap with the passed value
         * @param value the value you want it to maxLineGap
         * @return returns the current object instance
         */
        HoughLinesPBuilder withMaxLineGap(double value){
            this.maxLineGap = value;
            return this;
        }
    }

    /**
     * Inner class to create an object GetPixelsBuilder that contains
     * all the variables needed from getPixels of android.graphics.Bitmap
     * @author Oscar Garrido (g1)
     */
    static class GetPixelsBuilder{
        private Bitmap source;
        private int offset;
        private int stride;
        private int x;
        private int y;
        private int width;
        private int height;

        /**
         * Constructor that initialize the variables of the object
         * with a default value
         * @param bmp the source image
         */
        GetPixelsBuilder(Bitmap bmp){
            this.source = bmp;
            this.offset = 0;
            this.stride = 0;
            this.x = 0;
            this.y = 0;
            this.width = 0;
            this.height = 0;
        }

        /**
         * Set offset with the passed value
         * @param value the value you want it to offset
         * @return returns the current object instance
         */
        GetPixelsBuilder withOffset(int value){
            this.offset = value;
            return this;
        }

        /**
         * Set stride with the passed value
         * @param value the value you want it to stride
         * @return returns the current object instance
         */
        GetPixelsBuilder withStride(int value){
            this.stride = value;
            return this;
        }

        /**
         * Set x with the passed value
         * @param value the value you want it to x
         * @return returns the current object instance
         */
        GetPixelsBuilder withX(int value){
            this.x = value;
            return this;
        }

        /**
         * Set y with the passed value
         * @param value the value you want it to y
         * @return returns the current object instance
         */
        GetPixelsBuilder withY(int value){
            this.y = value;
            return this;
        }

        /**
         * Set width with the passed value
         * @param value the value you want it to width
         * @return returns the current object instance
         */
        GetPixelsBuilder withWidth(int value){
            this.width = value;
            return this;
        }

        /**
         * Set height with the passed value
         * @param value the value you want it to height
         * @return returns the current object instance
         */
        GetPixelsBuilder withHeight(int value){
            this.height = value;
            return this;
        }
    }

    /**
     * Applies the openCV's method Imageproc.Canny, that detects the edges of an image
     * @param builder the CannyBuilder that contains the parameters of the
     *                Imageproc.Canny method
     * @return the matrix that contains the result of Imageproc.Canny
     * @author Thomas Porro (g1)
     */
    static Mat doCanny(CannyBuilder builder){
        Mat destination = new Mat();
        Imgproc.Canny(builder.source, destination, builder.minThreshold, builder.maxThreshold,
                builder.apertureSize, builder.l2gradient);
        return destination;
    }


    /**
     * Applies the openCv's method Imagproc.adaptiveThreshold, that applies a threshold
     * to an image
     * @param builder the AdaptiveThresholdBuilder that contains the parameters of the
     *                Imageproc.adaptiveThreshold method
     * @return the matrix that contains the result of Imageproc.adaptiveThreshold
     * @author Thomas Porro (g1)
     */
    static Mat doAdaptiveThreshold(AdaptiveThresholdBuilder builder) {
        Mat destination = new Mat();
        Imgproc.adaptiveThreshold(builder.source, destination, builder.maxThreshold,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, builder.blockSize,
                builder.constant);
        return destination;
    }


    /**
     * Applies the openCv's method Imagproc.findContours, that finds
     * contours in a binary image
     * @param builder the FindContoursBuilder that contains the parameters of the
     *                Imageproc.findContours method
     * @return the matrix that contains the result of Imageproc.adaptiveThreshold
     * @author Oscar Garrido (g1)
     */
    static List<MatOfPoint> doFindContours(FindContoursBuilder builder) {
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(builder.source, contours, new Mat(), builder.mode, builder.method);
        //The third parameter contains additional information that is unused
        return contours;
    }

    /**
     * Applies the openCv's method Imagproc.HoughLinesP, that finds line segments in a
     * binary image using the probabilistic Hough transform
     * @param builder the HoughLinesPBuilder that contains the parameters of the
     *                Imageproc.HoughLinesP method
     * @return the matrix that contains the result of Imageproc.HoughLinesP
     * @author Oscar Garrido (g1)
     */
    static MatOfInt4 doHoughLinesP(HoughLinesPBuilder builder){
        MatOfInt4 lines = new MatOfInt4();
        Imgproc.HoughLinesP(builder.source, lines, builder.rho, builder.theta,
                builder.threshold, builder.minLineLength, builder.maxLineGap);
        return lines;
    }

    /**
     * Applies the android's Bitmap method getPixels, that gets the value of pixels into an array
     * @param builder the GetPixelsBuilder that contains the parameters of the
     *                getPixels method
     * @return an array containing a copy of the data in the bitmap
     * @author Oscar Garrido (g1)
     */
    static int[] doGetPixels(GetPixelsBuilder builder){
      int[] pixels = new int[builder.source.getHeight() * builder.source.getWidth()];
      builder.source.getPixels(pixels, builder.offset, builder.stride, builder.x, builder.y,
              builder.width, builder.height);
      return pixels;
      }
}
