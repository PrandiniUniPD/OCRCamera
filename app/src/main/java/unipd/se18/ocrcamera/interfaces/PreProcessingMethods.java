package com.example.imageprocessing.interfaces;

import android.graphics.Bitmap;
import com.example.imageprocessing.enumClasses.BlurValue;

/**
 * Interface used to call the processing methods
 * @author Thomas Porro (g1), Giovanni Fasan(g1), OScar Garrido(g1)
 */
public interface PreProcessingMethods {

    /**
     * Detect if the image is blurred
     * @param image The image we want to discover if is blurred
     * @return BlurValue type that contains the information of the image
     * @author Thomas Porro (g1), Giovanni Fasan (g1), Oscar Garrido (g1)
     */
    BlurValue isBlurred(Bitmap image);

    /**
     * Does the image processing brightness adjustment and, if wanted to, it also auto rotates the image
     * @param image The image to modify
     * @param autoSkew auto rotates the image if true, does nothing if false
     * @return a Bitmap with adjustment, auto cropping the image and the likes
     * @author Thomas Porro (g1), Giovanni Fasan (g1), Oscar Garrido (g1)
     */
     BitmapContainer doImageProcessing(Bitmap image, boolean autoSkew);

}
