package unipd.se18.barcodemodule;

import android.graphics.Bitmap;

/**
 * Interface for barcode recognition
 * @author Andrea Ton
 */

public interface Barcode {
    void decodeBarcode(Bitmap image);
}
