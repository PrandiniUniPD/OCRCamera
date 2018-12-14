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
     * @return The TextAreas object that contains the area where there's some text
     */
    TextRegions detectTextRegions(Bitmap image);


    /**
     * Extract all the area where the text is detected
     * @param image The image that contains the text. Not null.
     * @param regions The object that contains the area where there's some text
     * @return A list of bitmaps, each containing some text
     */
    List<Bitmap> extractTextFromBitmap(Bitmap image, TextRegions regions);
}
