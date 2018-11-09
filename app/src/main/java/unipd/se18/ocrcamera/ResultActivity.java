package unipd.se18.ocrcamera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Class used for showing the result of the OCR processing
 */
public class ResultActivity extends AppCompatActivity {

    /**
     * The TextView of the extracted test from the captured photo.
     */
    private TextView mOCRTextView;

    /**
     * TextExtractor object useful for extracting text from pics
     */
    private TextExtractor ocr;

    /**
     * Bitmap of the lastPhoto saved
     */
    private Bitmap lastPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Instance of InternalStorageManager for managing the pic data
        InternalStorageManager bitmapManager;

        // UI components
        ImageView mImageView = findViewById(R.id.img_captured_view);
        mOCRTextView = findViewById(R.id.ocr_text_view);
        mOCRTextView.setMovementMethod(new ScrollingMovementMethod());

        String OCRText = getIntent().getStringExtra("text");

        FloatingActionButton fab = findViewById(R.id.newPictureFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultActivity.this, CameraActivity.class));
            }
        });

        bitmapManager = new InternalStorageManager(getApplicationContext(), "OCRPhoto", "lastPhoto");
        lastPhoto = bitmapManager.loadBitmapFromInternalStorage();

        if (lastPhoto != null) {
            mImageView.setImageBitmap(Bitmap.createScaledBitmap(lastPhoto, 960, 960, false));
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
        } else{
            // text from OCR
            ocr = new TextExtractor();
            if(lastPhoto != null) {
                //Text shows when OCR are processing the image
                mOCRTextView.setText(R.string.processing);
                String textRecognized = ocr.getTextFromImg(lastPhoto);
                if(textRecognized.equals("")) {
                    mOCRTextView.setText(R.string.no_text_found);
                } else {
                    mOCRTextView.setText(textRecognized);
                }
                // Saving in the preferences
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("extractedText", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("lastExtractedText", textRecognized);
                editor.apply();
            }
            else {
                Log.e("NOT_FOUND", "photo not found");
            }
        }
    }
}

