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
 */
public class BarcodeExtractor {

    private static final String TAG = "BarcodeTextExtractor";

    /**
     * Constructor
     */
    BarcodeExtractor(){
        //Nothing to initialize
    }

    /**
     * This method try to extract barcode information from an image if can,
     * otherwise return an empty string.
     *
     * @param img Bitmap image with or without barcode in it
     * @return string with barcode info if barcode found
     */
    public String getTextFromImg(Bitmap img){

        Log.d(TAG, "extractTextFromImage");
        final String rawBarcode;
        long beforeWaiting = java.lang.System.currentTimeMillis();

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(img);
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector();

        //latch used to wait for extraction to finish
        final CountDownLatch latch = new CountDownLatch(1);

        Task<List<FirebaseVisionBarcode>> task = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {

                        Log.v(TAG, "extractText -> onSuccess ->\n-----      RECOGNIZED BARCODE       -----\n"
                                + getInfoFromBarcode(barcodes) + "\n----- END OF THE RECOGNIZED TEXT -----");

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

        if(!task.isSuccessful()) {
            try
            {
                //analogous to wait
                latch.await();
            }
            catch (InterruptedException e)
            {

                return "Failed to extract text.";
            }
        }

        long afterWaiting = java.lang.System.currentTimeMillis();
        Log.i(TAG, "extractText -> text extracted in " +
                (afterWaiting - beforeWaiting) +
                " milliseconds");

        // Return the recognized text
        String barcodeResult = getInfoFromBarcode(task.getResult());
        return barcodeResult;

    }

    private static String getInfoFromBarcode(List<FirebaseVisionBarcode> barcodes) {
        StringBuilder result = new StringBuilder();
        for (FirebaseVisionBarcode barcode : barcodes) {
            //int valueType = barcode.getValueType();
            result.append(barcode.getRawValue() + "\n");
        }
        return result.toString();
    }
}
