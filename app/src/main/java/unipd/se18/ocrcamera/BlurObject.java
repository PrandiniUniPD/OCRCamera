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

    public BlurObject(String name, Bitmap image, double blur)
    {   this.name=name;
        this.image= image;
        this.blur=blur;
    }

    public BlurObject(String name, Bitmap image)
    {
        this.name=name;
        this.image=image;
        this.blur= CameraActivity.blurValue(this.image);
    }

    public Bitmap getImage()
    {
        return image;
    }

    public String getName() {return name;}

    public double getBlur()
    {
        return blur;
    }

    public String toString()
    {
        return name+" " +Double.toString(blur);
    }


}
