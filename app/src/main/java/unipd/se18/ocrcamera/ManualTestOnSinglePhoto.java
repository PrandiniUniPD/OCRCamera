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

import unipd.se18.ocrcamera.recognizer.OCR;
import unipd.se18.ocrcamera.recognizer.OCRListener;
import unipd.se18.ocrcamera.recognizer.TextRecognizer;

import static unipd.se18.ocrcamera.recognizer.TextRecognizer.getTextRecognizer;

/**
 * Class that compare the image stored image in preferences with the same image rotate of a certain angle,
 * showing the similarity value
 */

public class ManualTestOnSinglePhoto extends AppCompatActivity {

    private String startingOCRText;
    private final String errorLog1 = "No filePath preferences found";

    /**
     * Set default value in modify variable
     */
    private int angleRotation;
    private int illuminationValue;
    private int tiltValue;

    /**
     * Ui Initialization
     */
    private EditText degreeText;
    private EditText illuminationText;
    private EditText tiltText;
    private TextView textView;



    /**
     * Listener used by the extraction process to notify results
     */
    private OCRListener ocrListener = new OCRListener() {
        @Override
        public void onTextRecognized(String text) {
            /*
             Text correctly recognized
             -> prints it on the screen and saves it in the preferences
             */
            setContentView(R.layout.activity_manual_test_result);
            textView = findViewById(R.id.manualTextView);
            SetResult(text);
        }

        @Override
        public void onTextRecognizedError(int code) {
            /*
             Text not correctly recognized
             -> prints the error on the screen and saves it in the preferences
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
        degreeText = findViewById(R.id.editText1);
        illuminationText = findViewById(R.id.editText2);
        tiltText = findViewById(R.id.editText3);

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
                    angleRotation = Integer.parseInt(degreeText.getText().toString());
                    illuminationValue = Integer.parseInt(degreeText.getText().toString());
                    tiltValue = Integer.parseInt(degreeText.getText().toString());

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
                    String Tag="";
                    Log.e(Tag, errorLog1);
                    Intent takeANewPhoto = new Intent(ManualTestOnSinglePhoto.this, CameraActivity.class);
                    startActivity(takeANewPhoto);
                }
            }
        });
    }

    /**Based on the text received and startingOCRTText, analise informations like confidence
     * and show them on textView
     * @param text string got from OCR
     * @modify show on textView the result
     */
    private void SetResult(String text){

        final String firstPart = "Per la foto con rotazione di ";
        final String secondPart = " gradi: \nPercentuale di testo comune: ";
        final String thirdPart = "% \n Testo trovato: ";

        //Add information in defaultEntry
        String warning = findWarning(text, startingOCRText.length());
        int confidence = compareString(startingOCRText,text);

        String resultString = firstPart + angleRotation + secondPart + confidence
                +thirdPart + text
                +"\n"+warning;
        textView.setText(resultString);
    }


    /**
     *Analise the given string and get the right warnings and add them on waring variable
     * @param text String to analise
     * @param length int of length of starting string
     * @return  warnings
     */
    public String findWarning(String text, int length) {
        String warnings = "";
        final String noTextWarning = "OCR found no text in this photo";
        final String lessThen10CharWarning = "Less then 10 chars found by OCR";
        final String lessThen20CharWarning = "Less then 20 chars found by OCR";
        final String lessThen30CharWarning = "Less then 30 chars found by OCR";

        if (text.equals("")) {
            warnings = noTextWarning;
        } else if (text.length() < 10 && length>14) {
            warnings = lessThen10CharWarning;
        } else if (text.length() < 20 && length>26) {
            warnings = lessThen20CharWarning;
        } else if (text.length() < 30 && length>30) {
            warnings = lessThen30CharWarning;
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


    /** Split string1 in patterns and count how many times the patterns is found in in string2
     * @param stringToSplit string to split and search in string2
     * @param stringWhereToSearch string where to search
     * @return Bitmap image decoded
     */
    public static int compareString (String stringToSplit, String stringWhereToSearch) {
        int counter=0;
        String[] stringsToSplitArray = stringToSplit.split("\\W");
        String[] stringsWhereToSearchArray = stringWhereToSearch.split("\\W");
        for(String string1: stringsToSplitArray) {
            Boolean check=true;
            for(String string2: stringsWhereToSearchArray){
                if(string1.equals(string2) && check && string1.equals("")){
                    counter++;
                    check=false;
                }
            }
        }
        // % of matching
        return counter*100/stringsWhereToSearchArray.length;
    }
}


/**
 * New Exception
 */
class PhotoNullException extends Exception
{
    // Parameterless Constructor
    public PhotoNullException() {}

    // Constructor that accepts a message
    public PhotoNullException(String message)
    {
        super(message);
    }
}
