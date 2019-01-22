package unipd.se18.ocrcamera.recognizer;

/**
 * Class useful to set a type of recognizing.
 * It's avoid the single point of failure relative to this process.
 * @author Pietro Prandini (g2) - suggested by the doctor Li Daohong
 */
public class TextRecognizer {
    /**
     * Ids of the ocr recognizers
     */
    public enum Recognizer { mlKit }

    /**
     * Provides an OCR recognizer
     * @param type The id of the recognizing type requested
     * @param textRecognizerListener The listener used to notify the result of the extraction
     * @return The OCR object corresponding to the id of the recognizing type requested
     * @author Pietro Prandini (g2)
     */
    public static OCR getTextRecognizer(Recognizer type, OCRListener textRecognizerListener) {
        switch (type) {
            case mlKit: return new MlKitRecognizer(textRecognizerListener);
            default: return new MlKitRecognizer(textRecognizerListener);
        }
    }
}
