package unipd.se18.ocrcamera.performancetester;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import unipd.se18.ocrcamera.R;

/**
 * Prints to the screen the details of a TestElement
 * @author Pietro Prandini (g2)
 */
public class TestDetailsActivity extends AppCompatActivity {
    /**
     * String used for the log of this class
     */
    private String TAG = "TestDetailsActivity -> ";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the layout with a fragment container
        setContentView(R.layout.activity_with_a_fragment_container);

        // Gets the support from the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Initializes the transaction (TestDetailsActivity to TestDetailsFragment)
        FragmentTransaction testDetailsFragmentTransaction = fragmentManager.beginTransaction();

        // Creates a TestDetailsFragment instance
        TestDetailsFragment testDetailsFragment = new TestDetailsFragment();

        // Adds the TestDetailsFragment to the transaction
        testDetailsFragmentTransaction.add(R.id.fragment_container,testDetailsFragment);

        // Commits the transaction
        testDetailsFragmentTransaction.commit();
    }
}
