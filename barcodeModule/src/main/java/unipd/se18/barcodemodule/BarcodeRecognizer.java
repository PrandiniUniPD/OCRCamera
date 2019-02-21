package unipd.se18.barcodemodule;

/**
 * Useful set a type of recognition
 * It avoids the single point of failure relative to this process
 * @author Andrea Ton, modified by Elia Bedin
 */
public class BarcodeRecognizer {

    /**
     * Ids of the different barcode api
     */
    public enum API {
        mlkit,
        zxing
    }

    /**
     * Provides an Barcode recognizer
     * @param type The id of the recognition api requested
     * @param barcodeListener the listener for result or errors
     * @return the object relative to the chosen type
     */
    public static Barcode barcodeRecognizer(API type, BarcodeListener barcodeListener){
        switch (type){
            case mlkit: return new MLKitBarcode(barcodeListener);
            case zxing: return new ZXingBarcode(barcodeListener);
            default: return new MLKitBarcode(barcodeListener);
        }
    }
}