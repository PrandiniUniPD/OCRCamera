package unipd.se18.ocrcamera;

import android.graphics.Bitmap;

interface OCRWrapper {
    /**
     * Wrapper for OCR libraries. Extract a text from a given image.
     * @param img The image in a Bitmap format
     * @return The String of the text recognized (empty String if nothing is recognized)
     */
    String getTextFromImg(Bitmap img);
}
