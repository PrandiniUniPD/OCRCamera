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
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * This class implements the MLKit Api for barcode recognition
 * @author Andrea Ton
 */
public class MLKitBarcode implements Barcode{

    //actual barcode String returned by main method decodeBarcode
    private String barcode = "";
    //text of the decoding error
    private String decodeERROR = "ERROR: Barcode decoding unsuccessful, please try again.";
    //text of the possible exception related to the CountdownLatch
    private String latchERROR = "Fatal error! Please try again.";
    //possibility to limit barcode format recognized to improve performance
    private FirebaseVisionBarcodeDetectorOptions options =
            new FirebaseVisionBarcodeDetectorOptions.Builder()
                    .setBarcodeFormats(
                            FirebaseVisionBarcode.FORMAT_ALL_FORMATS).build();
    //cause of different possible implementations, the detector instantiation is leaved detached
    private FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

    /**
     * implementation of the decodeBarcode method, that would detect the barcode from the given image
     * @param bitmap photo taken from the camera, to be analyzed.
     * @return barcode String that represent the barcode scanned in the image
     */
    @Override
    public String decodeBarcode(Bitmap bitmap) {
        //using a countdown latch to manage processing time and concurrency
        final CountDownLatch latch = new CountDownLatch(1);
        //using firebase .fromBitmap method to get an analyzable image from a given bitmap
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        //Get the detector, modularized from the main method, so it's possible to make independent changes
        //method to find and decode a barcode in the image
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                //adding a listener for the success result
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    //it gets a List of barcodes as parameter, which is the output of the barcode detector
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        if (!(barcodes.isEmpty())) {
                            //if not empty get the string of the barcode value
                            barcode = barcodes.get(0).getRawValue();
                        }else{
                            //if no barcode is found, return empty string
                            barcode= "";
                        }
                        //get the countdown to zero in order to proceed with the method
                        latch.countDown();
                    }
                })
                //then adding the listener for the case of failure
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        latch.countDown();
                        //in case of failure return a string that report the error occurred
                        barcode = decodeERROR;
                        Log.e("Error Barcode", e.getMessage());
                    }
                });
        try {
            //waiting for the latch to be 0
            latch.await();
        }catch(InterruptedException e){
            Log.e("Latch exception", e.getMessage());
            return latchERROR;
        }
        return barcode;
    }


}
