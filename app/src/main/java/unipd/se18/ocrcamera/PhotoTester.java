package unipd.se18.ocrcamera;


import android.graphics.Bitmap;


/**
 * Class built to test the application's OCR
 * @author Luca Moroldo (g3)
 */
public class PhotoTester {

    /**
     * Stores the path to the directory containing photos and description
     */
    private String dirPath;
    private TestInstance testInstances[];

    /**
     * Load test instances (images + correct ingredients)
     * @param dirPath stores the path to the directory containing photos and description
     */
    public PhotoTester(String dirPath) {
        this.dirPath = dirPath;
        //TODO initialize testinstances
    }

    //TODO think of a better report
    /**
     * @return String with the test's report, each line contains: image name, extracted text, real text, photo tags, notes if present, confidence
     */
    public String testAndReport() {
        //For each test instance apply ocr, compare texts, build report

        return null;
    }

    /**
     * Compare the list of ingredients extracted by OCR and the correct list of ingredients
     * @param correct correct list of ingredients loaded from file
     * @param extracted list of ingredients extracted by the OCR
     * @return percentage of matched words.
     */
    private int ingredientsStringComparison(String correct, String extracted){ return 0;}

    /**
     * @param filename name of a jpeg file to convert to bitmap
     * @return image converted to bitmap
     */
    private Bitmap loadBitmap(String filename) {
        return null;
    }


    /**
     * @param fileName
     * @return String with the text inside the fileName.txt
     */
    private String getIngredientsFromFile(String fileName) {
        return null;
    }

    /**
     *
     * @param bitmap from which the text is extracted
     * @return String - the text extracted
     */
    private String executeOcr(Bitmap bitmap) {
        return null;
    }



}
