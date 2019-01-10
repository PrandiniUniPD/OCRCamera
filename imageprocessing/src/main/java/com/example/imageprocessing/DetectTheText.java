package com.example.imageprocessing;

import android.graphics.Bitmap;
import java.util.List;

/**
 * Interface used to detect the text in an image
 * @author Thomas Porro (g1)
 */
public interface DetectTheText {

    /**
     * Detects all the regions where there some text in the image
     * @param image The image we want to analyze. Not null.
     * @param method The method used to extract the text area. See DetectTheTextMethods.java.
     * @return The TextAreas object that contains the area where there's some text. If it fails
     *         return TextRegions containing the full image
     */
    TextRegions detectTextRegions(Bitmap image, DetectTheTextMethods method);


    /**
     * Extract all the area where the text is detected
     * @param image The image that contains the text. Not null.
     * @param regions The object that contains the area where there's some text
     * @return A list of bitmaps, each containing some text. If it fails return a List containing
     *         only the full image
     */
    List<Bitmap> extractTextFromBitmap(Bitmap image, TextRegions regions);
}
