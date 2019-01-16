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
        // Checks the Bitmap is not null
        if(img == null) {
            textExtractionListener.onTextRecognizedError(OCRListener.BITMAP_IS_NULL_FAILURE);
            return;
        }

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
                        textExtractionListener.onTextRecognized(firebaseVisionText.getText());
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
}
