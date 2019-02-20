package unipd.se18.barcodemodule;

import android.graphics.Bitmap;

public interface BarcodeListener {


    /**
     * Method called when barcode is successfully recognized
     * @param barcode String of the barcode detected
     */
    public void onBarcodeRecognized(String barcode);


    /**
     * Method called when the process fail
     * @param error error message
     */
    public void onBarcodeRecognizedError(ErrorCode error);
}
