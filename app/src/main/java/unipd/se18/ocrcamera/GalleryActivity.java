package unipd.se18.ocrcamera;



import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Gallery activity
 * @author Stefano Romanello - Fragment suggestion Leonardo Rossi
 */
public class GalleryActivity extends AppCompatActivity {

    //Code for internet permission
    private static final int REQUEST_PERMISSION_CODE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        if(verifyStoragePermission())
        {
            loadHomeFragment();
        }
    }

    /**
     * Function for load the home fragment from onActivityCreated and
     * onRequestPermissionsResult in case I get the storage permission from the permission
     */
    FragmentManager fm;
    private void loadHomeFragment()
    {
        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragmentPlaceHolder, new MainFragment(),getString(R.string.homeFragmentTag));
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        //If there is more than 1 fragment running I close the
        //current one (DetailsFragment) and return to the prevous (HomeFragment)
        //Or I just call the default onBackPressed which closes the activity
        if (backStackEntryCount != 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }



    /************************************************************/
    /***************************FRAGMENTS************************/
    /************************************************************/







    /*********************************************/
    /******************PERMISSIONS****************/
    /*********************************************/


    /**
     * Verify if the user granted the permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==REQUEST_PERMISSION_CODE)
        {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // permission denied
                showPermissionErrorDialog();
            }
            else
            {
                loadHomeFragment();
            }
        }
    }

    /**
     * Verify if the app has the storage permission
     * @return boolean of the current status (before asking the permission)
     * if its false this prevent the app to load the fragment that uses the internal storage
     */
    public boolean verifyStoragePermission()
    {
        //Check and in case Ask for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Show a simple error message before closing the activity in case bad permissioN
     */
    public void showPermissionErrorDialog()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Permission error")
                .setMessage("You did not have authorized the app to use internal storage.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with closing the activity
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
