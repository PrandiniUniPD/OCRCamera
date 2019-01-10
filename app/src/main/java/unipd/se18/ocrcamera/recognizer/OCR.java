package unipd.se18.ocrcamera.recognizer;

import android.graphics.Bitmap;

/**
 * Interface useful to avoid the single point of failure about the OCR recognizing text
 * @author Commonly decided by all the groups
 */
public interface OCR {
    /**
     * Launches the text recognizing process from a given image.
     * See OCRListener.java of this package for retrieving the output of this process.
     * @param img The image in the Bitmap format
     * @author Commonly decided by all the groups, modified by a suggestion from the doctor Li Daohong
     */
    void getTextFromImg(Bitmap img);
}
