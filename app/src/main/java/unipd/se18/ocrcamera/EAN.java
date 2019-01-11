package unipd.se18.eanresolvemodule;

/**
 * Wrapper interface for EAN string resolve
 * @author Elia Bedin
 */

public interface EAN {

    /**
     * @param EANCode the EAN value of a barcode
     * more at https://en.wikipedia.org/wiki/International_Article_Number
     * @return The string of the name and details of the product with that EAN value, if one is
     * found, otherwise an error is returned
     */
    String decodeEAN(String EANCode);

}
