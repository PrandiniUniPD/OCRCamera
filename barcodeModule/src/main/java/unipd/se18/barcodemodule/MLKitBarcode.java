package unipd.se18.barcodemodule;

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
 * This class implements the MLKit Api for barcode recognition
 */

public class MLKitBarcode implements Barcode{

    private String code = "";

    /**
     * implementation of the decodeBarcode method, that would detect the barcode from the given image
     * @param bitmap photo taken from the camera, to be analyzed.
     */
    @Override
    public String decodeBarcode(Bitmap bitmap) {


        final CountDownLatch latch = new CountDownLatch(1);
        //get the firebase image from the bitmap


        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        //get the detector
        final FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector();
        //detect barcodes from the image given

        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)

                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        if (!(barcodes.isEmpty())) {
                            code = barcodes.get(0).getRawValue();
                        }else{
                            code= "";
                        }
                        latch.countDown();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        latch.countDown();
                        Log.e("SGAIO", e.getMessage());
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
