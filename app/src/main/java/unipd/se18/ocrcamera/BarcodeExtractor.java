package unipd.se18.ocrcamera;


import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Class which try to extract the content of a barcode in an image
 * @author Luca Perali (inspired to Pietro Prandini's TextExtractor)
 */
public class BarcodeExtractor {

    private static final String TAG = "BarcodeTextExtractor";

    /**
     * Constructor
     */
    BarcodeExtractor(){
        //Nothing to initialize yet
    }

    /**
     * This method try to extract barcode information from an image if can,
     * otherwise return an empty string.
     * (The method has the same name of OCRInterface methods for eventual future implementation of a common
     * interface)
     *
     * @param img Bitmap image with or without barcode in it
     * @return string with barcode info if barcode is found, empty if no barcode is found
     * @author Luca Perali
     */
    public String getTextFromImg(Bitmap img){

        Log.d(TAG, "extractTextFromImage");
        long initialTime = java.lang.System.currentTimeMillis();

        // Firebase implementation tutorial at https://firebase.google.com/docs/ml-kit/android/read-barcodes
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(img);
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector();

        //latch used to wait barcode extraction to complete
        //https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/CountDownLatch.html
        final CountDownLatch latch = new CountDownLatch(1);

        Task<List<FirebaseVisionBarcode>> task = detector.detectInImage(image) // this start an asynchronous thread
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        Log.v(TAG, "extractText -> onSuccess ->\n" +
                                "-----      RECOGNIZED BARCODE       -----\n"
                                + getInfoFromBarcode(barcodes) + "\n" +
                                "----- END OF THE RECOGNIZED TEXT -----");

                        //analogous to signal
                        latch.countDown();
                    }
                });
        /*         .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            // ...
                        }
                    });*/

        // if barcode is not detected yet
        if(!task.isSuccessful()) {
            try
            {
                //wait until barcode is detected
                latch.await();
            }
            catch (InterruptedException e)
            {

                return "Failed to extract text.";
            }
        }

        long detectionTime = java.lang.System.currentTimeMillis() - initialTime;
        Log.i(TAG, "extractText -> text extracted in " +
                detectionTime +
                " milliseconds");

        // Return the recognized text
        return getInfoFromBarcode(task.getResult());
    }

    /**
     * Get a string from the Firebase barcodes list object.
     *
     * @param barcodes rapresents the list of barcode detected by Firebase library
     * @return the string conversion of the input param, empty string if no barcode is detected
     * @author Luca Perali
     */
    private static String getInfoFromBarcode(List<FirebaseVisionBarcode> barcodes) {
        //StringBuilder result = new StringBuilder();
        for (FirebaseVisionBarcode barcode : barcodes) {
            //result.append(barcode.getRawValue() + "\n");
            return barcode.getRawValue(); //TODO attention: multiple barcode is not managed!
        }
        //return result.toString();

        return ""; //if no barcode is detected
    }
}
