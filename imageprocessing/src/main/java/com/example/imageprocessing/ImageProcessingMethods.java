package com.example.imageprocessing;

import android.graphics.Bitmap;

/**
 * Interface used to call the processing methods
 * @author Thomas Porro (g1)
 */
public interface ImageProcessingMethods {

    /**
     * Detect if the image is blurred
     * @param image The image we want to discover if is blurred
     * @return True if the image is blurred. False otherwise or if the detection failed
     * @author Thomas Porro (g1)
     */
    boolean isBlurred(Bitmap image);

    /**
     * Change the brightness of the image into an optimal one
     * @param image the image we want to modify the brightness
     * @return the image with the modified brightness
     * @author Thomas Porro (g1)
     */
    Bitmap editBright(Bitmap image);
}
