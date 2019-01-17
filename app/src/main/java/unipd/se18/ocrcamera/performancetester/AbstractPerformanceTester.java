package unipd.se18.ocrcamera.performancetester;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import unipd.se18.ocrcamera.Utils;

/**
 * Abstract class of a performance tester.
 * It's specific for testing the OCR on pics stored in the device
 * @author Pietro Prandini (g2)
 */
public abstract class AbstractPerformanceTester implements PerformanceTester {
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

        //create a TestElement object for each original photo - then link all the alterations to the relative original TestElement
        for(File file : directory.listFiles()) {
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
        } else {
            Log.d(TAG, "Directory of tests => " + file.getAbsolutePath());
        }
        return file;
    }

    /**
     * Elaborate tests using threads, stores the json report in string format to testReport.txt inside the directory given on construction
     * @return String in JSON format with the test's report, each object is a single test named with the filename and contains:
     * ingredients, tags, notes, original photo name, confidence and alterations (if any), each alteration contains alteration tags and alteration notes
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
