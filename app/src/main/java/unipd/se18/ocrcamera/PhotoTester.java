package unipd.se18.ocrcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import unipd.se18.ocrcamera.InciSingleton;
import unipd.se18.ocrcamera.R;
import unipd.se18.ocrcamera.Utils;
import unipd.se18.ocrcamera.inci.Ingredient;
import unipd.se18.ocrcamera.inci.IngredientsExtractor;
import unipd.se18.ocrcamera.inci.LevenshteinStringDistance;
import unipd.se18.ocrcamera.inci.TextAutoCorrection;
import unipd.se18.ocrcamera.inci.TextSplitIngredientsExtractor;
import unipd.se18.textrecognizer.OCR;
import unipd.se18.textrecognizer.OCRListener;
import unipd.se18.textrecognizer.TextRecognizer;


/**
 * Class built to test the application's OCR comparing the goal text with the recognized text and
 * providing a JSON report containing stats and results.
 * @author Luca Moroldo (g3) - Francesco Pham (g3)
 */
class PhotoTester extends AbstractPerformanceTester {

    private static final String TAG = "PhotoTester";

    //ingredients extractors (Francesco Pham)
    private IngredientsExtractor ocrIngredientsExtractor;
    private IngredientsExtractor correctIngredientsExtractor;
    private TextAutoCorrection textCorrector;

    /**
     * The String of a JSON report
     */
    private String report;


    /**
     * Load test elements (images + description)
     * @param context The context of the app
     * @param dirPath The path where the photos and descriptions are.
     */
    PhotoTester(Context context, String dirPath) throws TestDirectoryException{
        super(context, dirPath);
    }


    /**
     * Evaluate each TestElement and create a JSON-format report that sums up the test result.
     * Each JSON object inside the report is related to a test, and contains: ingredients, tags,
     * notes, original photo name, confidence and alterations (if any); the alterations are JSON
     * objects too (named with their filename) and contains alteration tags and notes.
     * The report is saved int txt format, inside the directory given on construction.
     * @return String containing the test results in JSON format
     * @throws InterruptedException if the calling thread is interrupted
     * @author Luca Moroldo (g3)
     */
    public String testAndReport() throws InterruptedException {

        final JSONObject fullJsonReport = new JSONObject();

        //load inci db and initialize ingredient extractor (Francesco Pham)
        textCorrector = InciSingleton.getInstance(context).getTextCorrector();
        ocrIngredientsExtractor = InciSingleton.getInstance(context).getIngredientsExtractor();
        List<Ingredient> listInciIngredients =
                InciSingleton.getInstance(context).getListInciIngredients();
        correctIngredientsExtractor = new TextSplitIngredientsExtractor(listInciIngredients);

        //countDownLatch allows to sync this thread with the end of all the single tests
        CountDownLatch countDownLatch = new CountDownLatch(testElements.size());

        int max_concurrent_tasks = Runtime.getRuntime().availableProcessors();
        //leave a processor for the OS if possible
        if(max_concurrent_tasks > 1) {
            max_concurrent_tasks--;
        }

        //Define a thread executor that will run a maximum of 'max_concurrent_tasks' simultaneously
        ExecutorService executor = Executors.newFixedThreadPool(max_concurrent_tasks);

        //launch a task for each test
        for(TestElement singleTest : testElements) {
            Runnable runnableTest = new RunnableTest(fullJsonReport, singleTest, countDownLatch);
            executor.execute(runnableTest);
        }

        //the executor will reject any new task after shutdown
        executor.shutdown();

        //wait until all tests are completed -
        // i.e. when CoundDownLatch.countDown() has been called by each runnableTest
        countDownLatch.await();

        String fullReport = fullJsonReport.toString();
        //write report to file
        try {
            writeReportToExternalStorage(fullReport, dirPath, REPORT_FILENAME);
        } catch (IOException e) {
            Log.e(TAG, "Error writing report to file.");
            e.printStackTrace();
        }
        //save current report
        this.report = fullReport;
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
            stream.write(report.getBytes());
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found");
        } finally {
            //close res
            if(stream != null)
                stream.close();
        }
    }


    /**
     *
     * @param bitmap from which the text is extracted - this method blocks the calling thread
     * and waits for the text extraction from the OCR
     * @return String - the text extracted
     * @author Luca Moroldo, Pietro Prandini
     */
    private String executeOcr(Bitmap bitmap) {

        final CountDownLatch waitForExtraction = new CountDownLatch(1);

        final String[] recognizedText = new String[1];

        final OCRListener ocrTextListener = new OCRListener() {
            @Override
            public void onTextRecognized(String text) {
                recognizedText[0] = text;
                waitForExtraction.countDown();
            }

            @Override
            public void onTextRecognizedError(int code) {
                String errorText = R.string.extraction_error + " " + R.string.error_code + ")";
                recognizedText[0] = errorText;
                waitForExtraction.countDown();
            }
        };
        OCR ocrTextProcess = TextRecognizer.getTextRecognizer(TextRecognizer.Recognizer.mlKit, ocrTextListener);
        ocrTextProcess.getTextFromImg(bitmap);

        try {
            waitForExtraction.await();
        } catch (InterruptedException e) {
            Log.i(TAG, "Extraction interrupted");
        }

        return recognizedText[0];
    }


    /**
     * Class used to evaluate a single test
     * @author Luca Moroldo (g3)
     */
    class RunnableTest implements Runnable {

        /**
         * The test that will be evaluated
         */
        private TestElement test;

        /**
         * The JSONObject where will be added this test evaluation
         */
        private JSONObject jsonReport;

        /**
         * The countdownlatch that will be used to signal the end of the test
         */
        private CountDownLatch countDownLatch;


        /**
         * @param jsonReport JSONObject containing tests data that will be updated with current
         *                   'test'
         * @param test element of a test - must contain an image's path and ingredients field
         * @param countDownLatch used to signal the task completion
         */
        RunnableTest(JSONObject jsonReport, TestElement test, CountDownLatch countDownLatch) {
            this.jsonReport = jsonReport;
            this.test = test;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            //evaluate text extraction updating recognized text and confidence with the
            //recognition
            evaluateTest(test);

            //evaluate alterations if any updating, for each alteration, recognized text and
            // confidence with the extraction
            evaluateTestAlterations(test);

            try {
                //add the test using a synchronized function to avoid concurrency
                addTestElement(jsonReport, test);
            } catch (JSONException e) {
                //if a listener has been set, then signal the failure
                if(testListener != null) {
                    testListener.onTestFailure(TestListener.TEST_ADDITION_FAILURE, test.getFileName());
                }
                Log.e(TAG, "Failed to add test element '"
                        + test.getFileName() + " to json report");
            }

            //calls TestListener.onTestFinished (synchronized to avoid concurrency)
            signalTestFinished();

            //signal the end of the evaluation of this test
            countDownLatch.countDown();
        }


        /**
         * Evaluate a test setting the recognized text and the confidence with the recognition
         * @param test TestElement to evaluate
         * @modify test
         * @author Luca Moroldo
         */
        private void evaluateTest(TestElement test) {
            String imagePath = test.getImagePath();
            Bitmap testBitmap = Utils.loadBitmapFromFile(imagePath);

            //extract text from the image
            String ocrText = executeOcr(testBitmap);

            //compare extracted ingredients with real ingredients
            String correctIngredients = test.getIngredients();
            float confidence = ingredientsTextComparison(correctIngredients, ocrText);

            //credits Francesco Pham
            String ingredientsExtractionReport =
                    buildIngredientsExtractionReport(ocrText, correctIngredients);

            //insert results in test
            test.setConfidence(confidence);
            test.setRecognizedText(ocrText);
            test.setIngredientsExtraction(ingredientsExtractionReport);
        }


        /**
         * Evaluate each alteration (if any) setting the recognized text and the confidence
         * with the recognition.
         * @param test TestElement to evaluate
         * @modify test
         * @author Luca Moroldo
         */
        private void evaluateTestAlterations(TestElement test) {
            String[] alterationsFileNames = test.getAlterationsNames();
            //if not null, then there is at least one alteration
            if(alterationsFileNames != null) {
                //evaluate each alteration
                for(String alterationFilename : alterationsFileNames) {

                    String alterationImagePath = test.getAlterationImagePath(alterationFilename);
                    Bitmap alterationBitmap = Utils.loadBitmapFromFile(alterationImagePath);

                    String alterationExtractedIngredients = "";
                    if(alterationBitmap != null) {
                        //get ingredients recognized by the OCR
                        alterationExtractedIngredients = executeOcr(alterationBitmap);
                    }

                    //calculate confidence with recognition
                    float alterationConfidence = ingredientsTextComparison(
                            test.getIngredients(),
                            alterationExtractedIngredients
                    );

                    //insert evaluation
                    test.setAlterationConfidence(alterationFilename, alterationConfidence);

                    test.setAlterationRecognizedText(
                            alterationFilename,
                            alterationExtractedIngredients
                    );
                }
            }
        }


        /**
         * Signal the end of a single test by calling TestListener.onTestFinished if a listener
         * has been set, otherwise end silently
         * @author Luca Moroldo
         */
        private synchronized void signalTestFinished() {
            if(testListener != null) {
                testListener.onTestFinished();
            } else
                return;
        }
    }


    /**
     * Add a single TestElement associated JSONReport inside the JSONObject jsonReport,
     * multi-thread safe
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
     * Calculate the percentage gain for each alteration tag.
     * @return an HashMap: (Tag, Value)  where value is the average gain result
     * of the alterated photo tagged with that Tag
     * @author Luca Moroldo -
     * Credits: Nicolò Cerco (the structure of this method is the same of getTagsStats)
     */
    private HashMap getAlterationsTagsGainStats() {

        //contains the % earning for each alteration tag
        HashMap<String, Float> alterationTagsGain = new HashMap<>();
        //contains the occurrences of each tag
        HashMap<String, Integer> alterationTagsOccurrences = new HashMap<>();

        //for each test get the alterations (if any), for each alteration get the tags linked to it
        //and calc. gain
        for(TestElement element : testElements) {

            //evaluate alterations if any
            String[] alterationsNames = element.getAlterationsNames();
            if(alterationsNames != null) {

                //evaluate all the alterations
                for(String alterationName : alterationsNames) {

                    //for each tag calculate gain
                    for(String tag : element.getAlterationTags(alterationName)) {
                        //If the tag already exist then update the gain, otherwise add the tag
                        //with the current gain
                        if(alterationTagsGain.containsKey(tag)) {
                            float newGain = alterationTagsGain.get(tag)
                                            + (element.getAlterationConfidence(alterationName)
                                            - element.getConfidence());
                            alterationTagsGain.put(tag, newGain);
                            alterationTagsOccurrences.put(tag, alterationTagsOccurrences.get(tag) + 1);
                        } else{
                            alterationTagsGain.put(tag, element.getConfidence());
                            alterationTagsOccurrences.put(tag, 1);
                        }

                    }
                }
            }
        }
        //calculate avarage gain for each tag
        for(String tag : alterationTagsGain.keySet()){
            alterationTagsGain.put(tag, alterationTagsGain.get(tag)/alterationTagsOccurrences.get(tag)); // average of the scores
            Log.i(TAG, "-" + tag + " score: " + alterationTagsGain.get(tag));
        }
        return alterationTagsGain;
    }

}