package unipd.se18.barcodemodule;

/**
 * Useful set a type of recognition
 * It avoids the single point of failure relative to this process
 * @author Andrea Ton
 */
public class BarcodeRecognizer {

    /**
     * Ids of the different barcode api
     */
    public enum API {
        zxing
    }

    /**
     * Provides a Barcode Recognizer
     * @param type The id of the recognition api requested
     * @return the object relative to the choosen type
     */
    public static Barcode barcodeRecognizer(API type){
        switch (type){
            case mlkit: return new MLKitBarcode();
            case zxing: return new ZXingBarcode();
            default: return new MLKitBarcode();
        }
    }
}
