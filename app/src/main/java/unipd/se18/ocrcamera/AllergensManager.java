package unipd.se18.ocrcamera;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import unipd.se18.ocrcamera.inci.IngredientsExtractor;


/**
 * Class that contains the methods to read the allergens from a file, create an allergen list,
 * find the allergens in the recognized ingredients from the OCR.
 */
class AllergensManager {

    static ArrayList<Allergen> allergensList = new ArrayList<>();

    /**
     * This method returns an list of Allergen objects from the input file
     * @param allergenDbStream, an InputStream containing the list of allergens from a file
     * @return allergensList, the converted ArrayList of Allergen objects
     * @modify allergensList by putting all allergens in it
     * @author Cervo Nicolò
     * @author Balzan Pietro
     */
    public static ArrayList<Allergen> getAllergensList(InputStream allergenDbStream) {
        InputStreamReader allergenDbReader = new InputStreamReader(allergenDbStream);
        return allergensList;
    }

    /**
     * this method compares the list of allergens to a String, checking if there are
     * some matching ingredients
     * @param ingredients a List of the ingredients (recognized form the oCR text)
     *                    to be compared to the allergens
     * @return allergensInText, ArrayList of the matching ingredients
     * @author Cervo Nicolò
     * @author Balzan Pietro
     */
    public static ArrayList<Allergen> findListAllergens(List ingredients) {
        ArrayList<Allergen> allergensInText= new ArrayList<Allergen>();
        return allergensInText;
    }
}
