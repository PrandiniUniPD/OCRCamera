package unipd.se18.ocrcamera.performancetester;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import unipd.se18.ocrcamera.R;

/**
 * Activity for showing the result of the tests
 * Pietro Prandini (g2)
 */
public class TestsListActivity extends AppCompatActivity {
    /**
     * String used for the logs of this class
     */
    private static final String TAG = "TestsListActivity";

    /**
     * Prepares the activity to show the test results.
     * More details at: {@link ActivityCompat#checkSelfPermission(Context, String)}
     * @param savedInstanceState Bundle of the last instance state of the app
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the layout with a fragment container
        setContentView(R.layout.activity_with_a_fragment_container);

        // Gets the support from the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Initializes the transaction (TestsListActivity to TestsListFragment)
        FragmentTransaction testListFragmentTransaction = fragmentManager.beginTransaction();

        // Creates a TestsListFragment instance
        TestsListFragment testsListFragment = new TestsListFragment();

        // Adds the testsListFragment to the transaction
        testListFragmentTransaction.add(R.id.fragment_container,testsListFragment);

        // Commits the transaction
        testListFragmentTransaction.commit();
    }
}
