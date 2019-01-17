package unipd.se18.ocrcamera.performancetester.testers;

import android.content.Context;

/**
 * Provides a tester object
 * @author Pietro Prandini (g2)
 */
public class TesterProvider {

    public enum testers{ PhotoTester }

    public static PerformanceTester getTester(TesterProvider.testers tester,
                                       Context context, String dirPath) {
        switch (tester) {
            case PhotoTester:
                return new PhotoTester(context,dirPath);
            default:
                return new PhotoTester(context, dirPath);
        }
    }
}
