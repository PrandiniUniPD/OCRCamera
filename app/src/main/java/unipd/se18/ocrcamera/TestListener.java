package unipd.se18.ocrcamera;

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
     * Code of the test addition error
     */
    int TEST_ADDITION_FAILURE = 1;


    /**
     * The tests has finished
     */
    void onTestFinished();


    /**
     * A test is failed
     * @param failureCode The code of failure
     * @param testName The name of the test that has failed
     */
    void onTestFailure(int failureCode, String testName);
}
