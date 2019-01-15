package unipd.se18.textrecognizer;

/**
 * Listener useful to control the output of the OCR processing
 * @author Pietro Prandini (g2) - suggested by the doctor Li Daohong
 */
public interface OCRListener {
    /**
     * Code of extraction failure
     */
    int FAILURE = 0;

    /**
     * Code of the bitmap is null failure
     */
    int BITMAP_IS_NULL_FAILURE = 1;

    /**
     * Method called when an extraction is successfully completed.
     * @param text The String of the text recognized (empty String if nothing is recognized)
     */
    void onTextRecognized(String text);

    /**
     * Method called when an extraction is failed.
     * @param code The code of the extraction error
     */
    void onTextRecognizedError(int code);
}
