package unipd.se18.ocrcamera;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/**
    This activity is the first started activity that chooses which activity launch next
    @author Francesco Pham
 */
public class NavigatorActivity extends AppCompatActivity {

    //Tag used for logs
    private static final String TAG = "NavigatorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Get image path of last image
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String pathImage = prefs.getString("imagePath", null);


        Intent intent;

        /**
         * If already exists a photo, launch result activity to show it
         * with text attached.
         * @author Luca Moroldo modified by Francesco Pham
        */
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


        startActivity(intent);
        finish();
    }
}
