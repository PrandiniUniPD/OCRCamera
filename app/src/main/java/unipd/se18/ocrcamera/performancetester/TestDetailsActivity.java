package unipd.se18.ocrcamera.performancetester;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import unipd.se18.ocrcamera.R;
import unipd.se18.ocrcamera.Utils;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the layout with a fragment container
        setContentView(R.layout.activity_with_a_fragment_container);

        // Gets the support from the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Initializes the transaction (TestsListActivity to TestsListFragment)
        FragmentTransaction testListFragmentTransaction = fragmentManager.beginTransaction();

        // Creates a TestsListFragment instance
        TestDetailsFragment testsListFragment = new TestDetailsFragment();

        // Adds the testsListFragment to the transaction
        testListFragmentTransaction.add(R.id.fragment_container,testsListFragment);

        // Commits the transaction
        testListFragmentTransaction.commit();
    }
}
