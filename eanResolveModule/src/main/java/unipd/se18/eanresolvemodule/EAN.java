package unipd.se18.eanresolvemodule;

/**
 * Wrapper interface for EAN string resolve
 * @author Elia Bedin
 */

public interface EAN {

    /**
     * @param EANCode the EAN value of a barcode
     * more at https://en.wikipedia.org/wiki/International_Article_Number
     */
    void decodeEAN(String EANCode) throws IllegalArgumentException;

}
