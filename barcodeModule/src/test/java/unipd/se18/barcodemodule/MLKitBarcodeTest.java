package unipd.se18.barcodemodule;

import android.graphics.Bitmap;
import org.junit.Test;
import static org.mockito.Mockito.mock;


/**
 * Test class for MLKitBarcode class
 * @author Andrea Ton
 */
public class MLKitBarcodeTest{

    /**
     * Test methods for main decode method decodeBarcode(Bitmap bitmap)
     */
    @Test
    public void decodeBarcode() {
        final MLKitBarcode barcode = new MLKitBarcode();
        MLKitBarcode bc = mock(MLKitBarcode.class);
        Bitmap bitmap = mock(Bitmap.class);
        //...
    }


}