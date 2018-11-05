package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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

        InternalStorageManager bitmapManager = new InternalStorageManager(
                getApplicationContext(), "OCRPhoto", "lastPhoto");

        Intent intent;

        /*
            If already exists a photo, launch result activity to show it
            with text attached - Author Luca Moroldo modified by Francesco Pham
        */
        if(bitmapManager.existsFile()) {
            //load last extracted text
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("extractedText", Context.MODE_PRIVATE);
            String lastExtractedText = sharedPref.getString("lastExtractedText", "");

            if(lastExtractedText != null && !(lastExtractedText.equals(""))) {
                //An intent that will launch the activity
                intent = new Intent(NavigatorActivity.this, ResultActivity.class);
                intent.putExtra("text", lastExtractedText);
            }
            else {
                Log.e(TAG, "Error retrieving last extr text");
                intent = new Intent(NavigatorActivity.this, CameraActivity.class);
            }

        }
        else {
            intent = new Intent(NavigatorActivity.this, CameraActivity.class);
        }


        startActivity(intent);
        finish();
    }
}
