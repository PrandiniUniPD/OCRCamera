package unipd.se18.ocrcamera.performancetester;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import unipd.se18.ocrcamera.Utils;

/**
 * Class that contains a single test element, used in PhotoTester to build a single test and on
 * TestsListAdapter to show data in a listview.
 * @author Luca Moroldo, Francesco Pham
 */

public class TestElement {

    private static final String TAG = "TestElement";

    private static final String NOTES_KEY = "notes";
    private static final String EXTRACTED_TEST_KEY = "extracted_text";
    private static final String TAGS_KEY = "tags";
    private static final String INGREDIENTS_KEY = "ingredients";
    private static final String ALTERATIONS_KEY = "alterations";
    private static final String CONFIDENCE_KEY = "confidence";
    private static final String INGREDIENTS_EXTRACTION = "ingredients_extraction";

    private String imagePath;
    private JSONObject jsonObject;
    private String fileName;
    private HashMap<String, String> alterationsImagesPath;
    private float percentCorrectIngredients;

    /**
     * Array of Strings, each string is an ingredient, ingredients are separated on comma
     * @return Array of strings, each string is an ingredient
     * @author Francesco Pham
     */
    public String[] getIngredientsArray() {
        String ingredients = getIngredients();
        String[] ingredientsArr = ingredients.trim().split("\\s*,\\s*"); //split removing whitespaces
        return ingredientsArr;
    }


    /**
     * @param report Report of ingredients extraction
     * @modify jsonObject of this TestElement
     * @author Francesco Pham - g3
     */
    public void setIngredientsExtraction(String report) {
        try {
            jsonObject.put(INGREDIENTS_EXTRACTION, report);
        } catch (JSONException e) {
            Log.i(TAG, "Failed to set ingredients extraction");
        }
    }

    /**
     * @return Report of ingredients extraction, null otherwise
     * @author Francesco Pham - g3
     */
    public String getIngredientsExtraction() {
        try {
            return jsonObject.getString(INGREDIENTS_EXTRACTION);
        } catch (JSONException e) {
            Log.i(TAG, "Failed to get ingredients extraction");
        }
        return null;
    }

    /**
     * Get percentage of correct ingredients extracted from the ocr.
     * @return percentage of correct ingredients extracted from the ocr.
     * @author Francesco Pham
     */
    public float getPercentCorrectIngredients() {
        return percentCorrectIngredients;
    }

    /**
     * Set percentage of correct ingredients extracted from the ocr.
     * @param percentCorrectIngredients Percentage of correct ingredients extracted from the ocr.
     * @author Francesco Pham
     */
    public void setPercentCorrectIngredients(float percentCorrectIngredients) {
        this.percentCorrectIngredients = percentCorrectIngredients;
    }
}
