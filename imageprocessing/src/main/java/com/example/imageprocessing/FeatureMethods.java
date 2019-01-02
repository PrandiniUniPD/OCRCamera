package com.example.imageprocessing;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.imageprocessing.exceptions.ConversionFailedException;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.CvType.CV_8U;
import static org.opencv.core.CvType.CV_8UC1;

/**
 * Detect if the image is blurred
 * @author Thomas Porro (g1)
 */
public class FeatureMethods implements ImageProcessingMethods {
    private final String TAG = "FeatureMethods";

    @Override
    public boolean blurDetection(Bitmap image) {
        //Converts the image into a matrix
        Mat imageMat;
        try {
            imageMat = IPUtils.conversionBitmapToMat(image);
        } catch (ConversionFailedException error){
            Log.d(TAG, error.getErrorMessage());
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

        //Obtain the value of all the pixels from the matrix
        long dimension = laplacianMat8Bit.total()*laplacianMat8Bit.channels();
        byte[] matrixBuffer = new byte[(int)dimension];
        laplacianMat8Bit.get(0,0, matrixBuffer);

        int maxLap = -16777216;
        for(int value : matrixBuffer){
            if(value > maxLap){
                maxLap = value;
            }
        }

        int threshold = 180;

        return maxLap < threshold;
    }
}
