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

/**
 * This classe implements the MLKit Api per barcode recognition
 */

public class BarcodeReader implements BarcodeInterface{


    /**
     * implementation of the decodeBarcode method, that would detect the barcode from the given image
     * @param context Context of the resultActivity, used to use the shared preferences to retrieve barcode value
     *                //TODO delete this and find another way
     * @param bitmap photo taken from the camera, to be analyzed.
     */

    @Override
    public void decodeBarcode(Context context, Bitmap bitmap) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = pref.edit();
        //get the firebase image from the bitmap
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        //get the detector
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector();
        //detect barcodes from the image given
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                            //this can be replaced with barcodes.get(0).getRawValue();
                        for (FirebaseVisionBarcode barcode: barcodes) {
                            //get the
                            String code = barcode.getRawValue();
                            //check if the barcode is effectively read
                            Log.i("CODE!!!", code);
                            //put the barcode value in the shared preferences as a String
                            editor.putString("BARCODE", code);
                            editor.commit();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Barcode Fail", "Detecting barcode failed");
                    }
                });

    }
}
