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

    /**
     * @param dirPath stores the path to the directory containing photos and description
     */
    public PhotoTester(String dirPath) {
        this.dirPath = dirPath;
    }

    //TODO think of a better report
    /**
     * @return String with the test's report, each line contains: image name, extracted text, real text, photo tags, notes if present, confidence
     */
    public String testAndReport() {
        //For each file.jpeg in directory, load it and convert it to bitmap, get the description, apply ocr, compare texts, build report
        //TODO define how to campare the two strings and how to evaluate the compare

        return null;
    }

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
    private String getDescription(String fileName) {
        return null;
    }

    /**
     *
     * @param bitmap from which the text is extracted
     * @return String - the text extracted
     */
    private String extractText(Bitmap bitmap) {
        return null;
    }





}
