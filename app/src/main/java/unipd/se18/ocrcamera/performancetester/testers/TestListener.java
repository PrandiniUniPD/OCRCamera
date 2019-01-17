package unipd.se18.ocrcamera.performancetester.testers;

/**
 * Listener used by the performance tester
 * @author Pietro Prandini (g2) - Luca Modoldo (g3)
 */
public interface TestListener {
    /**
     * Code of the JSON parsing error
     */
    public static final int JSON_PARSING_FAILURE = 0;

    /**
     * The tests has finished
     */
    void onTestFinished();

    /**
     * An alteration has been analyzed
     */
    void onAlterationAnalyzed();

    /**
     * The path of the directory used for finding the test pics is empty
     * @param dirPath The path of the empty directory of the test pics
     */
    void onEmptyDirectory(String dirPath);

    /**
     * The path of the directory used for finding the test pics is not valid
     * @param dirPath The path of the not valid directory of the test pics
     */
    void onNotValidDirectory(String dirPath);

    /**
     * A test is failed
     * @param failureCode The code of failure
     */
    void onTestFailure(int failureCode);
}
