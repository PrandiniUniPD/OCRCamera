package unipd.se18.ocrcamera;

import android.graphics.Bitmap;

public class BlurObject {
    Bitmap image;
    double blur;

    public BlurObject(Bitmap image, double blur)
    {
        this.image= image;
        this.blur=blur;
    }

    public BlurObject(Bitmap image)
    {
        this.image= image;
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


}
