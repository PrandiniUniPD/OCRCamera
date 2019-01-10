package com.example.imageprocessing;

import android.graphics.Bitmap;

/**
 * Interface used to call the processing methods
 * @author Thomas Porro (g1), Giovanni Fasan(g1), OScar Garrido(g1)
 */
public interface PreProcessingMethods {

    /**
     * Detect if the image is blurred
     * @param image The image we want to discover if is blurred
     * @return True if the image is blurred. False otherwise or if the detection failed
     * @author Thomas Porro (g1), Giovanni Fasan (g1), Oscar Garrido (g1)
     */
    boolean isBlurred(Bitmap image);

    /**
     * Do all the image processing, like brightness adjustment, auto cropping the image and the likes
     * @param image The image to modify
     * @return a Bitmap with adjustment, auto cropping the image and the likes
     * @author Thomas Porro (g1), Giovanni Fasan (g1), Oscar Garrido (g1)
     */
     Bitmap doImageProcessing(Bitmap image);
}
