package unipd.se18.ocrcamera;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * Class that contains the methods to read the allergens from a file, create an allergen list,
 * find the allergens in the recognized ingredients from the OCR.
 */
class AllergensManager {

    ArrayList<Allergen> listAllergens = new ArrayList<>();

    public ArrayList<Allergen> getListAllergens (InputStream allergendbStream) {
        InputStreamReader allergendbReader = new InputStreamReader(allergendbStream);


        
        return listAllergens;
    }
}
