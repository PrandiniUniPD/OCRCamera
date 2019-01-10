package unipd.se18.ocrcamera;

import android.graphics.Bitmap;


/** Class that holds the bitmap, the name , and the blur value calculated
 *  Pratesi Leonardo
 *
 */

public class BlurObject {
    String name;
    Bitmap image;
    double blur;

    public BlurObject(Bitmap image, String name, double blur)
    {
        this.image = image;
        this.name = name;
        this.blur=blur;
    }

    public BlurObject(Bitmap image, String name)
    {
        this.image= image;
        this.name = name;
        this.blur= CameraActivity.blurValue(this.image);
    }

    public Bitmap getImage()
    {
        return image;
    }

    public double getBlur()
    {
        return blur;
    }

    public String toString()
    {
        return Double.toString(blur);
    }


}
