package unipd.se18.ocrcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;

/**
 * This class is used to calculate the brightness of an image in Bitmap format
 * @author Pietro Balzan
 */
public class BrightnessRecognition {
    Handler handler;
    private Context appContext;
    private final Bitmap image;  // the bitmap containing the image to analyze
    private static final String TAG = "BrightnessRecognition";


    /**
     * Constructor of a BrightnessRecognition object containing a Bitmap
     * @param bmp the bitmap of the image to calculate the brightness of
     * @modify image with the new bitmap
     * @author Pietro Balzan
     */

    public BrightnessRecognition(Bitmap bmp, Context c, Handler h){
        appContext= c;
        image = bmp;
        handler= h;
    }

    /**
     * This method calculates the brightness of the image and prompts a Toast to the user
     * @author Pietro Balzan
     */
    public void imgBrightness(){
        int width = image.getWidth();
        int height = image.getHeight();
        int R, G, B, pixel;
        int total_pixels = width*height;
        int UpperBound= 190;
        int LowerBound= 80;
        double tot= 0;

        for(int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = image.getPixel(x, y);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                double brightness = (0.2126 * R + 0.7152 * G + 0.0722 * B); //  RGB/Luma conversion formula, dermines luminance of a pixel
                tot+= brightness;
            }
        }

        final String toobright = "The picture might be too bright";
        final String toodark = "The picture might be too dark";
        double media= tot/total_pixels;

        if (media> UpperBound){
            Log.d(TAG, "too bright image");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(appContext, toobright, Toast.LENGTH_LONG).show();
                }
            });
            // image is too bright
        }
        else if (media < LowerBound){
            Log.d(TAG, "too dark image");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(appContext, toodark, Toast.LENGTH_LONG).show();
                }
            });
            //image is too dark
        }
        else Log.d(TAG, "good image"); // image is neither too bright nor too dark

    }
}