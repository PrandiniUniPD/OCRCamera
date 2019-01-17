package unipd.se18.ocrcamera.performancetester.testers;

/**
 * Listener used by the performance tester
 * @author Pietro Prandini (g2) - Luca Modoldo (g3)
 */
public interface TestListener {
    /**
     *
     */
    public static final int JSON_FAILURE = 0;

    /**
     *
     */
    void onTestFinished();

    /**
     *
     */
    void onAlterationAnalyzed();

    /**
     *
     */
    void onEmptyDirectory(String dirPath);

    /**
     *
     * @param dirPath
     */
    void onNotValidDirectory(String dirPath);

    /**
     *
     * @param failureCode
     */
    void onTestFailure(int failureCode);
}
