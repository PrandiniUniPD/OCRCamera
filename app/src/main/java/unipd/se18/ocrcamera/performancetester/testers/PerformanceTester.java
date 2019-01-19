package unipd.se18.ocrcamera.performancetester.testers;

import unipd.se18.ocrcamera.performancetester.TestElement;

/**
 * The interface of a performance tester
 * @author Pietro Prandini (g2) - Luca Moroldo (g3)
 */
public interface PerformanceTester {
    /**
     * Elaborates tests using threads, stores the json report in string format to testReport.txt
     * inside the directory given on construction
     * @return String in JSON format with the test's report, each object is a single test named
     * with the filename and contains ingredients, tags, notes, original photo name, confidence
     * and alterations (if any), each alteration contains alteration tags and alteration notes
     */
    String testAndReport() throws InterruptedException;

    /**
     * Retrieves the TestElements loaded by this class
     * @return The array of TestElement loaded.
     */
    TestElement[] getTestElements();

    /**
     * Retrieves the number of the TestElements loaded by this class
     * @return The number of the TestElements loaded by this class
     */
    int getTestSize();

    /**
     * Sets a listener whose function will be called at the end of each test
     * @param testListener The TestListener used for communicating events about the progress
     */
    void setTestListener(TestListener testListener);

    /**
     * Converts statistics returned by getTagsStats() into a readable text
     */
    String getTagsStatsString();

    /**
     * Initialize the test converting files into TestElements. Required before calling
     * testAndReport().
     */
    void loadTests();
}
