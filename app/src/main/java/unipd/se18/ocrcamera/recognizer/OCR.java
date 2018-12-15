package unipd.se18.ocrcamera.recognizer;

import android.graphics.Bitmap;

public interface OCR {
    /**
     * Extract a text from a given image.
     * @param img The image in the Bitmap format
     * @return The String of the text recognized (empty String if nothing is recognized)
     */
    String getTextFromImg(Bitmap img);
}
