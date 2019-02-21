package unipd.se18.barcodemodule;

public interface BarcodeListener {

    /**
     * Method called when barcode is successfully recognized
     * @param barcode String of the barcode detected
     */
    void onBarcodeRecognized(String barcode);

    /**
     * Method called when the process fails
     * @param error error message
     */
    void onBarcodeRecognizedError(ErrorCode error);
}
