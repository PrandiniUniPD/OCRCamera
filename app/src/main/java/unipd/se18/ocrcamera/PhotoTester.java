package unipd.se18.ocrcamera;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;


/**
 * Class built to test the application's OCR
 * @author Luca Moroldo (g3)
 */
public class PhotoTester {

    public static final String[] IMAGE_EXTENSIONTS = {"jpeg", "jpg"};
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


        File directory = new File(this.dirPath);


        for (File file : directory.listFiles()) {

            Bitmap photoBitmap = null;
            String photoDesc = null;

            String filePath = file.getPath();
            String fileExtension = Utils.getFileExtension(filePath);

            if(Arrays.asList(IMAGE_EXTENSIONTS).contains(fileExtension)) {
                photoBitmap = Utils.loadBitmap(filePath);
            } else if(fileExtension.equals("txt")) {
                photoDesc = Utils.getDescription(filePath);
            }

            String extractedText = extractText(photoBitmap);

            //TODO get ingredients from description - NB the description is in JSON format
            String photoIngredients = "";

            int confidence = compareTexts(extractedText, photoIngredients);

            //TODO build report

        }

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


    //TODO define how to campare the two strings and how to evaluate the compare - a possible solution may be a % normalized in 0 to 100
    /**
     *
     * @param text1
     * @param text2
     * @return a value representing the confidence
     */
    private int compareTexts(String text1, String text2) {
        return 0;
    }






}
