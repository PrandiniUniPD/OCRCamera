package unipd.se18.barcodemodule;

import android.graphics.Bitmap;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for MLKitBarcode class
 * @author Andrea Ton
 */
public class MLKitBarcodeTest{

    /**
     * Test methods for main decode method decodeBarcode(Bitmap bitmap)
     */
    @Test
    public void decodeBarcode_String() {
        MLKitBarcode detector = mock(MLKitBarcode.class);
        Bitmap image = mock(Bitmap.class);
        when(detector.decodeBarcode(image)).thenReturn("873124007169");
        final String expected = "873124007169";
        assertEquals(detector.decodeBarcode(image), expected);
    }

    @Test
    public void decodeBarcode_Void() {
        MLKitBarcode detector = mock(MLKitBarcode.class);
        Bitmap image = mock(Bitmap.class);
        when(detector.decodeBarcode(image)).thenReturn("");
        final String expected = "";
        assertEquals(detector.decodeBarcode(image), expected);
    }

}