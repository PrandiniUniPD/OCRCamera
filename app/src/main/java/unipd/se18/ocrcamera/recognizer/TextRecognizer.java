package unipd.se18.ocrcamera.recognizer;

/**
 * Class useful to choose a type of recognizing.
 * It's avoid the single point of failure relative to this process.
 */
public class TextRecognizer {
    /**
     * Ids of the ocr recognizers
     */
    public static enum Recognizer { mlKit }

    /**
     * Provides an OCR recognizer
     * @param type The id of the recognizing type requested
     * @return The OCR object corresponding to the id of the recognizing type requested
     * @author Pietro Prandini (suggested by the doctor Li Daohong)
     */
    public static OCR getRecognizer(Recognizer type) {
        switch (type) {
            case mlKit:
                return new MlKitRecognizer();

            default:
                return new MlKitRecognizer();
        }
    }
}
