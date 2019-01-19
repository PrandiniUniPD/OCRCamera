package unipd.se18.ocrcamera.performancetester.testers;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import unipd.se18.ocrcamera.Utils;
import unipd.se18.ocrcamera.performancetester.TestElement;

/**
 * Abstract class of a performance tester.
 * It's specific for testing the OCR on pics stored in the device
 * @author Pietro Prandini (g2)
 */
abstract class AbstractPerformanceTester implements PerformanceTester {
    /**
     * String used for the logs of this class
     */
    private static final String TAG = "PerformanceTester";

    /**
     * String used as file name for the report
     */
    static final String REPORT_FILENAME = "report.txt";

    /**
     * Contains the available extensions for the test
     */
    private static final String[] IMAGE_EXTENSIONS = {"jpeg", "jpg"};

    /**
     * Contains the base name of a photo used for the test
     */
    private static final String PHOTO_BASE_NAME = "foto";

    /**
     * The ArrayList of the test elements
     */
    ArrayList<TestElement> testElements = new ArrayList<>();

    /**
     * The TestListener used for communicating events about the progress
     */
    TestListener testListener;

    /**
     * The path of the directory containing test files
     */
    String dirPath;

    /**
     * The context of the app
     */
    Context context;

    /**
     * Load test elements (images + description)
     * @param context The context of the app
     * @param dirPath The path where the photos and descriptions are.
     * @author Luca Moroldo (g3) - Modified by Pietro Prandini (g2)
     */
    AbstractPerformanceTester(Context context, String dirPath) throws TestDirectoryException {
        // Initializes the context
        this.context = context;

        // Gets File of the path
        File directory = new File(dirPath);

        // Checks if it's a directory
        if(!directory.isDirectory()) {
            // It's not a directory
            Log.e(TAG, directory.getAbsolutePath() + "It's not a directory");

            // Launches the not valid directory event
            throw new TestDirectoryException(dirPath + " is not a directory.");
        }

        // Get the files contained in the directory
        File[] testElementsFiles = directory.listFiles();

        // Checks if the directory is empty, if yes there is nothing to analyze
        if (testElementsFiles.length == 0) {
            // Launches the empty directory event
            throw new TestDirectoryException(dirPath + " is empty.");
        }
        this.dirPath = dirPath;
    }

    /**
     * Init all test elements reading files inside the directory pointed by dirPath.
     * If TestListener has been set, then in case of corrupted test calls onTestFailure(int code).
     * Modifies testElements.
     * @see #parseTestElement(File file)
     * @author Luca Moroldo
     */
    public void loadTests() {
        File directory = new File(dirPath);
        File[] testElementsFiles = directory.listFiles();

        // Saves the path of the directory
        this.dirPath = directory.getPath();
        Log.d(TAG, "PhotoTester -> dirPath == " + dirPath);

        //creates a TestElement object for each original photo
        // - then links all the alterations to the relative original TestElement
        for(File file : testElementsFiles) {
            String filePath = file.getPath();
            String fileName = Utils.getFilePrefix(filePath);

            // If the file is not an alteration then creates a test element for it
            if(fileName != null && fileName.contains(PHOTO_BASE_NAME)) {
                // Checks if the extension is supported
                String fileExtension = Utils.getFileExtension(filePath);
                if (Arrays.asList(IMAGE_EXTENSIONS).contains(fileExtension)) {
                    // Extension supported -> Parses the test element
                    TestElement testElement = parseTestElement(file);
                    if(testElement != null) {
                        testElements.add(testElement);
                    }
                }
            }
        }
    }



    /**
     * Elaborate tests using threads, stores the json report in string format to testReport.txt
     * inside the directory given on construction
     * @return String in JSON format with the test's report, each object is a single test named
     * with the filename and contains ingredients, tags, notes, original photo name, confidence
     * and alterations (if any), each alteration contains alteration tags and alteration notes
     * @author Luca Moroldo (g3)
     */
    public abstract String testAndReport() throws InterruptedException;

    /**
     * Retrieves the TestElements loaded by this class
     * @return The array of TestElement loaded.
     */
    public TestElement[] getTestElements() { return testElements.toArray(new TestElement[0]); }

    /**
     * Retrieves the number of the TestElements loaded by this class
     * @return The number of the TestElements loaded by this class
     */
    public int getTestSize() { return testElements.size(); }

    /**
     * Set a listener whose function will be called at the end of each test
     * @param testListener The TestListener used for communicating events about the progress
     */
    public void setTestListener(TestListener testListener) { this.testListener = testListener; }

    /**
     * Convert statistics returned by getTagsStats() into a readable text
     */
    public abstract String getTagsStatsString();

    /**
     * Parse a txt file to get a TestElement, calls testListener.onTestFailure in case of
     * corrupted test file.
     * @param file .txt file that contains Test data in JSON format
     * @return parsed TestElement object
     * @author Luca Moroldo
     */
    private TestElement parseTestElement(File file) {

        String fileName = Utils.getFilePrefix(file.getPath());
        //this file is an image -> get file path
        String originalImagePath = file.getAbsolutePath();
        //Each photo has a description.txt with the same filename
        // - so when an image is found we know the description filename
        String photoDesc= Utils.getTextFromFile(dirPath + "/" + fileName + ".txt");

        TestElement originalTest = null;

        // Parses test element giving filename, description and image path
        try {
            JSONObject jsonPhotoDescription = new JSONObject(photoDesc);
            originalTest =
                    new TestElement(originalImagePath, jsonPhotoDescription, fileName);

            String[] alterationsFilenames = originalTest.getAlterationsNames();
            if(alterationsFilenames != null) {
                for(String alterationFilename : alterationsFilenames) {
                    String alterationImagePath = dirPath + "/" + alterationFilename;
                    originalTest.setAlterationImagePath(alterationFilename, alterationImagePath);
                }
            }

        } catch(JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "PhotoTester constructor -> Error decoding JSON with name: " + fileName);

            if(testListener != null) {
                testListener.onTestFailure(TestListener.JSON_PARSING_FAILURE, fileName);
            }

        }
        return originalTest;
    }
}
