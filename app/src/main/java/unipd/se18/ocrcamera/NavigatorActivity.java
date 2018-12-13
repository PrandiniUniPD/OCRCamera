package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

/*
    This activity is the starting activity that chooses which activity to start
    Author: Francesco Pham
 */
public class NavigatorActivity extends AppCompatActivity {

    /**
     * TAG used for logs
     */
    private static final String TAG = "NavigatorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Get image path of last image
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String pathImage = prefs.getString("imagePath", null);


        Intent intent;

        /**
            If already exists a photo, launch result activity to show it
            with text attached - Author Luca Moroldo modified by Francesco Pham
        **/
        if(pathImage != null) {
            //load last extracted text
            prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            String OCRText = prefs.getString("text", null);

            if(OCRText != null && !(OCRText.equals(""))) {
                //An intent that will launch the activity
                intent = new Intent(NavigatorActivity.this, ResultActivity.class);

            }
            else {
                Log.e(TAG, "Error retrieving last extracted text");
                intent = new Intent(NavigatorActivity.this, CameraActivity.class);
            }

        }
        else {
            intent = new Intent(NavigatorActivity.this, CameraActivity.class);
        }
        //intent = new Intent(NavigatorActivity.this, GalleryActivity.class);

        startActivity(intent);
        finish();
    }
}
