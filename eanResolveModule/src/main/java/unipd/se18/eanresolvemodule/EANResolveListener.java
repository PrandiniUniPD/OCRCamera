package unipd.se18.eanresolvemodule;

public interface EANResolveListener {

    /**
     * Method called when the product is successfully resolved from the barcode
     * @param product String with name and details of the barcode
     */
    void onProductFound(String product);

    /**
     * Method called when the process fails
     * @param error message
     */
    void onResolveError(ErrorCode error);
}
