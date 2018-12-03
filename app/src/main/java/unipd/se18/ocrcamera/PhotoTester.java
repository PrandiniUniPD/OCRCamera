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
     *
     * Load test elements (images + description)
     * @param environment environment where the function will look for the directory with dirName
     * @param dirName stores the name of the directory containing photos and description
     */
    public PhotoTester(File environment, String dirName) {
        File directory = getStorageDir(environment, dirName);
        dirPath = directory.getPath();
        Log.v(TAG, "PhotoTester -> dirPath == " + dirPath);

        //create a TestElement object for each original photo - then link all the alterations to the relative original TestElement
        for(File file : directory.listFiles()) {

            String filePath = file.getPath();
            String fileName = Utils.getFilePrefix(filePath);

            //if the file is not an alteration then create a test element for it
            if(fileName.contains(PHOTO_BASE_NAME)) {

                String fileExtension = Utils.getFileExtension(filePath);

                //check if extension is available
                if(Arrays.asList(IMAGE_EXTENSIONS).contains(fileExtension)) {

                    Bitmap photoBitmap = Utils.loadBitmapFromFile(filePath);

                    //Each photo has a description.txt with the same filename - so when an image is found we know the description filename
                    String photoDesc= Utils.getTextFromFile(dirPath + "/" + fileName + ".txt");

                    //create test element giving filename, description and bitmap
                    //author Luca Moroldo - g3

                    JSONObject jsonPhotoDescription = null;
                    try {
                        jsonPhotoDescription = new JSONObject(photoDesc);
                    } catch(JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "PhotoTester constructor -> Error decoding JSON");
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
                            }
                        }
                        testElements.add(originalTest);
                    }
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
     * Elaborate tests using threads, stores the json report in string format to testReport.txt inside the directory given on construction
     * @return String in JSON format with the test's report, each object is a single test named with the filename and contains:
     * ingredients, tags, notes, original photo name, confidence and alterations (if any), each alteration contains alteration tags and alteration notes
     * @author Luca Moroldo (g3)
     */
    public String testAndReport() throws InterruptedException {

        Log.i(TAG,"testAndReport started");
        long started = java.lang.System.currentTimeMillis();

        final JSONObject fullJsonReport = new JSONObject();

        int totalTestElements = testElements.size();

        //countDownLatch allows to sync this thread with the end of all the single tests
        CountDownLatch countDownLatch = new CountDownLatch(totalTestElements);

        int max_concurrent_tasks = Runtime.getRuntime().availableProcessors();
        Log.i(TAG, "max_concurrent_tasks == " + max_concurrent_tasks + " (number of the available cores)");

        //Define a thread executor that will run a maximum of 'max_concurrent_tasks' simultaneously
        ExecutorService executor = Executors.newFixedThreadPool(max_concurrent_tasks);

        for(TestElement singleTest : testElements) {
            Runnable runnableTest = new RunnableTest(fullJsonReport, singleTest, countDownLatch);
            executor.execute(runnableTest);
        }

        //after shut down the executor will reject any new task
        executor.shutdown();

        //wait until all tests are completed - i.e. when CoundDownLatch.countDown() is called by each runnableTest
        countDownLatch.await();

        long ended = java.lang.System.currentTimeMillis();
        Log.i(TAG,"testAndReport ended (" + totalTestElements + " pics tested in " + (ended - started) + " ms)");

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

    public TestElement[] getTestElements() {
        return testElements.toArray(new TestElement[0]);
    }


    /**
     * Compare the list of ingredients extracted by OCR and the correct list of ingredients
     * @param correct correct list of ingredients loaded from file
     * @param extracted list of ingredients extracted by the OCR
     * @return confidence percentage based on number of matched words, their similarity and order
     * @author Francesco Pham credit to Stefano Romanello for Levenshtein library suggestion
     */
    private float ingredientsTextComparison(String correct, String extracted){

        extracted = extracted.toLowerCase();
        String[] extractedWords = extracted.trim().split("[ ,-:./\\n\\r]+");
        String[] correctWords = correct.trim().split("[ ,-:./\\n\\r]+");

        Log.i(TAG, "ingredientsTextComparison -> Start of comparing");
        Log.i(TAG, "ingredientsTextComparison -> correctWords.length == " + correctWords.length + ", extractedWords.length == " + extractedWords.length);

        float points = 0;
        int maxPoints = 0;
        int posLastWordFound = 0;
        int consecutiveNotFound = 0;


        WeightedLevenshtein levenshtein = new WeightedLevenshtein(
                new CharacterSubstitutionInterface() {
                    public double cost(char c1, char c2) {

                        // The cost for substituting 't' and 'r' is considered
                        // smaller as these 2 are located next to each other
                        // on a keyboard
                        if (c1 == 't' && c2 == 'r') {
                            return 0.5;
                        }
                        else if (c1 == 'q' && c2 == 'o') {
                            return 0.5;
                        }
                        else if (c1 == 'I' && c2 == 'l') {
                            return 0.5;
                        }

                        // For most cases, the cost of substituting 2 characters
                        // is 1.0
                        return 1.0;
                    }
                });

        for (String word : correctWords) {
            boolean found = false;
            int index = posLastWordFound;
            word = word.toLowerCase();

            if (word.length() >= 3) {
                maxPoints += word.length();
                for (int i = 0; i < extractedWords.length && !found; i++) {
                    index = (posLastWordFound + i) % extractedWords.length;

                    //Calculate similarity and normalize
                    int maxLength = Math.max(word.length(),extractedWords[index].length());
                    double similarity = 1.0 - levenshtein.distance(word,extractedWords[index])/maxLength;

                    if (similarity > 0.8) {
                        if (points == 0 || i < consecutiveNotFound + 10) {
                            points += word.length()*similarity; //assign points based on number of characters
                        } else {
                            points += (float) word.length()*similarity/2;
                        }
                        Log.d(TAG, "ingredientsTextComparison -> \"" + word + "\" ==  \"" + extractedWords[index] + "\" similarity="+similarity);
                        extractedWords[index] = ""; //remove found word
                        found = true;
                    }
                }
            }

            if(!found && word.length() >= 6){
                maxPoints += word.length();
                for(int i=0; i<extractedWords.length && !found; i++) {
                    index = (posLastWordFound+i)%extractedWords.length;
                    if (extractedWords[index].contains(word)) {
                        if(points==0 || i<consecutiveNotFound+10) {
                            points += word.length(); //assign points based on number of characters
                        } else {
                            points += (float)word.length()/2;
                        }
                        Log.d(TAG, "ingredientsTextComparison -> \"" + word + "\" contained in  \"" + extractedWords[index] + "\"");
                        extractedWords[index] = extractedWords[index].replace(word, ""); //remove found word
                        found = true;
                    }
                }
            }

            if(found){
                consecutiveNotFound = 0;
                posLastWordFound = index;
            } else {
                consecutiveNotFound++;
            }
        }
        float confidence = (points / maxPoints)*100;
        Log.i(TAG, "ingredientsTextComparison -> confidence == " + confidence + " (%)");
        return confidence;

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
     *
     * @param bitmap from which the text is extracted
     * @return String - the text extracted
     */
    private String executeOcr(Bitmap bitmap) {

        TextExtractor textExtractor = new TextExtractor();
        return textExtractor.getTextFromImg(bitmap);


    }


    /**
     * Class used to run a single test
     * @author Luca Moroldo (g3)
     */
    public class RunnableTest implements Runnable {
        private TestElement test;
        private JSONObject jsonReport;
        private CountDownLatch countDownLatch;

        /**
         * @param jsonReport JSONObject containing tests data
         * @param test element of a test - must contain bitmap and ingredients fields
         * @param countDownLatch used to signal the task completion
         * @param semaphore semaphore used to signal the end of the task
         */
        public RunnableTest(JSONObject jsonReport, TestElement test, CountDownLatch countDownLatch) {
            this.jsonReport = jsonReport;
            this.test = test;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {

                Log.d(TAG,"RunnableTest -> id \"" + Thread.currentThread().getId() + "\" started");
                long started = java.lang.System.currentTimeMillis();

                //evaluate text extraction confidence
                String extractedIngredients = executeOcr(test.getPicture());
                String correctIngredients = test.getIngredients();
                float confidence = ingredientsTextComparison(correctIngredients, extractedIngredients);

                //insert results in test
                test.setConfidence(confidence);
                test.setRecognizedText(extractedIngredients);

                //evaluate alterations if any
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

                long ended = java.lang.System.currentTimeMillis();
                Log.d(TAG,"RunnableTest -> id \"" + Thread.currentThread().getId() + "\" ended (runned for " + (ended - started) + " ms)");
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
    synchronized void addTestElement(JSONObject jsonReport, TestElement test) throws JSONException {
        jsonReport.put(test.getFileName(), test.getJsonObject());
    }

    /**
    * Returns a HashMap of (Tag, Value) pairs where value is the average test result of the photos tagged with that Tag
    * @author Nicolò Cervo (g3) with the tutoring of Francesco Pham (g3)
    */
    public HashMap getTagsStats() {

        HashMap<String, Float> tagStats = new HashMap<>(); //contains the cumulative score of every tag

        HashMap<String, Integer> tagOccurrences = new HashMap<>(); //contains the number of occurrences of each tag

        for(TestElement element : testElements) {
            for (String tag : element.getTags()) {
                if(tagStats.containsKey(tag)) {
                    float newValue = tagStats.get(tag) + element.getConfidence();
                    tagStats.put(tag, newValue);
                    tagOccurrences.put(tag, tagOccurrences.get(tag) + 1);
                }else{
                    tagStats.put(tag, element.getConfidence());
                    tagOccurrences.put(tag, 1);
                }
            }
        }

        Log.i(TAG, "getTagStats():");
        for(String tag : tagStats.keySet()){
            tagStats.put(tag, tagStats.get(tag)/tagOccurrences.get(tag)); // average of the scores
            Log.i(TAG, "-" + tag + " score: " + tagStats.get(tag));
        }
        return tagStats;
    }

    /**
     * Convert statistics returned by getTagsStats() into a readable text
     * @author Francesco Pham (g3)
     */
    public String getTagsStatsString() {
        HashMap tagsStats = getTagsStats();
        String report = "Average confidence by tags: \n";
        while(!tagsStats.isEmpty()){
            String keymin = getMinKey(tagsStats);
            report = report + keymin + " : " + tagsStats.get(keymin) + "%\n";
            tagsStats.remove(keymin);
        }
        return report;
    }

    private String getMinKey(Map<String, Float> map) {
        String minKey = null;
        float minValue = Float.MAX_VALUE;
        for(String key : map.keySet()) {
            float value = map.get(key);
            if(value < minValue) {
                minValue = value;
                minKey = key;
            }
        }
        return minKey;
    }
}
