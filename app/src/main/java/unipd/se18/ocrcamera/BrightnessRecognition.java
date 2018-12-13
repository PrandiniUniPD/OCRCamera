package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * This class is used to calculate the brightness of an image in Bitmap format
 * @author Pietro Balzan
 */
public class BrightnessRecognition {
    private static final String TAG = "BrightnessRecognition";

    /**
     * This method calculates the brightness of the image and prompts a Toast to the user
     * @param upperBound the toast will be launched if brightness exceeds this value.
     * @param lowerBound the toast will be launched if brightness is lower than this value.
     * @param pixelSkip how many pixels to skip each checked pixel. Higher values result in better
     *                     performance, but a more rough estimate. When pixelSpacing = 1, the method
     *                     actually calculates the real average brightness, not an estimate.
     *                     Do not use values for pixelSpacing that are smaller than 1.
     * @param image the bitmap of the image to be processed
     * @return result int signaling the brightness of the image: 1 if it's too bright,
     *                 -1 if too dark and 0 if the image is good
     * @author Pietro Balzan - some changes by Francesco Pham
     */
    public static int imgBrightness(Bitmap image ,int upperBound, int lowerBound, int pixelSkip){
        int result = 0;                 // result of the recognition, initially set to "good image"
        int width = image.getWidth();
        int height = image.getHeight();
        int R, G, B, pixel;
        int totalPixels = 0;            //total pixels checked
        double totalBrightness=0;         // used to calculate the average brightness

        for(int x = 0; x < width; x+=pixelSkip) {
            for (int y = 0; y < height; y += pixelSkip) {
                // get pixel color
                pixel = image.getPixel(x, y);
                R = Color.red(pixel);       // Red value of the pixel 0<R<255
                G = Color.green(pixel);     // Green value of the pixel 0<G<255
                B = Color.blue(pixel);      // Blue value of the pixel 0<B<255

                //  RGB/Luma conversion formula, dermines "luminance", brightness of a pixel
                double brightness = (0.2126 * R + 0.7152 * G + 0.0722 * B);
                totalBrightness += brightness;
                totalPixels++;
            }
        }
        double average= totalBrightness/totalPixels;   // average brightness

        if (average > upperBound){             // image is too bright
            Log.d(TAG, "too bright image");
            result = 1;
        }
        else if (average < lowerBound){        //image is too dark
            Log.d(TAG, "too dark image");
            result = -1;
        }
        else {
            Log.d(TAG, "good image");     // image is neither too bright nor too dark
        }
        return result;
    }
}