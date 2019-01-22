package unipd.se18.ocrcamera;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import info.debatty.java.stringsimilarity.Damerau;
import unipd.se18.ocrcamera.recognizer.OCR;
import unipd.se18.ocrcamera.recognizer.OCRListener;
import unipd.se18.ocrcamera.recognizer.TextRecognizer;

import static unipd.se18.ocrcamera.recognizer.TextRecognizer.getTextRecognizer;

/**
 * Class that compare the image stored image in preferences with the same image rotate
 * of a certain angle given by the user, showing the similarity
 */

public class ManualTestOnSinglePhoto extends AppCompatActivity {

    /**
     * Text get from OCR of the last image taken
     */
    private String startingOCRText;
    /**
     * Modification variable
     */
    private int angleRotation;

    /**
     * UI
     */
    private TextView degreeTextView;
    private TextView confidenceTextView;
    private TextView differenceLengthTextView;
    private TextView foundTextView;

    /**
     * Listener used by the extraction process to notify results
     */
    private OCRListener ocrListener = new OCRListener() {
        @Override
        public void onTextRecognized(String text) {
            /*
             Text correctly recognized
             Initialize Ui
             Start compare OCRText of the modified photo with the one with no rotation
             */
            setContentView(R.layout.activity_manual_test_result);
            degreeTextView = findViewById(R.id.degreeTextView);
            confidenceTextView =findViewById(R.id.confidenceTextView);
            differenceLengthTextView = findViewById(R.id.differenceLengthTextView);
            foundTextView = findViewById(R.id.foundTextView);

            setResult(text);
        }

        @Override
        public void onTextRecognizedError(int code) {
            /*
             Text not correctly recognized
             Start CameraActivity to take a new photo
             */
            Intent cameraActivity = new Intent(ManualTestOnSinglePhoto.this, CameraActivity.class);
            startActivity(cameraActivity);
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //UI initialization
        setContentView(R.layout.activity_manual_test);
        final EditText degreeEditText = findViewById(R.id.editText1);


        //Get image path and text of the last image from preferences
        SharedPreferences lastImagePref = this.getSharedPreferences("prefs", MODE_PRIVATE);
        String pathImage = lastImagePref.getString("imagePath", null);
        startingOCRText = lastImagePref.getString("text", null);

        final Bitmap photo = BitmapFactory.decodeFile(pathImage);

        Button confirmButton = findViewById(R.id.btnConfirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    angleRotation = Integer.parseInt(degreeEditText.getText().toString());

                    if (photo == null)
                        throw new PhotoNullException();

                    //Photo changes
                    Bitmap photoToCompare = rotateImage(photo, angleRotation);

                    // Instance of an OCR recognizer
                    OCR ocrProcess = getTextRecognizer(TextRecognizer.Recognizer.mlKit,
                            ocrListener);

                    // Runs the operations of text extraction
                    ocrProcess.getTextFromImg(photoToCompare);


                } catch (PhotoNullException e) {
                    Log.e(null, getResources().getString(R.string.errorLog1));
                    Intent takeANewPhoto = new Intent(ManualTestOnSinglePhoto.this, CameraActivity.class);
                    startActivity(takeANewPhoto);
                }
            }
        });
    }

    /**Based on the text received and startingOCRTText, analise information like confidence
     * and show them on textView
     * @param text string got from OCR
     * @modify degreeTextView, confidenceTextView, differenceLengthTextView and foundTextView
     */
    private void setResult(String text){
        String differenceLength = Integer.toString(startingOCRText.length()-text.length());
        String confidence = Double.toString(compareStrings(startingOCRText,text));

        degreeTextView.setText(angleRotation);
        confidenceTextView.setText(confidence);
        foundTextView.setText(text);
        differenceLengthTextView.setText(differenceLength);
    }

        /**
         * Rotate the bitmap image of the angle
         * @param source the image
         * @param angle angle of rotation
         * @return Bitmap image rotated
         */
    public static Bitmap rotateImage (Bitmap source,int angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    /** Calculate Damerau-Levenshtein distance between the strings and return similarity
     * @param firstString string to split and search in secondString
     * @param secondString string where to search
     * @return percentage of similarity
     * see the link for more information {@link <a https://github.com/tdebatty/java-string-similarity/
     * blob/master/src/main/java/info/debatty/java/stringsimilarity/Damerau.java">link</a>}
     */
    public static double compareStrings (String firstString, String secondString) {

        Damerau damerau = new Damerau();
        //Percentage of distance based on firstString length
        double distance = damerau.distance(firstString,secondString)/firstString.length()*100;
        return Math.round(100-distance);
    }
}

/**
 * New Exception
 */
class PhotoNullException extends Exception
{
    // Parameterless Constructor
    PhotoNullException() {}

    // Constructor that accepts a message
    PhotoNullException(String message)
    {
        super(message);
    }
}
