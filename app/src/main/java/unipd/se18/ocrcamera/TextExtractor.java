package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
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
     * Extracts a text from a given image.
     *
     * @param img The image in a Bitmap format
     * @return The String of the text recognized (empty String if nothing is recognized)
     * @author Pietro Prandini (g2)
     */
    public String getTextFromImg(Bitmap img) {
        Log.d(TAG, "getTextFromImg");
        return getIngredientsBlock(extractText(img));
    }

    /**
     * Extracts a text from a given image.
     *
     * @param img The image in a Bitmap format
     * @return The FirebaseVisionText of the text recognized, null if nothing is recognized
     * @author Pietro Prandini (g2), Luca Moroldo (g3)
     */
    private FirebaseVisionText extractText(Bitmap img) {
        Log.d(TAG, "extractText");
        long beforeWaiting = java.lang.System.currentTimeMillis();
        // Defines the image that will be analysed to get the text
        FirebaseVisionImage fbImage = FirebaseVisionImage.fromBitmap(img);
        // Defines that will be used an on device text recognizer
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();


        //latch used to wait for extraction to finish
        final CountDownLatch latch = new CountDownLatch(1);

        Task<FirebaseVisionText>fbText = textRecognizer.processImage(fbImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
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
            }
            catch (InterruptedException e) {
                Log.e(TAG,"Nothing recognized");
                return null;
            }

        }

        long afterWaiting = java.lang.System.currentTimeMillis();
        Log.i(TAG, "extractText -> text extracted in " + (afterWaiting - beforeWaiting) + " milliseconds");
        // Return the recognized text
        return fbText.getResult();
    }

    /**
     * Analyze the OCR result for recognizing only the ingredients
     * @param OCRResult FirebaseVisionText result of the OCR processing
     * @return The String of the ingredients recognized, empty String if nothing is recognized
     * @author Pietro Prandini (g2)
     */
    private String getIngredientsBlock(FirebaseVisionText OCRResult) {
        Log.i(TAG, "getIngredientsBlock");
        String ingredients = "";
        if(OCRResult != null) {
            for (FirebaseVisionText.TextBlock block : OCRResult.getTextBlocks()) {
                /*Log.v(TAG, "getIngredientsBlock -> block " + block.toString() + " ->\n-----      RECOGNIZED TEXT       -----\n"
                        + block.getText() + "\n----- END OF THE RECOGNIZED TEXT -----");*/
                for (FirebaseVisionText.Line line : block.getLines()) {
                    /*Log.v(TAG, "getIngredientsBlock -> line " + line.toString() + " ->\n-----      RECOGNIZED TEXT       -----\n"
                            + line.getText() + "\n----- END OF THE RECOGNIZED TEXT -----");*/
                    for (FirebaseVisionText.Element element : line.getElements()) {
                        /*Log.v(TAG, "getIngredientsBlock -> element " + element.toString() + " ->\n-----      RECOGNIZED TEXT       -----\n"
                                + element.getText() + "\n----- END OF THE RECOGNIZED TEXT -----");*/
                        if (element.getText().toLowerCase().contains("ingredients")) {
                            ingredients = block.getText();
                            Log.d(TAG, "getIngredientsBlock ->\n-----      RECOGNIZED TEXT       -----\n"
                                    + ingredients + "\n----- END OF THE RECOGNIZED TEXT -----");
                            break;
                        }
                    }
                }
            }
        }
        return ingredients;
    }
}