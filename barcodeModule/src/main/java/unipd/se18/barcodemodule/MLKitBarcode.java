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

/**
 * This class implements the MLKit Api for barcode recognition
 * @author Andrea Ton
 */
public class MLKitBarcode implements Barcode{

    //Firebase detector object needed to perform a scan on the given image
    private final FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector();

    private BarcodeListener barcodeListener;

    /**
     *constructor of the class
     * @param listener
     */
    public MLKitBarcode (BarcodeListener listener){
        if (listener != null){
            barcodeListener = listener;
        }else{
            throw new IllegalArgumentException("BarcodeListener must be provided");
        }
    }

    /**
     * implementation of the decodeBarcode method, that would detect the barcode from the given image
     * @param bitmap photo taken from the camera, to be analyzed.
     * @return barcode String that represent the barcode scanned in the image
     */
    @Override
    public void decodeBarcode(Bitmap bitmap) {

        if (bitmap == null){
            throw new IllegalArgumentException("No photo was found");
        }

        //get the FirebaseImage obj from the bitmap
        final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        detectTheBarcode(image);

    }

    /**
     * Take the image and detect the barcodes in it using Firebase
     * @param image the image that will be scanned from Firebase
     */
    private void detectTheBarcode(final FirebaseVisionImage image) {

        //Firebase method to detect barcodes inside an image
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)

                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {

                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        if (!barcodes.isEmpty()) {
                            barcodeListener.onBarcodeRecognized(barcodes.get(0).getRawValue());
                        }
                        else {
                            barcodeListener.onBarcodeRecognizedError(ErrorCode.BARCODE_NOT_FOUND);
                        }
                    }
                })

                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        barcodeListener.onBarcodeRecognizedError(ErrorCode.DECODING_ERROR);
                    }
                });
    }

}
