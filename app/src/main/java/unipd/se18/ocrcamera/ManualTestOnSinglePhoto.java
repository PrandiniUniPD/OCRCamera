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

    private String startingOCRText;

    /**
     * Modification variable
     */
    private int angleRotation;

    /**
     * UI
     */
    private EditText degreeEditText;
    private TextView degreeTextView;
    private TextView confidenceTextView;
    private TextView warningTextView;
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
            warningTextView = findViewById(R.id.warningTextView);
            foundTextView = findViewById(R.id.foundTextView);

            SetResult(text);
        }

        @Override
        public void onTextRecognizedError(int code) {
            /*
             Text not correctly recognized
             Start CameraActivity to take a new photo
             */
            Intent i = new Intent(ManualTestOnSinglePhoto.this, CameraActivity.class);
            startActivity(i);
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //UI initialization
        setContentView(R.layout.activity_manual_test);
        degreeEditText = findViewById(R.id.editText1);


        //Get image path and text of the last image from preferences
        SharedPreferences prefs = this.getSharedPreferences("prefs", MODE_PRIVATE);
        String pathImage = prefs.getString("imagePath", null);
        startingOCRText = prefs.getString("text", null);

        final Bitmap photo = BitmapFactory.decodeFile(pathImage);

        Button fab = findViewById(R.id.btnConfirm);
        fab.setOnClickListener(new View.OnClickListener() {
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
     * @modify degreeTextView, confidenceTextView, warningTextView and foundTextView
     */
    private void SetResult(String text){
        String warning = findWarning(text, startingOCRText.length());
        String confidence = Double.toString(compareStrings(startingOCRText,text));

        degreeTextView.setText(angleRotation);
        confidenceTextView.setText(confidence);
        foundTextView.setText(text);
        warningTextView.setText(warning);
    }

    /**
     *Analise the given string and get the right warnings and add them on waring variable
     * warning are found
     * @param text String to analise
     * @param length int of length of starting string
     * @return  warnings
     */
    private String findWarning(java.lang.String text, int length) {
        String warnings = "";
        final int firstBound=10;
        final int secondBound=20;
        final int thirdBound=30;
        final int toleranceValue=4;


        if (text.equals("")) {
            warnings = getResources().getString(R.string.noTextWarning);
        } else if (text.length() < firstBound && length>firstBound+toleranceValue) {
            warnings = getResources().getString(R.string.lessThen10CharWarning);
        } else if (text.length() < secondBound && length>secondBound+toleranceValue) {
            warnings = getResources().getString(R.string.lessThen20CharWarning);
        } else if (text.length() < thirdBound && length>thirdBound+toleranceValue) {
            warnings = getResources().getString(R.string.lessThen30CharWarning);
        }

        return warnings;
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
     * @param string1 string to split and search in string2
     * @param string2 string where to search
     * @return percentage of similarity
     * see the link for more information {@link <a https://github.com/tdebatty/java-string-similarity/
     * blob/master/src/main/java/info/debatty/java/stringsimilarity/Damerau.java">link</a>}
     */
    public static double compareStrings (String string1, String string2) {

        Damerau damerau = new Damerau();
        //Percentage of distance based on string1 length
        double distance = damerau.distance(string1,string2)/string1.length()*100;
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
