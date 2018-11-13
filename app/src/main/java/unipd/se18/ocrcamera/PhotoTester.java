package unipd.se18.ocrcamera;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
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
     * @param environment environment where find the directory with dirName
     * @param dirName stores the path to the directory containing photos and description
     */
    public PhotoTester(File environment, String dirName) {
        File directory = getStorageDir(environment, dirName);
        String dirPath = directory.getPath();
        Log.v(TAG, "PhotoTester -> dirPath == " + dirPath);

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
     * Get a File directory from an environment
     * @param environment parent environment
     * @param dirName name of the directory
     * @return the file relative to the environment and the dirName
     * @author Pietro Prandini (g2)
     */
    private File getStorageDir(File environment, String dirName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(environment, dirName);
        if(!file.isDirectory()) {
            Log.e(TAG, file.getAbsolutePath() + "It's not a directory");
        } else {
            Log.v(TAG, "Directory => " + file.getAbsolutePath());
        }
        return file;
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
     * @author Francesco Pham
     */
    private int ingredientsTextComparison(String[] correct, String extracted){

        extracted = extracted.toLowerCase();
        String[] extractedWords = extracted.trim().split("\\s*,\\s*");

        Log.i(TAG, "ingredientsTextComparison -> Start of comparing");
        Log.i(TAG, "ingredientsTextComparison -> correct.length == " + correct.length + ", extractedWords.length == " + extractedWords.length);

        int matchCount = 0;
        int lastMatchedWord = 0;

        for (String ingredient : correct) {
            String ingredientLower = ingredient.toLowerCase();
            int i=lastMatchedWord;
            boolean found = false;
            while(i<extractedWords.length && !found){
                if (extractedWords[i].contains(ingredientLower)) {
                    found = true;
                }
                else i++;
            }
            if(found){
                matchCount++;
                lastMatchedWord = i;
                Log.d(TAG, "ingredientsTextComparison -> \"" + ingredient + "\" == \"" + extractedWords[i] + "\" -> matchCount++");
            }
        }
        Log.i(TAG, "ingredientsTextComparison -> matchCount == " + matchCount);
        int confidence = (matchCount / correct.length)*100;
        Log.i(TAG, "ingredientsTextComparison -> confidence == " + confidence + " (%)");
        return confidence;

    }


    /**
     *
     * @param bitmap from which the text is extracted
     * @return String - the text extracted
     */
    private String executeOcr(Bitmap bitmap) {

        TextExtractor textExtractor = new TextExtractor();
        return textExtractor.getTextFromImg(bitmap);
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
