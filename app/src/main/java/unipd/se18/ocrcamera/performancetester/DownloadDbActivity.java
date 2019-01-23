package unipd.se18.ocrcamera.performancetester;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import unipd.se18.ocrcamera.R;


public class DownloadDbActivity extends AppCompatActivity {
    /**
     * Instantiate the UI elements and check if is possible to do the login.
     * @author Stefano Romanello (g3)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the layout with a fragment container
        setContentView(R.layout.activity_with_a_fragment_container);

        // Gets the support from the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Initializes the transaction (DownloadDbActivity to DownloadDbFragment)
        FragmentTransaction downloadDbFragmentTransaction = fragmentManager.beginTransaction();

        // Creates a DownloadDbFragment instance
        DownloadDbFragment downloadDbFragment = new DownloadDbFragment();

        // Adds the DownloadDbFragment to the transaction
        downloadDbFragmentTransaction.add(R.id.fragment_container,downloadDbFragment);

        // Commits the transaction
        downloadDbFragmentTransaction.commit();
    }
}
