package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

        FloatingActionButton fab = findViewById(R.id.newPictureFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultActivity.this,CameraActivity.class));
            }
        });

        String OCRText = getIntent().getStringExtra("text");

        bitmapManager = new InternalStorageManager(getApplicationContext(), "OCRPhoto", "lastPhoto");
        Bitmap lastPhoto = bitmapManager.loadBitmapFromInternalStorage();

        if(lastPhoto != null) {
            mImageView.setImageBitmap(Bitmap.createScaledBitmap(lastPhoto, 960, 960, false));
        }
        else {
            Log.e("ResultActivity", "error retrieving last photo");
        }


        //Displaying the text, from OCR or preferences
        if(OCRText != null) {
            //Show the text of the last image
            mOCRTextView.setText(OCRText);
        }
        else{

            if(lastPhoto != null) {
                //Text shows when OCR are processing the image
                mOCRTextView.setText(R.string.processing);
                extractTextFromImage(lastPhoto);
            }
            else {
                Log.e("NOT_FOUND", "photo not found");
            }
        }


    }

    /**
     * Retrieves the text from the given byte array
     * @param bitmap the bitmap from wich the text will be extracted
     * @modify mOCRTextView It will contains the text extracts from the image
     * @modify sharedPreferences if recognized text is not null
     * @author Leonardo Rossi - Modified by Luca Moroldo
     */
    private void extractTextFromImage(Bitmap bitmap) {
        //Call to text extractor method to get the text from the given image
        TextExtractor extractor = new TextExtractor(this);
        extractor.getTextFromImg(bitmap);
        //Definition of the observer which will be responsible of updating the UI once the text extractor has finished its work
        android.arch.lifecycle.Observer<String> obsText = new android.arch.lifecycle.Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(s != null) {
                    if(s.equals(""))
                        mOCRTextView.setText(R.string.no_text_found);
                    else
                        mOCRTextView.setText(s);

                    //store on shared pref the extracted text
                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("extractedText", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("lastExtractedText", s);
                    editor.apply();
                }
            }
        };
        extractor.extractedText.observe(this, obsText);
    }
}
