package com.example.imageprocessing;

import android.graphics.Bitmap;
import android.util.Log;
import com.example.imageprocessing.exceptions.ConversionFailedException;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.CvType.CV_8U;
import static org.opencv.core.CvType.CV_8UC1;

public class PreProcessing extends ImageProcessing implements ImageProcessingMethods {

    private final String TAG = "PreProcessing";

    /**
     * @author Thomas Porro(g1), Oscar Garrido (g1), Giovanni Fasan(g1).
     * See ImageProcessingMethods.java
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

        int[] pixels = new int[laplacianImage.getHeight() * laplacianImage.getWidth()];
        laplacianImage.getPixels(pixels, 0, laplacianImage.getWidth(), 0, 0,
                laplacianImage.getWidth(), laplacianImage.getHeight());

        //Searches the pixel that has the highest colour range in the RGB format
        for(int pixel : pixels){
            if(pixel > maxLap){
                maxLap = pixel;
            }
        }

        if(maxLap < threshold){
            Log.d("Blur", "IS BLURRED");
        } else {
            Log.d("Blur", "IS NOT BLURRED");
        }
        return maxLap < threshold;
    }


    /**
     * @author Thomas Porro (g1), Giovanni Fasan (g1), Oscar GArrido (g1)
     * See ImageProcessingMethods.java
     */
    @Override
    public Bitmap doImageProcessing(Bitmap image) {
        //Call methods that perform the image processing
        Bitmap modifiedBright = editBright(image);
        Bitmap modifiedSkew = editSkew(modifiedBright);
        //TODO verify if we needs this
        Bitmap finalImage = modifiedSkew;
        return finalImage;
    }

}
