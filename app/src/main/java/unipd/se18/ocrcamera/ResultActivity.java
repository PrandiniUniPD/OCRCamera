package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import unipd.se18.ocrcamera.recognizer.OCR;
import unipd.se18.ocrcamera.recognizer.OCRListener;
import unipd.se18.ocrcamera.recognizer.TextRecognizer;

import static unipd.se18.ocrcamera.recognizer.TextRecognizer.getTextRecognizer;

/**
 * Class used for showing the result of the OCR processing
 */
public class ResultActivity extends AppCompatActivity {

    /**
     * The TextView of the extracted test from the captured photo.
     */
    private TextView mOCRTextView;

    /**
     * Listener used by the extraction process to notify results
     */
    private OCRListener textExtractionListener = new OCRListener() {
        @Override
        public void onTextRecognized(String text) {
                    /*
                     Text correctly recognized
                     -> prints it on the screen and saves it in the preferences
                     */
            mOCRTextView.setText(text);
            saveTheResult(text);
        }

        @Override
        public void onTextRecognizedError(int code) {
                    /*
                     Text not correctly recognized
                     -> prints the error on the screen and saves it in the preferences
                     */
            String errorText = R.string.extraction_error
                    + " (" + R.string.error_code + code + ")";
            mOCRTextView.setText(errorText);
            saveTheResult(errorText);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // UI components
        ImageView mImageView = findViewById(R.id.img_captured_view);
        mOCRTextView = findViewById(R.id.ocr_text_view);
        mOCRTextView.setMovementMethod(new ScrollingMovementMethod());

        FloatingActionButton fab = findViewById(R.id.newPictureFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultActivity.this, CameraActivity.class));
            }
        });


        //Get image path and text of the last image from preferences
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String pathImage = prefs.getString("imagePath", null);
        String OCRText = prefs.getString("text", null);

        Bitmap lastPhoto = BitmapFactory.decodeFile(pathImage);

        if (lastPhoto != null) {
            mImageView.setImageBitmap(Bitmap.createScaledBitmap(lastPhoto, lastPhoto.getWidth(),
                    lastPhoto.getHeight(), false));
        } else {
            Log.e("ResultActivity", "error retrieving last photo");
        }

        //Displaying the text, from OCR or preferences
        if(OCRText != null) {
            // Text in preferences
            if(OCRText.equals("")) {
                mOCRTextView.setText(R.string.no_text_found);
            } else {
                //Show the text of the last image
                mOCRTextView.setText(OCRText);
            }
        } else {
            // Views processing string
            mOCRTextView.setText(R.string.processing);

            // Instance of an OCR recognizer
            OCR ocrProcess = getTextRecognizer(TextRecognizer.Recognizer.mlKit,
                    textExtractionListener);

            // Runs the operations of text extraction
            ocrProcess.getTextFromImg(lastPhoto);
        }
    }

    /**
     * Saves the result obtained in the "prefs" preferences (Context.MODE_PRIVATE)
     * - the name of the String is "text"
     * @param text The text extracted by the process
     * @author Pietro Prandini (g2)
     */
    private void saveTheResult(String text) {
        // Saving in the preferences
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("prefs",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("text", text);
        editor.apply();
    }

    /**
     * Menu inflater
     * @author Francesco Pham
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.result_menu, menu);
        return true;
    }

    /**
     * Handling click events on the menu
     * @author Francesco Pham - modified by Stefano Romanello
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.test:
                Intent i = new Intent(ResultActivity.this, TestResultActivity.class);
                startActivity(i);
                return true;
            case R.id.download_photos:
                Intent download_intent = new Intent(ResultActivity.this,
                        DownloadDbActivity.class);
                startActivity(download_intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

