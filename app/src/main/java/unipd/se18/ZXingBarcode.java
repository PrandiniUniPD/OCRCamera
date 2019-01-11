package unipd.se18.barcodemodule;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.RGBLuminanceSource;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * This class implements the ZXing API for barcode recognition
 * More at https://github.com/zxing/zxing
 * APIDocs at https://zxing.github.io/zxing/apidocs/overview-summary.html
 * @author Elia Bedin
 */

public class ZXingBarcode implements Barcode {

    //The string decoded from the barcode.
    private String code = "";
    //Text of the decoding error
    private static final String DECODE_ERROR =
            "ERROR: Barcode decoding unsuccessful, please try again.";


    /**
     * implementation of the decodeBarcode method, that would detect the barcode from the given
     * image
     * @param bitmap photo taken from the camera, to be analyzed.
     */
    @Override
    public String decodeBarcode(Bitmap bitmap) {

        //int array needed by the RGBLuminanceSource constructor
        int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
        //getPixels copies pixel data from the Bitmap into the 'pixels' int array
        //The three zeros (that will always be zeros) are, in order:
        //  offset, the first index to write into pixels[];
        //  the x coordinate of the first pixel to read from the bitmap;
        //  the y coordinate of the first pixel to read from the bitmap
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
                bitmap.getHeight());
        //ZXing library class needed to abstract different bitmap implementations across platforms
        //into a standard interface for requesting gray scale luminance values
        LuminanceSource luminanceValues =
                new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels);
        //Reader that can decode an image of a barcode in some format into the String it encodes
        //MultiFormatReader attempts to determine what barcode format is present within the image
        //as well, and then decodes it accordingly
        Reader reader = new MultiFormatReader();
        //BinaryBitmap is the core bitmap class used to represent 1 bit data
        //Reader objects accept a BinaryBitmap and attempt to decode it
        BinaryBitmap processedBitmap = new BinaryBitmap(new HybridBinarizer(luminanceValues));

        try {
            //Locates and decodes a barcode within an image
            Result result = reader.decode(processedBitmap);
            //getText() returns the raw text encoded by the barcode
            code = result.getText();
        }
        catch (NotFoundException e) {
            //If no barcode if found, "" is returned
            Log.e("NoBarcode", "Error getting barcode from bitmap.", e);
        }
        catch (ChecksumException | FormatException e) {
            //If a barcode is found but decoding it leads to an error, a string that report an
            // error occurred is returned
            code = DECODE_ERROR;
            Log.e("ErrorDecoding", "Error decoding barcode", e);
        }
        return code;
    }

}