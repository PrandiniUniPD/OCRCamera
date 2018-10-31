package unipd.se18.ocrcamera;

import android.graphics.Bitmap;

/**
 * Implements the common OCR wrapper to retrieve text from an image.
 */
class TextExtractor implements OCRWrapper {

    /**
     * Extract a text from a given image.
     * @param img The image in a Bitmap format
     * @return The String of the text recognized (empty String if nothing is recognized)
     */
    @Override
    public String getTextFromImg(Bitmap img) {
        return "";
    }
}
