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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

import info.debatty.java.stringsimilarity.*;

/**
 * Class built to test the application's OCR comparing the goal text with the recognized text and
 * providing a JSON report containing stats and results.
 * @author Luca Moroldo (g3) - Francesco Pham (g3)
 */
public class PhotoTester {

    /**
     * Contains the available extensions for the test
     */
    public static final String[] IMAGE_EXTENSIONS = {"jpeg", "jpg"};

    /**
     * Contains the base name of a photo used for the test
     */
    public static final String PHOTO_BASE_NAME = "foto";

    private static final String TAG = "PhotoTester";

    private ArrayList<TestElement> testElements = new ArrayList<TestElement>();

    //stores the path of the directory containing test files
    private String dirPath;

    private static final String REPORT_FILENAME = "report.txt";


    /**
     * Load all tests (images + description) inside a directory and prepare them to be evaluated
     * @param environment environment where the function will look for the directory named with dirName
     * @param dirName name of the directory containing photos and descriptions
     */
    public PhotoTester(File environment, String dirName) {
        /*
        An example of test files contained in the directory used to create the test is:
            foto11.txt
            foto11.jpeg
            foto12.jpg
            foto12.txt
            alteration16.jpg
            alteration22.jpg

         To find out which 'foto' has an alteration (e.g. modified photo) we need to read each .txt file, data is stored in JSON format.

         To automate the elaboration of each test i've modified the class TestElement, that allows to link each photo to its description and alterations (if any),
         and then, at the evaluation phase, set the results like recognition confidence and recognized text.
         */

        //Credits for getStorageDir function: Pietro Prandini (group 2)
        File directory = getStorageDir(environment, dirName);
        dirPath = directory.getPath();

        //create a TestElement object for each original photo - then link all the alterations to the relative original TestElement
        for(File file : directory.listFiles()) {

            String filePath = file.getPath();
            //get name without extension
            String fileName = Utils.getFilePrefix(filePath);

            //check if the file is a test
            if(fileName.contains(PHOTO_BASE_NAME)) {

                String fileExtension = Utils.getFileExtension(filePath);

                //check if image extension is available
                if(Arrays.asList(IMAGE_EXTENSIONS).contains(fileExtension)) {

                    Bitmap photoBitmap = Utils.loadBitmapFromFile(filePath);

                    //Each photo has a description.txt with the same filename - so when an image is found we know the description filename
                    String photoDesc= Utils.getTextFromFile(dirPath + "/" + fileName + ".txt");

                    JSONObject jsonPhotoDescription = null;
                    try {
                        //create test element giving filename, description and bitmap
                        jsonPhotoDescription = new JSONObject(photoDesc);
                    } catch(JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "PhotoTester constructor -> Error decoding JSON with name " + fileName);
                    }
                    if(jsonPhotoDescription != null) {
                        TestElement originalTest = new TestElement(photoBitmap, jsonPhotoDescription, fileName);

                        //associate the relative bitmap to each alteration of the original test if there is any
                        String[] alterationsFilenames = originalTest.getAlterationsNames();
                        if(alterationsFilenames != null) {
                            for(String alterationFilename : alterationsFilenames) {

                                String alterationBitmapPath = dirPath + "/" + alterationFilename;
                                Bitmap alterationBitmap = Utils.loadBitmapFromFile(alterationBitmapPath);
                                //Utils.loadBitmapFromFile may return null
                                if(alterationBitmap != null)
                                    originalTest.setAlterationBitmap(alterationFilename, alterationBitmap);
                                else
                                    Log.i(TAG, "alteration image with name " + alterationFilename + " not found")
                            }
                        }
                        testElements.add(originalTest);
                    }
                }
            }
        }
    }


    /**
     * Elaborate tests using threads, stores a report in JSON format to testReport.txt inside the directory given on construction
     * @return String in JSON format with the test's report, each object is a single test named with the filename and contains:
     * ingredients, tags, notes, original photo name, recognized text, confidence and alterations (if any), each alteration
     * contains tags, notes, recognized text, confidence
     * @author Luca Moroldo (g3)
     */
    public String testAndReport() throws InterruptedException {
        /*
        This method will launch a task to evaluate each test, and uses:
            - FixedThreadPool: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newFixedThreadPool-int-
            - CounDownLatch: https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/CountDownLatch.html

        When all the tasks are completed, it saves a report in JSON format and return it as String.
         */
        final JSONObject fullJsonReport = new JSONObject();

        int totalTestElements = testElements.size();
        //countDownLatch allows to sync this thread with the end of all the single tests
        CountDownLatch countDownLatch = new CountDownLatch(totalTestElements);

        int max_concurrent_tasks = Runtime.getRuntime().availableProcessors();
        //Define a thread executor that will run a maximum of 'max_concurrent_tasks' simultaneously
        ExecutorService executor = Executors.newFixedThreadPool(max_concurrent_tasks);

        //evaluate all the tests
        for(TestElement singleTest : testElements) {
            Runnable runnableTest = new RunnableTest(fullJsonReport, singleTest, countDownLatch);
            executor.execute(runnableTest);
        }
        //after shut down the executor will reject any new task
        executor.shutdown();
        //wait until all tests are completed - i.e. when CoundDownLatch.countDown() is called by each runnableTest
        countDownLatch.await();

        String fullReport = fullJsonReport.toString();
        //write report to file
        try {
            writeReportToExternalStorage(fullReport, dirPath, REPORT_FILENAME);
        } catch (IOException e) {
            Log.e(TAG, "Error writing report to file.");
            e.printStackTrace();
        }
        return fullReport;
    }


    /**
     * Write the string given as argument to a file named with the fileName argument
     * @param report text that will be saved to filename
     * @param dirPath path to a directory with writing permissions
     * @param fileName name of the file - will be overwritten if already exist
     * @author Luca Moroldo (g3)
     * @throws IOException
     */
    private void writeReportToExternalStorage(String report, String dirPath, String fileName) throws IOException {
        //Write report to report.txt
        File file = new File(dirPath, fileName);

        file.createNewFile();
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            try {
                stream.write(report.getBytes());
            } finally {
                stream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "File not found");
        }
    }


    /**
     * Apply OCR to the given bitmap
     * @param bitmap from which the text is extracted
     * @return String - the text extracted
     */
    private String executeOcr(Bitmap bitmap) {
        TextExtractor textExtractor = new TextExtractor();
        return textExtractor.getTextFromImg(bitmap);
    }


    /**
     * Class used to evaluate a single TestElement
     * @author Luca Moroldo (g3)
     */
    private class RunnableTest implements Runnable {
        private TestElement test;
        private JSONObject jsonReport;
        private CountDownLatch countDownLatch;

        /**
         * @param jsonReport JSONObject where will be added the evaluation of the TestElement
         * @param test - TestElement that must be associated to a bitmap
         * @param countDownLatch used to signal the task completion
         */
        public RunnableTest(JSONObject jsonReport, TestElement test, CountDownLatch countDownLatch) {
            this.jsonReport = jsonReport;
            this.test = test;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
                //evaluate text extraction confidence
                String extractedIngredients = executeOcr(test.getPicture());
                String correctIngredients = test.getIngredients();
                //compare the text recognized by the OCR with the real one
                float confidence = ingredientsTextComparison(correctIngredients, extractedIngredients);

                //insert results in test
                test.setConfidence(confidence);
                test.setRecognizedText(extractedIngredients);

                //evaluate each alterations if present
                String[] alterationsFileNames = test.getAlterationsNames();
                if(alterationsFileNames != null) {
                    for(String alterationFilename : alterationsFileNames) {

                        Bitmap alterationBitmap = test.getAlterationBitmap(alterationFilename);
                        String alterationExtractedIngredients = "";
                        if(alterationBitmap != null) {
                            alterationExtractedIngredients = executeOcr(alterationBitmap);
                        }
                        float alterationConfidence = ingredientsTextComparison(correctIngredients, alterationExtractedIngredients);

                        //insert evaluation
                        test.setAlterationConfidence(alterationFilename, alterationConfidence);
                        test.setAlterationRecognizedText(alterationFilename, alterationExtractedIngredients);
                    }
                }

                try {
                    addTestElement(jsonReport, test);
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to add test element '" + test.getFileName() + " to json report");
                }
                //signal the end of this single test
                countDownLatch.countDown();
        }
    }

    /**
     * Add a single TestElement associated JSONReport inside the JSONObject jsonReport, multi-thread safe
     * @param jsonReport the report containing tests in JSON format
     * @param test element of a test
     * @modify jsonReport
     * @throws JSONException
     * @author Luca Moroldo (g3)
     */
    private synchronized void addTestElement(JSONObject jsonReport, TestElement test) throws JSONException {
        jsonReport.put(test.getFileName(), test.getJsonObject());
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
}
