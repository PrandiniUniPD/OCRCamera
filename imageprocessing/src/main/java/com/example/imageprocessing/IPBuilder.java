package com.example.imageprocessing;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder used to pass to openCV's methods the parameters
 * @author Thomas Porro (g1), Oscar Garrido (g1)
 */
class IPBuilder {

    /**
     * Inner class to create an object CannyBuilder that contains
     * all the variables needed to Imgproc.Canny method
     * @author Thomas Porro (g1)
     */
    private static class CannyBuilder{

        private Mat source;
        private double minThreshold;
        private double maxThreshold;
        private int apertureSize;
        private boolean l2gradient;

        /**
         * Constructor that initialize the variables of the object
         * with a default value
         * @param src the source matrix
         * @author Thomas Porro (g1)
         */
        private CannyBuilder(Mat src){
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
         * @author Thomas Porro (g1)
         */
        private CannyBuilder withMinThreshold(double value){
            this.minThreshold = value;
            return this;
        }

        /**
         * Set maxThreshold with the passed value
         * @param value the value you want it to take maxThreshold
         * @return returns the current object instance
         * @author Thomas Porro (g1)
         */
        private CannyBuilder withMaxThreshold(double value){
            this.maxThreshold = value;
            return this;
        }

        /**
         * Set ApertureSize with the passed value
         * @param value the value you want it to withApertureSize
         * @return returns the current object instance
         * @author Thomas Porro (g1)
         */
        private CannyBuilder withApertureSize(int value){
            this.apertureSize = value;
            return this;
        }

        /**
         * Set l2gradient with the passed value
         * @param value the value you want it to l2gradient
         * @return returns the current object instance
         * @author Thomas Porro (g1)
         */
        private CannyBuilder withL2gradient(boolean value){
            this.l2gradient = value;
            return this;
        }
    }




    /**
     * Inner class to create an object adaptiveThresholdBuilder that contains
     * all the variables needed to Imgproc.adaptiveThreshold
     * @author Thomas Porro (g1)
     */
    private static class AdaptiveThresholdBuilder{
        private Mat source;
        private double maxThreshold;
        private int blockSize;
        private double constant;

        /**
         * Constructor that initialize the variables of the object
         * with a default value
         * @param src the source matrix
         * @author Thomas Porro (g1)
         */
        private AdaptiveThresholdBuilder(Mat src){
            this.source = src;
            this.maxThreshold = 200;
            this.blockSize = 3;
            this.constant = 0;
        }

        /**
         * Set maxThreshold with the passed value
         * @param value the value you want it to maxThreshold
         * @return returns the current object instance
         * @author Thomas Porro (g1)
         */
        private AdaptiveThresholdBuilder withMaxThreshold(double value){
            this.maxThreshold = value;
            return this;
        }

        /**
         * Set blockSize with the passed value
         * @param value the value you want it to blockSize
         * @return returns the current object instance
         * @author Thomas Porro (g1)
         */
        private AdaptiveThresholdBuilder withBlockSize(int value){
            this.blockSize = value;
            return this;
        }

        /**
         * Set constant with the passed value
         * @param value the value you want it to withApertureSize
         * @return returns the current object instance
         * @author Thomas Porro (g1)
         */
        private AdaptiveThresholdBuilder withConstant(double value){
            this.constant = value;
            return this;
        }
    }



    /**
     * Inner class to create an object FindContoursBuilder that contains
     * all the variables needed to Imgproc.findContours
     * @author Oscar Garrido (g1)
     */
    private static class FindContoursBuilder{
        private Mat source;
        private int mode;
        private int method;

        /**
         * Constructor that initialize the variables of the object
         * with a default value
         * @param src the source matrix
         * @author Oscar Garrido (g1)
         */
        private FindContoursBuilder(Mat src){
            this.source = src;
            this.mode = 0;
            this.method = 1;
        }

        /**
         * Set mode with the passed value
         * @param value the value you want it to take mode
         * @return returns the current object instance
         * @author Oscar Garrido (g1)
         */
        private FindContoursBuilder withMode(int value){
            this.mode = value;
            return this;
        }

        /**
         * Set method with the passed value
         * @param value the value you want it to take method
         * @return returns the current object instance
         * @author Oscar Garrido (g1)
         */
        private FindContoursBuilder withMethod(int value){
            this.mode = value;
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
    public Mat doCanny(CannyBuilder builder){
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
    public Mat doAdaptiveThreshold(AdaptiveThresholdBuilder builder) {
        Mat destination = new Mat();
        Imgproc.adaptiveThreshold(builder.source, destination, builder.maxThreshold,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, builder.blockSize,
                builder.constant);
        return destination;
    }


    /**
     * Applies the openCv's method Imagproc.findContours, that applies a threshold
     * to an image
     * @param builder the AdaptiveThresholdBuilder that contains the parameters of the
     *                Imageproc.adaptiveThreshold method
     * @return the matrix that contains the result of Imageproc.adaptiveThreshold
     * @author Oscar Garrido (g1)
     */
    public List<MatOfPoint> doFindContours(FindContoursBuilder builder) {
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(builder.source, contours, new Mat(), builder.mode, builder.method);
        //The third parameter contains additional information that is unused
        return contours;
    }

}
