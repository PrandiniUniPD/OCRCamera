package unipd.se18.ocrcamera;


import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Class built to test the application's OCR
 * @author Luca Moroldo (g3) - Francesco Pham (g3)
 */
public class PhotoTester {

    public static final String[] IMAGE_EXTENSIONS = {"jpeg", "jpg"};

    private static final String TAG = "PhotoTester";

    private ArrayList<TestInstance> testInstances = new ArrayList<TestInstance>();



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

                String photoDesc= Utils.getTextFromFile(dirPath + "/" + fileName + ".txt");

                //create test instance giving filename, description and bitmap
                try {
                    JSONObject jsonPhotoDescription = new JSONObject(photoDesc);
                    testInstances.add(new TestInstance(photoBitmap, jsonPhotoDescription, fileName));
                } catch(JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error decoding JSON");
                }
            }
        }
    }




    /**
     * @return String in JSON format with the test's report, each object is named with the filename and contains:
     * ingrediens, tags, notes, original photo name, confidence
     */
    public String testAndReport() {


        JSONObject jsonReport = new JSONObject();

        //For each test instance apply ocr, compare texts, build report
        for(TestInstance test : testInstances){


            try {

                //evaluate text extraction
                String extractedIngredients = executeOcr(test.getPicture());
                String[] correctIngredients = test.getIngredientsArray();
                int confidence = ingredientsTextComparison(correctIngredients, extractedIngredients);

                //insert test in report
                test.setConfidence(confidence);

                jsonReport.put(test.getFileName(), test.getJsonObject());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonReport.toString();

    }



    /**
     * Compare the list of ingredients extracted by OCR and the correct list of ingredients
     * @param correct correct list of ingredients loaded from file
     * @param extracted list of ingredients extracted by the OCR
     * @return percentage of matched words.
     */
    private int ingredientsTextComparison(String[] correct, String extracted){
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
     * @author Francesco Pham - Luca Moroldo
     */
    private class TestInstance {

        private Bitmap picture;
        private JSONObject jsonObject;
        private String fileName;

        public TestInstance(Bitmap picture, JSONObject jsonObject, String fileName) {
            this.picture = picture;
           this.jsonObject = jsonObject;
            this.fileName = fileName;
        }

        public String[] getIngredientsArray() throws JSONException {
            String ingredients = jsonObject.getString("ingredients");
            String[] ingredientsArr = ingredients.trim().split("\\s*,\\s*"); //split removing whitespaces
            return ingredientsArr;
        }
        public String getIngredients() throws  JSONException {
            String ingredients = jsonObject.getString("ingredients");
            return ingredients;
        }

        public String[] getTags() throws JSONException {
            return Utils.getStringArrayFromJSON(jsonObject, "tags");
        }

        public Bitmap getPicture() {
            return picture;
        }

        public String getFileName() {
            return fileName;
        }

        public String getNotes() throws JSONException {
            return jsonObject.getString("notes");
        }

        @Override
        public String toString() {
            return jsonObject.toString();
        }

        public JSONObject getJsonObject() {
            return jsonObject;
        }

        public void setConfidence(int confidence) throws JSONException {
            jsonObject.put("confidence", confidence);
        }
    }
}
