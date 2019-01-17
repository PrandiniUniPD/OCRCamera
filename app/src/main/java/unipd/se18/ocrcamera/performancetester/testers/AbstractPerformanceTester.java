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
     * Contains the available extensions for the test
     */
    private static final String[] IMAGE_EXTENSIONS = {"jpeg", "jpg"};

    /**
     * Contains the base name of a photo used for the test
     */
    private static final String PHOTO_BASE_NAME = "foto";

    /**
     * String used as file name for the report
     */
    static final String REPORT_FILENAME = "report.txt";

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
    AbstractPerformanceTester(Context context, String dirPath) {
        // Initializes the context
        this.context = context;

        // Retrieves the directory where the test pics are stored
        File directory = getStorageDir(dirPath);
        this.dirPath = directory.getPath();
        Log.d(TAG, "PhotoTester -> dirPath == " + dirPath);

        // Gets the files contained in the directory
        File[] testElementsFiles = directory.listFiles();

        // Checks if the directory is empty, if yes there are nothing to analyze
        if (testElementsFiles.length == 0) {
            // Launches the empty directory event
            testListener.onEmptyDirectory(dirPath);

            // Nothing to analyze
            return;
        }

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
                    TestElement originalTest = parseTestElement(file,fileName);

                    // Adds the test element parsed if it's parsed correctly
                    if (originalTest != null) {
                        testElements.add(originalTest);
                    }
                }
            }
        }
    }


    /**
     * Get a File directory from a path String
     * @param dirPath The path of the directory
     * @return the file relative to the environment and the dirName
     * @author Pietro Prandini (g2)
     */
    private File getStorageDir(String dirPath) {
        // Get the directory for the user's public pictures directory.
        File file = new File(dirPath);
        if(!file.isDirectory()) {
            Log.e(TAG, file.getAbsolutePath() + "It's not a directory");
            testListener.onNotValidDirectory(dirPath);
        } else {
            Log.d(TAG, "Directory of tests => " + file.getAbsolutePath());
        }
        return null;
    }

    /**
     * Creates a TestElement
     * @param file The File compatible to a TestElement
     * @param fileName The filename of the file
     * @return The TestElement found, null if there was a problem when parsing of the JSON file
     */
    private TestElement parseTestElement(File file, String fileName) {
        //this file is an image -> get file path
        String originalImagePath = file.getAbsolutePath();

        //Each photo has a description.txt with the same filename
        // - so when an image is found we know the description filename
        String photoDesc= Utils.getTextFromFile(dirPath + "/" + fileName + ".txt");

        // Parses test element giving filename, description and image path
        try {
            JSONObject jsonPhotoDescription = new JSONObject(photoDesc);
            TestElement originalTest =
                    new TestElement(originalImagePath, jsonPhotoDescription, fileName);
            String[] alterationsFilenames = originalTest.getAlterationsNames();
            if(alterationsFilenames != null) {
                for(String alterationFilename : alterationsFilenames) {
                    String alterationImagePath = dirPath + "/" + alterationFilename;
                    originalTest.setAlterationImagePath(alterationFilename, alterationImagePath);
                }
            }
            return originalTest;
        } catch(JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "PhotoTester constructor -> Error decoding JSON");
            testListener.onTestFailure(TestListener.JSON_PARSING_FAILURE);
        }
        return null;
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
}
