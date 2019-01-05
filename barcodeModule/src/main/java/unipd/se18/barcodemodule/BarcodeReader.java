package unipd.se18.barcodemodule;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * This classe implements the MLKit Api per barcode recognition
 */

public class BarcodeReader implements BarcodeInterface{

    private String code = "z";

    /**
     * implementation of the decodeBarcode method, that would detect the barcode from the given image
     * @param bitmap photo taken from the camera, to be analyzed.
     */
    @Override
    public String decodeBarcode(Bitmap bitmap) {

        //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        //final SharedPreferences.Editor editor = pref.edit();

        final CountDownLatch latch = new CountDownLatch(1);
        //get the firebase image from the bitmap
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        //get the detector
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector();
        //detect barcodes from the image given
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                            code = barcodes.get(0).getRawValue();
                            //check if the barcode is effectively read

                            latch.countDown();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                            //TODO se non trova un barcode da errore, fixare
                        Log.e("Barcode Fail", "Detecting barcode failed");
                    }
                });



        try {
            latch.await();
        }catch(InterruptedException e){
            //TODO gestire l'eccezione
            Log.e("ERROR", e.getMessage());
        }

        return code;
    }
}
