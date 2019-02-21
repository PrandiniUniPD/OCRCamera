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

/**
 * This class implements the ZXing API for barcode recognition
 * More at https://github.com/zxing/zxing
 * APIDocs at https://zxing.github.io/zxing/apidocs/overview-summary.html
 * @author Elia Bedin
 */

public class ZXingBarcode implements Barcode {

    //The listener that will contain either the barcode value or an error code
    private BarcodeListener barcodeListener;
    //The following three int will always be zeros
    //The x coordinate of the first pixel to read from the bitmap;
    private static final int  STARTING_X_POSITION = 0;
    //The y coordinate of the first pixel to read from the bitmap
    private static final int  STARTING_Y_POSITION = 0;
    //Offset, the first index to write into pixels[];
    private static final int  STARTING_INDEX = 0;

    /**
     * Constructor of the class
     * @param listener the listener for result or error
     */
    ZXingBarcode(BarcodeListener listener) throws IllegalArgumentException{
        if (listener != null) {
            barcodeListener = listener;
        } else {
            throw new IllegalArgumentException("BarcodeListener must be provided");
        }
    }

    /**
     * implementation of the decodeBarcode method, that will process a given bitmap photo to make it
     * ready to be scanned with the ZXing barcode reader
     * @param bitmap photo that will be processed for a barcode scan with the ZXing reader
     */
    @Override
    public void decodeBarcode(Bitmap bitmap) {
        if(bitmap == null){
            barcodeListener.onBarcodeRecognizedError(ErrorCode.BITMAP_NOT_FOUND);
        } else {
            //int array needed by the RGBLuminanceSource constructor
            int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
            //getPixels copies pixel data from the Bitmap into the 'pixels' int array
            bitmap.getPixels(pixels, STARTING_INDEX, bitmap.getWidth(), STARTING_X_POSITION,
                    STARTING_Y_POSITION, bitmap.getWidth(), bitmap.getHeight());
            //ZXing library class needed to abstract different bitmap implementations across
            //platforms into a standard interface for requesting gray scale luminance values
            LuminanceSource luminanceValues =
                    new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels);
            //BinaryBitmap is the core bitmap class used to represent 1 bit data
            //Reader objects accept a BinaryBitmap and attempt to decode it
            BinaryBitmap processedBitmap = new BinaryBitmap(new HybridBinarizer(luminanceValues));
            detectBarcode(processedBitmap);
        }
    }

    /**
     * Take the processed bitmap and detect the barcode in it using the ZXing reader
     * @param processedBitmap the image that will be scanned with the ZXing reader
     */
    private void detectBarcode(BinaryBitmap processedBitmap) {
        //Reader that can decode an image of a barcode in some format into the String it encodes
        //MultiFormatReader attempts to determine what barcode format is present within the image
        //as well, and then decodes it accordingly
        Reader reader = new MultiFormatReader();
        try {
            //Locates and decodes a barcode within an image
            Result result = reader.decode(processedBitmap);
            //getText() returns the raw text encoded by the barcode
            barcodeListener.onBarcodeRecognized(result.getText());
        }
        catch (NotFoundException e) {
            //If no barcode if found
            barcodeListener.onBarcodeRecognizedError(ErrorCode.BARCODE_NOT_FOUND);
        }
        catch (ChecksumException | FormatException e) {
            //If a barcode is found but decoding it leads to an error
            barcodeListener.onBarcodeRecognizedError(ErrorCode.DECODING_ERROR);
        }
    }

}