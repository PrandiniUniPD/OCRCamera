package unipd.se18.barcodemodule;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Interface for barcode recognition
 */

public interface BarcodeInterface {
        //TODO change by deleting context that could be inconvenient.
    String decodeBarcode(Bitmap image);
}
