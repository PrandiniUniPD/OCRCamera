package unipd.se18.barcodemodule;

import android.graphics.Bitmap;

public interface BarcodeListener {

    public int BITMAP_NOT_FOUND = 0;

    public int BARCODE_NOT_FOUND = 1;

    public int DECODING_ERROR = 2;

    /**
     * Method called when barcode is successfully recognized
     * @param barcode String of the barcode detected
     */
    public void onBarcodeRecognized(String barcode);


    /**
     * Method called when the process fail
     * @param code int of en error code
     */
    public void onBarcodeRecognizedError(int code);
}
