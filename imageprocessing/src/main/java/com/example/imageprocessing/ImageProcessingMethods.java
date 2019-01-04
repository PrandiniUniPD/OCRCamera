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
     * Detect if the image is bright
     * @param image the image we want to detect the brightness
     * @return True if the image is bright. False othwerwise or if the detection failed
     * @author Thomas Porro (g1)
     */
    boolean isBright(Bitmap image);
}
