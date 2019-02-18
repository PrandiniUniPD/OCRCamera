package unipd.se18.textrecognizer;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * Class that implements the common OCR interface for retrieving text from a Bitmap image.
 * This class uses ml-kit provided by Firebase.
 * More details at: {@link FirebaseVisionText}.
 * @see <a href="https://firebase.google.com/docs/ml-kit/android/recognize-text">
 *     ml-kit, recognize text</a>
 * @author Pietro Prandini (g2)
 */
class MlKitRecognizer implements OCR {
    /**
     * String used for the logs of this class.
     */
    private final String TAG = "MlKitRecognizer -> ";

    /**
     * The listener used to notify the result of the extraction
     */
    private OCRListener textExtractionListener;

    /*
    The next four int are used for recognizing the position of the FirebaseVisionText Objects.
    They could be useful for sorting the blocks or for an automatic recognition
    of the ingredients text block.
    The indexes are a clockwise order from the top-left corner.
    More details at:
    {@link FirebaseVisionText.TextBlock#getCornerPoints()},
    {@link FirebaseVisionText.Line#getCornerPoints()}
    or {@link FirebaseVisionText.Element#getCornerPoints()}.
     */
    private final int TOP_LEFT      = 0;
    private final int TOP_RIGHT     = 1;
    private final int BOTTOM_LEFT   = 2;
    private final int BOTTOM_RIGHT  = 3;

    /**
     * Constructor of this recognizer
     * @param textExtractionListener The listener used to notify the result of the extraction
     * @author Pietro Prandini (g2)
     */
    MlKitRecognizer(OCRListener textExtractionListener) {
        this.textExtractionListener = textExtractionListener;
    }

    /*
    The next method is required by the OCRInterface that avoid a single point of failure.
     */

    /**
     * Extracts a text from a given image.
     * More details at: {@link FirebaseVisionText}.
     * @param img The image in a Bitmap format
     * @author Pietro Prandini (g2)
     */
    public void getTextFromImg(Bitmap img) {
        // String used for the logs of this method
        final String methodTag = "getTextFromImg -> ";
        Log.d(TAG, methodTag + "launched");

        // Extracts the text from the pic
        extractFireBaseVisionText(img);
    }

    /**
     * Extracts a FirebaseVisionText from a given image.
     * More details at: {@link FirebaseVisionText}, {@link CountDownLatch},
     * {@link Task#addOnSuccessListener(OnSuccessListener)}, {@link OnSuccessListener}.
     * @param img The image in a Bitmap format
     * @author Pietro Prandini (g2)
     */
    private void extractFireBaseVisionText(Bitmap img) {
        // String used for the logs of this method
        final String methodTag = "extractFireBaseVisionText -> ";
        Log.d(TAG, methodTag + "launched");

        // Starts the time counter - useful for tests
        final long beforeWaiting = System.currentTimeMillis();

        // Settings the image to analyze
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(img);

        // Settings the "on device" analyzing method
        FirebaseVisionTextRecognizer textRecognizer =
                FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        // Settings the extraction task
        textRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(
                new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        Log.v(TAG, methodTag + "onSuccess ->\n"
                                + "-----      RECOGNIZED TEXT       -----"
                                + "\n" + firebaseVisionText.getText() + "\n"
                                + "----- END OF THE RECOGNIZED TEXT -----");

                        // Ends the time counter - useful for tests
                        long afterWaiting = System.currentTimeMillis();
                        Log.i(TAG, methodTag + "text extracted in "
                                + (afterWaiting - beforeWaiting) + " ms");

                        // Notify to the listener the result of the task
                        textExtractionListener.onTextRecognized(extractString(firebaseVisionText));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error: " + e);

                // Notify to the listener the result of the task
                textExtractionListener.onTextRecognizedError(OCRListener.FAILURE);
            }
        });
    }

    /**
     * Produces a String from a FirebaseVisionText
     * More details at: {@link FirebaseVisionText.TextBlock#getText()}.
     * @param firebaseVisionTextExtracted The result of the FirebaseVisionText extraction
     * @return String extracted by the FirebaseVisionText result
     * @author Pietro Prandini (g2)
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
     * Sorts the blocks recognized in an ArrayList
     * More details at: {@link FirebaseVisionText.TextBlock#getTextBlocks()}.
     * @param OCRResult FirebaseVisionText object produced by an OCR recognition
     * @return An ArrayList of FirebaseVisionText sorted
     * @author Pietro Prandini (g2)
     */
    private ArrayList<FirebaseVisionText.TextBlock> sortBlocks(FirebaseVisionText OCRResult) {
        ArrayList<FirebaseVisionText.TextBlock> OCRBlocks =
                new ArrayList<>(OCRResult.getTextBlocks());
        // Sorts the ArrayList of the blocks from top to bottom
        OCRBlocks = sortBlocksY(OCRBlocks);
        return OCRBlocks;
    }

    /**
     * Sorts the blocks recognized from top to bottom in an ArrayList
     * More details at: {@link FirebaseVisionText.TextBlock#getCornerPoints()}, {@link Comparator},
     * {@link Collections#sort(List, Comparator)}.
     * @param OCRBlocks ArrayList of FirebaseVisionText.TextBlock recognized by the OCR processing
     * @return An ArrayList of FirebaseVisionText sorted from top to bottom
     * @author Pietro Prandini (g2)
     */
    private ArrayList<FirebaseVisionText.TextBlock>
    sortBlocksY(ArrayList<FirebaseVisionText.TextBlock> OCRBlocks) {
        // Comparator for ordering the blocks by the y axis
        Comparator<FirebaseVisionText.TextBlock> mYComparator =
                new Comparator<FirebaseVisionText.TextBlock>() {
                    @Override
                    public int compare(FirebaseVisionText.TextBlock o1,
                                       FirebaseVisionText.TextBlock o2) {
                        int o1TopLeftY = Objects.requireNonNull(o1.getCornerPoints())[TOP_LEFT].y;
                        int o2TopLeftY = Objects.requireNonNull(o2.getCornerPoints())[TOP_LEFT].y;
                        return Integer.compare(o1TopLeftY,o2TopLeftY);
                    }
                };
        // Sorts the blocks
        Collections.sort(OCRBlocks, mYComparator);
        return OCRBlocks;
    }
}
