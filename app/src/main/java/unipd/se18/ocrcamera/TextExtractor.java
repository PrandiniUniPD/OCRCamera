package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.util.Log;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * Class that implements the common OCR interface for retrieving text from a Bitmap image.
 * This class uses ml-kit provided by Firebase.
 * @link https://firebase.google.com/docs/ml-kit/android/recognize-text
 * @link com.google.firebase.ml.vision.text.FirebaseVisionText
 * @author Pietro Prandini (g2)
 */
public class TextExtractor implements OCRInterface {
    /**
     * String used for the logs of this class.
     */
    private final String TAG = "TextExtractor";

    /*
    The next four int are used for recognizing the position of the FirebaseVisionText Objects.
    They could be useful for sorting the blocks or for an automatic recognition
    of the ingredients text block.
     */

    /**
     * Index of the point on the top-left position.
     * @link com.google.firebase.ml.vision.text.FirebaseVisionText
     * @link FirebaseVisionText.TextBlock getCornerPoint()
     * @link FirebaseVisionText.Line getCornerPoint()
     * @link FirebaseVisionText.Element getCornerPoint()
     */
    private final int TOP_LEFT = 0;

    /**
     * Index of the point on the top-right position.
     * @link com.google.firebase.ml.vision.text.FirebaseVisionText
     * @link FirebaseVisionText.TextBlock getCornerPoint()
     * @link FirebaseVisionText.Line getCornerPoint()
     * @link FirebaseVisionText.Element getCornerPoint()
     */
    private final int TOP_RIGHT = 1;

    /**
     * Index of the point on the bottom-left position
     * @link com.google.firebase.ml.vision.text.FirebaseVisionText
     * @link FirebaseVisionText.TextBlock getCornerPoint()
     * @link FirebaseVisionText.Line getCornerPoint()
     * @link FirebaseVisionText.Element getCornerPoint()
     */
    private final int BOTTOM_LEFT = 2;

    /**
     * Index of the point on the bottom-right position.
     * @link com.google.firebase.ml.vision.text.FirebaseVisionText
     * @link FirebaseVisionText.TextBlock getCornerPoint()
     * @link FirebaseVisionText.Line getCornerPoint()
     * @link FirebaseVisionText.Element getCornerPoint()
     */
    private final int BOTTOM_RIGHT = 3;

    /*
    The next method is required by the OCRInterface that avoid a single point of failure.
     */

    /**
     * Extracts a text from a given image.
     * @param img The image in a Bitmap format
     * @return The String of the ingredients recognized, empty String if nothing is recognized
     * @link com.google.firebase.ml.vision.text.FirebaseVisionText
     * @author Pietro Prandini (g2)
     */
    public String getTextFromImg(Bitmap img) {
        // String used for the logs of this method
        final String methodTag = "getTextFromImg -> ";
        Log.d(TAG, methodTag + "launched");

        // Extracts the text from the pic
        FirebaseVisionText firebaseVisionTextExtracted = extractFireBaseVisionText(img);

        // Returns the result
        if (firebaseVisionTextExtracted != null) {
            // Text found in img -> returns the String of the text found
            Log.v(TAG, methodTag + "Text found");
            return extractString(firebaseVisionTextExtracted);
        } else {
            // Text not found in img -> returns an empty string
            Log.v(TAG, methodTag + "Text not found");
            return "";
        }
    }

    /**
     * Extracts a FirebaseVisionText from a given image.
     * @param img The image in a Bitmap format
     * @return The FirebaseVisionText of the text recognized, null if nothing is recognized
     * @link FirebaseVisionText
     * @link java.util.concurrent.CountDownLatch
     * @author Pietro Prandini (g2), Luca Moroldo (g3)
     */
    private FirebaseVisionText extractFireBaseVisionText(Bitmap img) {
        // String used for the logs of this method
        final String methodTag = "extractFireBaseVisionText -> ";
        Log.d(TAG, methodTag + "launched");

        // Starts the time counter
        long beforeWaiting = java.lang.System.currentTimeMillis();

        // Settings the image to analyze
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(img);

        // Settings the "on device" analyzing method
        FirebaseVisionTextRecognizer textRecognizer =
                FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        // Instantiates the CountDownLatch used for synchronizing the extraction
        final CountDownLatch extraction = new CountDownLatch(1); // Luca Moroldo

        // Settings the extraction task
        Task<FirebaseVisionText> firebaseVisionTextTask =
                textRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(
                new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        Log.v(TAG, methodTag + "onSuccess ->\n"
                                + "-----      RECOGNIZED TEXT       -----"
                                + "\n" + firebaseVisionText.getText() + "\n"
                                + "----- END OF THE RECOGNIZED TEXT -----");
                        // Extraction ended - Analogous to signal
                        extraction.countDown(); // Luca Moroldo
                    }
                });
        // Waits until the extraction ends
        if (!firebaseVisionTextTask.isSuccessful()) {
            try {
                // Analogous to wait
                extraction.await(); // Luca Moroldo
            } catch (InterruptedException e) {
                Log.e(TAG, methodTag + "Synchronizing problem -> "
                        + "return null (nothing recognized)");
                return null;
            }

        }

        // Ends the time counter
        long afterWaiting = java.lang.System.currentTimeMillis();

        Log.i(TAG, methodTag + "text extracted in "
                + (afterWaiting - beforeWaiting) + " ms");

        // Returns the FirebaseVisionText recognized by the task
        return firebaseVisionTextTask.getResult();
    }

    /**
     * Produces a String from a FirebaseVisionText
     * @param firebaseVisionTextExtracted The result of the FirebaseVisionText extraction
     * @return String extracted by the FirebaseVisionText result
     */
    private String extractString(FirebaseVisionText firebaseVisionTextExtracted) {
        // Orders the blocks (requested by the issue #18 but not so useful at the moment)
        ArrayList<FirebaseVisionText.TextBlock> textBlocks =
                sortBlocks(firebaseVisionTextExtracted);

        // Prepares the String with the text of the blocks identified by the Firebase process
        StringBuilder text = new StringBuilder();
        for(FirebaseVisionText.TextBlock block: textBlocks) {
            text.append(block.getText()).append("\n");
        }
        String extractedText = text.toString();

        // Returns the String
        return extractedText;
    }

    /*
    Sorting methods are not so useful at the moment.
    There is an issue (#18) that request this method for a possible future use.
     */

    /**
     * Sorts the blocks recognized
     * @param OCRResult FirebaseVisionText object produced by an OCR recognition
     * @return An ArrayList of FirebaseVisionText sorted
     * @author Pietro Prandini (g2)
     */
    private ArrayList<FirebaseVisionText.TextBlock> sortBlocks(FirebaseVisionText OCRResult) {
        ArrayList<FirebaseVisionText.TextBlock> OCRBlocks =
                new ArrayList<>(OCRResult.getTextBlocks());
        OCRBlocks = sortBlocksY(OCRBlocks);
        return OCRBlocks;
    }

    /**
     * Sorts the blocks recognized from top to bottom
     * @param OCRBlocks ArrayList of FirebaseVisionText.TextBlock recognized by the OCR processing
     * @return An ArrayList of FirebaseVisionText sorted from top to bottom
     * @link java.util.Comparator
     * @link java.util.Collections sort(...)
     * @author Pietro Prandini (g2)
     */
    private ArrayList<FirebaseVisionText.TextBlock>
    sortBlocksY(ArrayList<FirebaseVisionText.TextBlock> OCRBlocks) {
        // Comparator for ordering the blocks by the y axis
        Comparator<FirebaseVisionText.TextBlock> mYComparator =
                new Comparator<FirebaseVisionText.TextBlock>() {
            @Override
            public int compare(FirebaseVisionText.TextBlock o1, FirebaseVisionText.TextBlock o2) {
                int o1TopLeftY = Objects.requireNonNull(o1.getCornerPoints())[TOP_LEFT].y;
                int o2TopLeftY = Objects.requireNonNull(o2.getCornerPoints())[TOP_LEFT].y;
                return Integer.compare(o1TopLeftY,o2TopLeftY);
            }
        };
        // Collections
        Collections.sort(OCRBlocks, mYComparator);
        return OCRBlocks;
    }
}