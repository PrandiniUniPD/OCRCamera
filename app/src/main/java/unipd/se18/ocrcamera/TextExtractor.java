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
 * Class the implements the common OCR interface to retrieve text from an image
 * @author Pietro Prandini (g2)
 */
public class TextExtractor implements OCRInterface {

    /**
     * TAG used for the logs of this class
     */
    private final String TAG = "TextExtractor";

    /**
     * Index of the point on the top-left position
     */
    private final int TOP_LEFT = 0;

    /**
     * Index of the point on the top-right position
     */
    private final int TOP_RIGHT = 1;

    /**
     * Index of the point on the bottom-left position
     */
    private final int BOTTOM_LEFT = 2;

    /**
     * Index of the point on the bottom-right position
     */
    private final int BOTTOM_RIGHT = 3;

    /**
     * Extracts a text from a given image.
     * @param img The image in a Bitmap format
     * @return The String of the ingredients recognized (empty String if nothing is recognized)
     * @author Pietro Prandini (g2)
     */
    public String getTextFromImg(Bitmap img) {
        Log.d(TAG, "getTextFromImg");
        FirebaseVisionText textExtracted = extractFireBaseVisionText(img);
        if (textExtracted != null) {
            StringBuilder text = new StringBuilder();
            for(FirebaseVisionText.TextBlock block: sortBlocks(textExtracted)) {
                text.append(block.getText()).append("\n");
            }
            return text.toString();
        } else {
            return "";
        }
    }

    /**
     * Extracts a text from a given image.
     * @param img The image in a Bitmap format
     * @return The FirebaseVisionText of the text recognized, null if nothing is recognized
     * @author Pietro Prandini (g2), Luca Moroldo (g3)
     */
    private FirebaseVisionText extractFireBaseVisionText(Bitmap img) {
        Log.d(TAG, "extractText");
        long beforeWaiting = java.lang.System.currentTimeMillis();
        // Defines the image that will be analysed to get the text
        FirebaseVisionImage fbImage = FirebaseVisionImage.fromBitmap(img);
        // Defines that will be used an on device text recognizer
        FirebaseVisionTextRecognizer textRecognizer =
                FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        //latch used to wait for extraction to finish
        final CountDownLatch latch = new CountDownLatch(1);

        Task<FirebaseVisionText> fbText = textRecognizer.processImage(fbImage).addOnSuccessListener(
                new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        Log.v(TAG, "extractText -> onSuccess ->\n"
                                + "-----      RECOGNIZED TEXT       -----"
                                + "\n" + firebaseVisionText.getText() + "\n"
                                + "----- END OF THE RECOGNIZED TEXT -----");
                        //analogous to signal
                        latch.countDown();
                    }
                });

        if (!fbText.isSuccessful()) {
            try {
                //analogous to wait
                latch.await();
            } catch (InterruptedException e) {
                Log.e(TAG, "Nothing recognized");
                return null;
            }

        }

        long afterWaiting = java.lang.System.currentTimeMillis();
        Log.i(TAG, "extractText -> text extracted in "
                + (afterWaiting - beforeWaiting) + " milliseconds");
        // Return the recognized text
        return fbText.getResult();
    }
    /**
     * Sorts the blocks recognized
     * @param OCRResult FirebaseVisionText object produced by an OCR recognition
     * @return An ArrayList of FirebaseVisionText sorted
     * @author Pietro Prandini (g2)
     */
    private ArrayList<FirebaseVisionText.TextBlock> sortBlocks(FirebaseVisionText OCRResult) {
        ArrayList<FirebaseVisionText.TextBlock> OCRBlocks = new ArrayList<>(OCRResult.getTextBlocks());
        OCRBlocks = sortBlocksY(OCRBlocks);
        return OCRBlocks;
    }

    /**
     * Sorts the blocks recognized from top to bottom
     * @param OCRBlocks ArrayList of FirebaseVisionText.TextBlock recognized by the OCR processing
     * @return An ArrayList of FirebaseVisionText sorted from top to bottom
     * @author Pietro Prandini (g2)
     */
    private ArrayList<FirebaseVisionText.TextBlock> sortBlocksY(ArrayList<FirebaseVisionText.TextBlock> OCRBlocks) {
        // Comparator for ordering the blocks by the y axis
        Comparator<FirebaseVisionText.TextBlock> mYComparator = new Comparator<FirebaseVisionText.TextBlock>() {
            @Override
            public int compare(FirebaseVisionText.TextBlock o1, FirebaseVisionText.TextBlock o2) {
                int o1TopLeftY = Objects.requireNonNull(o1.getCornerPoints())[TOP_LEFT].y;
                int o2TopLeftY = Objects.requireNonNull(o2.getCornerPoints())[TOP_LEFT].y;
                return Integer.compare(o1TopLeftY,o2TopLeftY);
            }
        };
        Collections.sort(OCRBlocks, mYComparator);
        return OCRBlocks;
    }
}