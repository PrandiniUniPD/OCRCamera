package unipd.se18.ocrcamera;

import java.util.ArrayList;

/**
 * Classes that implement this interface should provide a method to extract a list of ingredients from a text
 * @author Francesco Pham
 */
interface IngredientsExtractor {

    /**
     * This method extracts ingredients from the ocr text and returns the list of ingredients.
     * @param text The entire OCR text
     * @return List of Ingredient objects where are stored ingredient's informations
     * @author Francesco Pham
     */
    ArrayList<Ingredient> findListIngredients(String text);
}
