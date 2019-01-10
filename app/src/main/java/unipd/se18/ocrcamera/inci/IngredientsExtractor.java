package unipd.se18.ocrcamera.inci;

import java.util.List;

/**
 * Classes that implement this interface should provide a method to extract a list of ingredients from a text
 * @author Francesco Pham
 */
public interface IngredientsExtractor {

    /**
     * This method extracts ingredients from the ocr text and returns the list of ingredients.
     * @param text The entire OCR text
     * @return List of extracted ingredients, empty list if no ingredients are found
     * @author Francesco Pham
     */
    List<Ingredient> findListIngredients(String text);
}
