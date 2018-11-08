package unipd.se18.ocrcamera;


import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Class built to test the application's OCR
 * @author Luca Moroldo (g3) - Francesco Pham (g3)
 */
public class PhotoTester {

    public static final String[] IMAGE_EXTENSIONS = {"jpeg", "jpg"};

    private static final String TAG = "PhotoTester";

    private ArrayList<TestInstance> testInstances;



    /**
     * Load test instances (images + description)
     * @param dirPath stores the path to the directory containing photos and description
     */
    public PhotoTester(String dirPath) {
        File directory = new File(dirPath);


        for (File file : directory.listFiles()) {

            String filePath = file.getPath();
            String fileExtension = Utils.getFileExtension(filePath);
            String fileName = Utils.getFilePrefix(filePath);


            //Each photo has a description.txt with the same filename - so when an image is found we know the description filename
            if(Arrays.asList(IMAGE_EXTENSIONS).contains(fileExtension)) {
                Bitmap photoBitmap = Utils.loadBitmapFromFile(filePath);

                String photoDesc = Utils.getTextFromFile(fileName+".txt");


                //decode JSON and get fields

                try {
                    JSONObject jsonPhotoDescription = new JSONObject(photoDesc);
                    String[] ingredients = Utils.getStringArrayFromJSON(jsonPhotoDescription, "ingredients");
                    String[] tags = Utils.getStringArrayFromJSON(jsonPhotoDescription, "tags");
                    String notes = jsonPhotoDescription.getString("notes");

                    testInstances.add(new TestInstance(photoBitmap, ingredients, tags, notes, fileName));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error decoding JSON");
                }

            }
        }
    }




    /**
     * @return String with the test's report, each line contains: image name, extracted text,
     *  real text, photo tags, notes if present, confidence
     */
    public String testAndReport() {

        //For each test instance apply ocr, compare texts, build report
        for(TestInstance test : testInstances){

            String extractedIngredients = executeOcr(test.getPicture());
            String[] correctIngredients = test.getIngredients();

            int confidence = ingredientsStringComparison(correctIngredients, extractedIngredients);

            String[] tags = test.getTags();
            String notes = test.getNotes();
            String filename = test.getFileName();

            //TODO generate report

        }

        return null;
    }



    /**
     * Compare the list of ingredients extracted by OCR and the correct list of ingredients
     * @param correct correct list of ingredients loaded from file
     * @param extracted list of ingredients extracted by the OCR
     * @return percentage of matched words.
     */
    private int ingredientsStringComparison(String[] correct, String extracted){
        return 0;
    }


    /**
     *
     * @param bitmap from which the text is extracted
     * @return String - the text extracted
     */
    private String executeOcr(Bitmap bitmap) {
        return null;
    }


    /**
     * Class that contains a single test instance
     * @author Francesco Pham
     */
    private class TestInstance {
        private String[] ingredients;
        private String[] tags;
        private Bitmap picture;
        private String notes;
        private String fileName;


        public TestInstance(Bitmap picture, String[] ingredients, String[] tags, String notes, String filename) {
            this.picture = picture;
            this.ingredients = ingredients;
            this.tags = tags;
            this.notes = notes;
            this.fileName = filename;

        }

        public String[] getIngredients() {
            return this.ingredients;
        }

        public String[] getTags() { return this.tags; }

        public Bitmap getPicture() {
            return this.picture;
        }

        public String getFileName() {
            return fileName;
        }

        public String getNotes() {
            return this.notes;

        }

    }
}
