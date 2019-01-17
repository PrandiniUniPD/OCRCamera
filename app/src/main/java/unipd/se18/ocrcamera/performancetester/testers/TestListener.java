package unipd.se18.ocrcamera.performancetester.testers;

public interface TestListener {

    public static final int JSON_FAILURE = 0;

    void onTestFinished();
    void onAlterationAnalyzed();
    void onTestFailure(int failureCode);
}
