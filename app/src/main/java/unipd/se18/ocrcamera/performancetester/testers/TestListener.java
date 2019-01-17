package unipd.se18.ocrcamera.performancetester.testers;

/**
 * Listener used by the performance tester
 * @author Pietro Prandini (g2) - Luca Modoldo (g3)
 */
public interface TestListener {
    /**
     * Code of the JSON parsing error
     */
    int JSON_PARSING_FAILURE = 0;

    /**
     * The tests has finished
     */
    void onTestFinished();

    /**
     * A test is failed
     * @param failureCode The code of failure
     */
    void onTestFailure(int failureCode, String testName);
}
