package unipd.se18.ocrcamera;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.concurrent.CountDownLatch;
import java.util.ArrayList;
import java.util.List;
import unipd.se18.ocrcamera.recognizer.OCR;
import unipd.se18.ocrcamera.recognizer.OCRListener;
import unipd.se18.ocrcamera.recognizer.TextRecognizer;

import static unipd.se18.ocrcamera.recognizer.TextRecognizer.getTextRecognizer;

/**
 * Class that compare the image stored image in preferences with the same image rotate of a certain angle,
 * showing the similarity value
 */

public class ManualTestOnSinglePhoto extends AppCompatActivity {

    private TextView textView;
    private String Tag;
    /**
     * Value of rotation angle and number of rotation
     */
    private final int angleRotation = 360;
    private final int numberOfRotation = 360 / angleRotation; // number of possible rotation fo angleRotation degrees

    private final String firstPartRotation = "Per la foto con rotazione di ";
    private final String secondPart = ": \nPercentuale di testo comune: ";

    private List<InformationEntry> resultList = new ArrayList<InformationEntry>();

    public enum Type {ROTATION} //Da aggiungere con l'aggiunta dei metodi

    /**
     * Async Controller inizialize
     */
    private CountDownLatch signal = new CountDownLatch(1);
    ;

    /**
     * Entry used to exchange information
     */
    private InformationEntry defaultEntry = new InformationEntry();

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
            defaultEntry.setOCRText(text);
            signal.countDown();
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
        setContentView(R.layout.manual_test_activity);
        textView = findViewById(R.id.manualTextView);
        textView.setMovementMethod(new ScrollingMovementMethod());

        CountDownLatch signal = new CountDownLatch(1);
        String resultString="";

        try {
            //Get image path and text of the last image from preferences
            SharedPreferences prefs = this.getSharedPreferences("prefs", MODE_PRIVATE);
            String pathImage = prefs.getString("imagePath", null);
            String OCRText = prefs.getString("text", null);

            Bitmap photo = BitmapFactory.decodeFile(pathImage);
            if (photo == null) {
                Log.e(Tag, "No filePath preferences found");
                throw new PhotoNullException();
            }

            int rotation=0;
            for(int i=0; i<numberOfRotation;i++) {
                Bitmap photoToCompare = rotateImage(photo, rotation);
                // create and start OCR thread
                Runnable test = new Worker(signal, photoToCompare, ocrListener);

                signal.await();

                //Add information in defaultEntry
                AddWarningAndSimilarity(OCRText);
                defaultEntry.setVariation(rotation);
                defaultEntry.setSimilarity(compareString(defaultEntry.getOCRText(),OCRText));
                defaultEntry.setTypeROTATION();
                resultList.add(defaultEntry.clone());
                rotation += angleRotation;
            }
            Log.e(Tag,"registration rotation photo complete");

            for(InformationEntry element : resultList)
            {
                if(element.getType()==Type.ROTATION)
                {
                    resultString += firstPartRotation+ angleRotation+ secondPart+ element.getSimilarity();
                }
            }


            textView.setText(resultString);


        } catch (PhotoNullException e) {
            Log.e(Tag, "No preferences found");
            Intent takeANewPhoto = new Intent(ManualTestOnSinglePhoto.this, CameraActivity.class);
            startActivity(takeANewPhoto);
        } catch (InterruptedException ex) {
            Log.e(Tag, "CountDownLatch error");
            Intent takeANewPhoto = new Intent(ManualTestOnSinglePhoto.this, CameraActivity.class);
            startActivity(takeANewPhoto);
        }

    }


    /**
     *Add warinings and similarity with standard text in defaulEntry
     * @param standardText referenced text that has to be compared with the one got from defaultEntry
     * @modify defaulEntry add warinings and similarity in defaulEntry
     * @throws PhotoNullException when text stored in defaulEntry is null
     */
    private void AddWarningAndSimilarity(String standardText) throws PhotoNullException
    {

        String warning = "";
        final String noTextWarning = "OCR found no text in this photo";
        final String lessThen10CharWarning = "Less then 10 chars found by OCR";
        final String lessThen20CharWarning = "Less then 20 chars found by OCR";
        final String lessThen30CharWarning = "Less then 30 chars found by OCR";
        String textToCompare = defaultEntry.getOCRText();
        if(textToCompare==null)
            throw new PhotoNullException();

        if (textToCompare.equals("")) {
            warning = noTextWarning;
        } else if (textToCompare.length() < 10) {
            warning = lessThen10CharWarning;
        } else if (textToCompare.length() < 20) {
            warning = lessThen20CharWarning;
        } else if (textToCompare.length() < 30) {
            warning = lessThen30CharWarning;
        }

        defaultEntry.setSimilarity(compareString(textToCompare, standardText));
        defaultEntry.setWarning(warning);
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
     * @param string1 string to split and search in string2
     * @param string2 string where to search
     * @return Bitmap image decoded
     */
    public static int compareString (String string1, String string2) {
        int i=0;
        String[] patterns = string1.split("\\W");
        if(KMP(patterns[i],string2)!=-1)
        {
            //Count the time a string is
            i++;
        }
        // % of matching
        return i/patterns.length*100;
    }

    /**KMP pattern matching algorithm
     * @param search String where to search
     * @param target String to search
     * @return the index of the match, -1 if no match found
     */
    public static int KMP(String search, String target) {
        int[] failureTable = failureTable(target);

        int targetPointer = 0; // current char in target string
        int searchPointer = 0; // current char in search string

        while (searchPointer < search.length()) { // while there is more to search with, keep searching
            if (search.charAt(searchPointer) == target.charAt(targetPointer)) { // case 1
                // found current char in targetPointer in search string
                targetPointer++;
                if (targetPointer == target.length()) { // found all characters
                    int x = target.length() + 1;
                    return searchPointer - x; // return starting index of found target inside searched string
                }
                searchPointer++; // move forward if not found target string
            } else if (targetPointer > 0) { // case 2
                // use failureTable to use pointer pointed at nearest location of usable string prefix
                targetPointer = failureTable[targetPointer];
            } else { // case 3
                // targetPointer is pointing at state 0, so restart search with current searchPointer index
                searchPointer++;
            }
        }
        return -1;
    }

    /**
     * Returns an int[] that points to last valid string prefix, given target string
     */
    public static int[] failureTable(String target) {
        int[] table = new int[target.length() + 1];
        // state 0 and 1 are guarenteed be the prior
        table[0] = -1;
        table[1] = 0;

        // the pointers pointing at last failure and current satte
        int left = 0;
        int right = 2;

        while (right < table.length) { // RIGHT NEVER MOVES RIGHT UNTIL ASSIGNED A VALID POINTER
            if (target.charAt(right - 1) == target.charAt(left)) { // when both chars before left and right are equal, link both and move both forward
                left++;
                table[right] = left;
                right++;
            }  else if (left > 0) { // if left isn't at the very beginning, then send left backward
                // by following the already set pointer to where it is pointing to
                left = table[left];
            } else { // left has fallen all the way back to the beginning
                table[right] = left;
                right++;
            }
        }
        return table;
    }
}


/**
 * Runnable class that call getTextFromImg in a different thread to allow async tasks
 */

class Worker implements Runnable {
    private final CountDownLatch signal;
    private Bitmap photo;
    private OCRListener ocrListener;

    Worker(CountDownLatch signal, Bitmap photo, OCRListener ocrListener) {
        this.signal = signal;
        this.photo = photo;
        this.ocrListener = ocrListener;
    }

    public void run() {
        // Instance of an OCR recognizer
        OCR ocrProcess = getTextRecognizer(TextRecognizer.Recognizer.mlKit,
                ocrListener);

        // Runs the operations of text extraction
        ocrProcess.getTextFromImg(photo);
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

/**
 * Entry to store information
 */
class InformationEntry
{
    private String OCRText;
    private String warning;
    private int variation;
    private int similarity;
    private ManualTestOnSinglePhoto.Type type;

    //Constructor
    public InformationEntry(String OCRText, String warning)
    {
        this.OCRText=OCRText;
        this.warning=warning;
    }

    //Constructor
    public InformationEntry(){
        this.OCRText=null;
        this.similarity=-1;
        this.type=null;
        this.variation=-1;
        this.warning=null;
    }

    public void setOCRText(String OCRText) {this.OCRText = OCRText;}

    public void setVariation(int variation) { this.variation = variation; }

    public void setSimilarity(int similarity) {this.similarity = similarity;}

    public void setTypeROTATION(){type=ManualTestOnSinglePhoto.Type.ROTATION;}

    public void setType(ManualTestOnSinglePhoto.Type type){this.type=type;}

    public void setWarning(String warning){this.warning= warning;}

    public String getOCRText() {return OCRText;}

    public String getWarning() {return warning;}

    public int getVariation() {return variation;}

    public int getSimilarity() {return similarity;}

    public ManualTestOnSinglePhoto.Type getType() {return type;}

    public InformationEntry clone(){
        InformationEntry newEntry=new InformationEntry(this.OCRText, this.warning);

        newEntry.setSimilarity(this.similarity);
        newEntry.setVariation(this.variation);
        newEntry.setType(this.type);

        return newEntry;
    }
}


