package unipd.se18.barcodemodule;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
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
 * @author Andrea Ton
 */
public class MLKitBarcode implements Barcode{

    private String barcode = "";
    private static final String DECODE_ERROR = "ERROR: Barcode decoding unsuccessful, please try again.";
    private final FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector();
    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * implementation of the decodeBarcode method, that would detect the barcode from the given image
     * @param bitmap photo taken from the camera, to be analyzed.
     * @return barcode String that represent the barcode scanned in the image
     */
    @Override
    public String decodeBarcode(Bitmap bitmap) {

        //get the FirebaseImage obj from the bitmap
        final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        detectTheBarcode(image);

        //wait for the detection to be completed
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return barcode;
    }

    /**
     * Take the image and detect the barcodes in it using Firebase
     * @param image the image that will be scanned from Firebase
     * @modify barcode is set with the string of the code retrieved
     */
    private void detectTheBarcode(FirebaseVisionImage image) {

        //Firebase method to detect barcodes inside an image
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)

                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {

                    //on success - set the barcode to the string retrieved from the image, empty string if no barcode is found
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        if (!barcodes.isEmpty()) {
                            barcode = barcodes.get(0).getRawValue();
                        }else{
                            barcode= "";
                        }
                        latch.countDown();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {

                    //on fail - return an error on decoding the barcode
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        barcode = DECODE_ERROR;
                        latch.countDown();
                    }
                });
    }

}
