package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;

/**
 * Class built to test the application's OCR
 * @author Luca Moroldo (g3) - Francesco Pham (g3)
 */
public class PhotoTester {

    public static final String[] IMAGE_EXTENSIONS = {"jpeg", "jpg"};

    private static final String TAG = "PhotoTester";

    private ArrayList<TestInstance> testInstances = new ArrayList<TestInstance>();

    //stores the path of the directory containing test files
    private String dirPath;


    /**
     * Load test instances (images + description)
     * @param environment environment where find the directory with dirName
     * @param dirName stores the path to the directory containing photos and description
     */
    public PhotoTester(File environment, String dirName) {
        File directory = getStorageDir(environment, dirName);

        dirPath = directory.getPath();
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
     * Elaborate tests using 4 concurrent threads, stores the json report to string in testReport.txt
     * @return String in JSON format with the test's report, each object is named with the filename and contains:
     * ingrediens, tags, notes, original photo name, confidence
     * @author Luca Moroldo (g3)
     */
    public String testAndReport() {

        Log.i(TAG,"testAndReport started");
        long started = java.lang.System.currentTimeMillis();

        final JSONObject jsonReport = new JSONObject();

        int totalTestInstances = testInstances.size();

        //stores the total number of tests
        CountDownLatch countDownLatch = new CountDownLatch(totalTestInstances);

        int max_concurrent_tasks = Runtime.getRuntime().availableProcessors();
        Log.i(TAG, "max_concurrent_tasks == " + max_concurrent_tasks + " (number of the available cores)");
        Semaphore availableThread = new Semaphore(max_concurrent_tasks);

        for(TestInstance test : testInstances){
            try {
                //wait for available thread
                availableThread.acquire();

                //launch thread
                new Thread(new RunnableTest(jsonReport, test, countDownLatch, availableThread)).start();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        //wait for all tests to complete
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long ended = java.lang.System.currentTimeMillis();
        Log.i(TAG,"testAndReport ended (" + totalTestInstances + " pics tested in " + (ended - started) + " ms)");

        String report = jsonReport.toString();

        writeReportToExternalStorage(report, dirPath, "report.txt");

        return report;
    }



    /**
     * Compare the list of ingredients extracted by OCR and the correct list of ingredients
     * @param correct correct list of ingredients loaded from file
     * @param extracted list of ingredients extracted by the OCR
     * @return percentage of matched words.
     * @author Francesco Pham
     */
    private float ingredientsTextComparison(String correct, String extracted){

        extracted = extracted.toLowerCase();
        String[] extractedWords = extracted.trim().split("[ ,./]+");
        String[] correctWords = correct.trim().split("[ ,./]+");

        Log.i(TAG, "ingredientsTextComparison -> Start of comparing");
        Log.i(TAG, "ingredientsTextComparison -> correctWords.length == " + correctWords.length + ", extractedWords.length == " + extractedWords.length);

        int matchCount = 0;

        for (String word : correctWords) {
            String ingredientLower = word.toLowerCase();
            int i=0;
            boolean found = false;
            while(i<extractedWords.length && !found){
                if (extractedWords[i].contains(ingredientLower)) {
                    found = true;
                }
                else i++;
            }
            if(found){
                matchCount++;
                Log.v(TAG, "ingredientsTextComparison -> \"" + word + "\" contained in  \"" + extractedWords[i] + "\" -> matchCount++");
            }
        }
        Log.i(TAG, "ingredientsTextComparison -> matchCount == " + matchCount);
        float confidence = ((float)matchCount / correctWords.length)*100;
        Log.i(TAG, "ingredientsTextComparison -> confidence == " + confidence + " (%)");
        return confidence;

    }

    /**
     *
     * @param report text that will be saved to filename
     * @param dirPath path to a directory with writing permissions
     * @param filename name of the file - will be overwritten if already exist
     * @author Luca Moroldo (g3)
     */
    private void writeReportToExternalStorage(String report, String dirPath, String filename) {
        //Write report to report.txt
        File file = new File(dirPath, filename);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error creating file");
        }

        FileOutputStream stream = null;

        try {
            stream = new FileOutputStream(file);

            try {
                try {
                    stream.write(report.getBytes());
                } finally {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error writing report to file");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "File not found");
        }

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
            String ingredients = getIngredients();
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

        public void setConfidence(float confidence) throws JSONException {
            jsonObject.put("confidence", confidence);
        }
        public void setRecognizedText(String text) throws JSONException {
            jsonObject.put("extracted_text", text);
        }
    }

    /**
     * Class used to run a single test
     * @author Luca Moroldo (g3)
     */
    public class RunnableTest implements Runnable {
        private TestInstance test;
        private JSONObject jsonReport;
        private CountDownLatch countDownLatch;
        private Semaphore semaphore;

        /**
         *
         * @param jsonReport JSONObject containing tests data
         * @param test instance of a test - must contain bitmap and ingredients fields
         * @param countDownLatch used to signal the task completion
         * @param semaphore semaphore used to signal the end of the task
         */
        public RunnableTest(JSONObject jsonReport, TestInstance test, CountDownLatch countDownLatch, Semaphore semaphore) {
            this.jsonReport = jsonReport;
            this.test = test;
            this.countDownLatch = countDownLatch;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                Log.d(TAG,"RunnableTest -> id \"" + Thread.currentThread().getId() + "\" started");
                long started = java.lang.System.currentTimeMillis();
                //evaluate text extraction
                String extractedIngredients = executeOcr(test.getPicture());
                String correctIngredients = test.getIngredients();
                float confidence = ingredientsTextComparison(correctIngredients, extractedIngredients);

                //insert test in report
                test.setConfidence(confidence);
                //insert extracted test
                test.setRecognizedText(extractedIngredients);


                addTestInstance(jsonReport, test);

                //done test process signal
                countDownLatch.countDown();

                long ended = java.lang.System.currentTimeMillis();
                Log.d(TAG,"RunnableTest -> id \"" + Thread.currentThread().getId() + "\" ended (runned for " + (ended - started) + " ms)");

                //let start another task
                semaphore.release();

            }catch (JSONException e) {
                Log.e(TAG, "Error elaborating JSON test instance");
            }
        }
    }

    /**
     * Puts the json object of the TestInstance inside the JSONObject jsonReport, multi thread safe
     * @param jsonReport the report containing tests in JSON format
     * @param test instance of a test
     * @throws JSONException
     * @author Luca Moroldo (g3)
     */
    synchronized void addTestInstance(JSONObject jsonReport, TestInstance test) throws JSONException {
        jsonReport.put(test.getFileName(), test.getJsonObject());
    }
}
