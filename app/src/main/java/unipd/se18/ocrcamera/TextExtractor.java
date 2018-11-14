package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

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
     * Constructor
     */
    TextExtractor() {    }

    /**
     * Extracts a text from a given image.
     *
     * @param img The image in a Bitmap format
     * @return The String of the text recognized (empty String if nothing is recognized)
     * @author Pietro Prandini (g2)
     */
    public String getTextFromImg(Bitmap img) {
        Log.d(TAG, "getTextFromImg");
        return extractText(img);
    }

    /**
     * Extracts a text from a given image.
     *
     * @param img The image in a Bitmap format
     * @return The String of the text recognized (empty String if nothing is recognized)
     * @author Pietro Prandini (g2)
     */
    private String extractText(Bitmap img) {
        Log.d(TAG, "extractText");
        long beforeWaiting = java.lang.System.currentTimeMillis();
        // Defines the image that will be analysed to get the text
        FirebaseVisionImage fbImage = FirebaseVisionImage.fromBitmap(img);
        // Defines that will be used an on device text recognizer
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();


        //latch used to wait for extraction to finish
        final CountDownLatch latch = new CountDownLatch(1);

        Task<FirebaseVisionText>fbText = textRecognizer.processImage(fbImage)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                Log.v(TAG, "extractText -> onSuccess ->\n-----      RECOGNIZED TEXT       -----\n"
                        + firebaseVisionText.getText() + "\n----- END OF THE RECOGNIZED TEXT -----");

                //analogous to signal
                latch.countDown();
            }
        });

        if(!fbText.isSuccessful()) {
            try {

                //analogous to wait
                latch.await();
            } catch (InterruptedException e) {
                return "Failed to extract text.";
            }

        }

        long afterWaiting = java.lang.System.currentTimeMillis();
        Log.i(TAG, "extractText -> text extracted in " + (afterWaiting - beforeWaiting) + " milliseconds");
        // Return the recognized text
        String ocrResult = fbText.getResult().getText();
        return ocrResult;
    }
}