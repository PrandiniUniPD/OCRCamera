package unipd.se18.ocrcamera.performancetester.testers;

import android.content.Context;

/**
 * Provides a tester object
 * @author Pietro Prandini (g2)
 */
public class TesterProvider {

    /**
     * Defines the performance tester types
     */
    public enum testers{ PhotoTester }

    /**
     * Gets a performance tester
     * @param tester The type of tester
     * @param context The context of the app
     * @param dirPath The path where the test pics are stored
     * @return The instance of the performance tester requested
     */
    public static PerformanceTester getTester(TesterProvider.testers tester,
                                       Context context, String dirPath)
                                        throws TestDirectoryException {
        switch (tester) {
            case PhotoTester:
                return new PhotoTester(context,dirPath);
            default:
                return new PhotoTester(context, dirPath);
        }
    }
}
