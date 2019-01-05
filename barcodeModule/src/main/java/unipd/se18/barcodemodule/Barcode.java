package unipd.se18.barcodemodule;

import android.graphics.Bitmap;

/**
 * Interface for barcode recognition
 */

public interface Barcode {
        //TODO change by deleting context that could be inconvenient.
    String decodeBarcode(Bitmap image);
}
