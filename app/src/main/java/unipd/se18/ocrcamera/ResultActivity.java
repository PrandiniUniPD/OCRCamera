package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Class used for showing the result of the OCR processing
 */
public class ResultActivity extends AppCompatActivity {

    /**
     * The ImageView of the captured photo.
     */
    private ImageView mImageView;

    /**
     * The TextView of the extracted test from the captured photo.
     */
    private TextView mOCRTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // UI components
        mImageView = findViewById(R.id.img_captured_view);
        mOCRTextView = findViewById(R.id.ocr_text_view);
        mOCRTextView.setMovementMethod(new ScrollingMovementMethod());


        //Retrieving captured image data and text from intent
        String pathImage = getIntent().getStringExtra("imageDataPath");
        String OCRText = getIntent().getStringExtra("text");
        Log.d("ResultActivity", "OCRText: -->" + OCRText + "<--");

        //Displaying the captured image to the user
        //displayImageFromByteArray(pathImage);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(pathImage));

        //Displaying the text, from OCR or preferences
        if(OCRText != null) {
            //Show the text of the last image
            mOCRTextView.setText(OCRText);
        }
        else{
            //Utilization of OCR to retrieve text from the given image
            extractTextFromImage(pathImage);
        }

        //Text shows when OCR are processing the image
        mOCRTextView.setText(R.string.processing);
    }

    // TODO check if unusued
    /**
     * Displays the captured image into UI given a specific byte array
     * @param path A string that contains the path where is temporary saved the captured image. Not null.
     * @modify mImageView The image view that is modified by the method
     * @author Leonardo Rossi
     */
    /*private void displayImageFromByteArray(String path) {
        File file = new File(path);
        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
        //bmp = rotateBitmap90Degrees(bmp);
        mImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 300, 300, false));
    }*/

    /**
     * Retrieves the text from the given byte array
     * @param path A string that contains the path where is temporary saved the captured image. Not null.
     * @modify mOCRTextView It will contains the text extracts from the image
     * @author Leonardo Rossi
     */
    private void extractTextFromImage(String path) {
        //Converting byte array into bitmap
        File file = new File(path);
        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
        //bmp = rotateBitmap90Degrees(bmp);
        //Call to text extractor method to get the text from the given image
        TextExtractor extractor = new TextExtractor(this);
        extractor.getTextFromImg(bmp);
        //Definition of the observer which will be responsible of updating the UI once the text extractor has finished its work
        android.arch.lifecycle.Observer<String> obsText = new android.arch.lifecycle.Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(s != null && s.equals("")) {
                    mOCRTextView.setText(R.string.no_text_found);
                } else if (s != null) {
                    mOCRTextView.setText(s.toUpperCase());
                }
            }
        };
        extractor.extractedText.observe(this, obsText);
    }

    //TODO Resolve this bug (this method resolve the bug only for some devices (i.e.: to an Android 8.1 (API Level 27) the app works without this method and it is not work with it))
    /**
     * Rotate bitmap image counter clockwise by 90 degrees
     * @param bmp original bitmap image
     * @author Group 3
     */
    /*private Bitmap rotateBitmap90Degrees(Bitmap bmp){
        Matrix matrix = new Matrix();
        matrix.postRotate(90); // anti-clockwise by 90 degrees
        bmp = Bitmap.createBitmap(bmp , 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        return bmp;
    }*/

}
