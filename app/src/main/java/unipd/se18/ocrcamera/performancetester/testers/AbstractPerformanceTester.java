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

        // Gets the files contained in the directory
        File[] testElementsFiles = directory.listFiles();

        // Checks if the directory is empty, if yes there is nothing to analyze
        if (testElementsFiles.length == 0) {
            // Launches the empty directory event
            throw new TestDirectoryException(dirPath + " is empty.");
        }

        // Saves the path of the directory
        this.dirPath = directory.getPath();
        Log.d(TAG, "PhotoTester -> dirPath == " + dirPath);
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
