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
     *
     * @param imagePath String containing the path to the image associated with the jsonObject
     * @param jsonObject JSONObject containing test data (ingredients, tags, notes, alterations if any)
     * @param fileName name of the test
     * @author Luca Moroldo - g3
     */
    public TestElement(String imagePath, JSONObject jsonObject, String fileName) {
        this.imagePath = imagePath;
        this.jsonObject = jsonObject;
        this.fileName = fileName;
        //prepare alterations images if there is any
        String[] alterationsNames = getAlterationsNames();
        if(alterationsNames != null) {
            alterationsImagesPath = new HashMap<>();
            for(String alterationName : alterationsNames) {
                alterationsImagesPath.put(alterationName, null);
            }
        }
    }

    /**
     * Array of Strings, each string is an ingredient, ingredients are separated on comma
     * @return Array of strings, each string is an ingredient
     */
    public String[] getIngredientsArray() {
        String ingredients = getIngredients();
        String[] ingredientsArr = ingredients.trim().split("\\s*,\\s*"); //split removing whitespaces
        return ingredientsArr;
    }

    /**
     * @return ingredients text of this test if exist, null otherwise
     * @author Luca Moroldo - g3
     */
    public String getIngredients() {
        try {
            return jsonObject.getString(INGREDIENTS_KEY);
        } catch (JSONException e) {
            Log.i(TAG, "Failed to get ingredients inside test: " + fileName);
        }
        return null;
    }

    /**
     * @return array of tags of this test if exist, null otherwise
     * @author Luca Moroldo - g3
     */
    public String[] getTags() {
        try {
            return Utils.getStringArrayFromJSON(jsonObject, TAGS_KEY);
        } catch (JSONException e) {
            Log.i(TAG, "Failed to get tags inside test: " + fileName);
        }
        return null;
    }

    /**
     * @return String with the path to the image associated with this test, can be null if it hasn't
     * been set
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * @return name of this test
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return notes text of this test if exist, null otherwise
     * @author Luca Moroldo - g3
     */
    public String getNotes() {
        try {
            return jsonObject.getString(NOTES_KEY);
        } catch (JSONException e) {
            Log.i(TAG, "Failed to get notes inside test: " + fileName);
        }
        return null;
    }

    /**
     * Getter for alterations filenames of a test (e.g. cropped photo) if any
     * @return array of strings if the element has any alteration (each string is a filename), null otherwise
     * @author Luca Moroldo - g3
     */
    public String[] getAlterationsNames() {

        JSONObject alterations = null;
        try {
            alterations = jsonObject.getJSONObject(ALTERATIONS_KEY);
        } catch (JSONException e) {
            Log.i(TAG, "There are no alterations inside test: " + fileName);
            return null;
        }

        ArrayList<String> alterationsNames= new ArrayList<String>();

        Iterator<String> keys = alterations.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            alterationsNames.add(key);
        }

        return alterationsNames.toArray(new String[0]);
    }

    /**
     * @return Float confidence of this test if present, -1 otherwise
     * @author Luca Moroldo - g3
     */
    public float getConfidence() {
        try {
            String confidence = jsonObject.getString(CONFIDENCE_KEY);
            return Float.parseFloat(confidence);
        } catch (JSONException e) {
            Log.i(TAG, "Failed to get confidence in test: " + fileName);
        }
        return -1;
    }

    /**
     * @return String recognized text of this test if present, null otherwise
     * @author Luca Moroldo - g3
     */
    public String getRecognizedText() {
        try {
            return jsonObject.getString(EXTRACTED_TEST_KEY);
        } catch (JSONException e) {
            Log.i(TAG, "Failed to get recognized text in test: " + fileName);
        }
        return null;
    }

    /**
     * Get an alteration extracted text
     * @param alterationName name of an existing alteration inside this test
     * @return alteration recognized text if it's set, null if recognized text hasn't been set or if there isn't any alteration named with the given param
     * @author Luca Moroldo - g3
     */
    public String getAlterationRecognizedText(String alterationName) {
        JSONObject jsonAlteration = getAlterationWithName(alterationName);
        if(jsonAlteration != null) {
            try {
                return jsonAlteration.getString(EXTRACTED_TEST_KEY);
            } catch (JSONException e) {
                Log.i(TAG, "Failed to get recognized text in alteration: " + alterationName + " inside test: " + fileName);
            }
        }
        return null;
    }

    /**
     * Get an alteration confidence
     * @param alterationName name of an existing alteration inside this test
     * @return confidence if it has been set, -1 if the confidence hasn't been set or if there isn't any alteration named with the given param
     * @author Luca Moroldo - g3
     */
    public float getAlterationConfidence(String alterationName) {

        JSONObject jsonAlteration = getAlterationWithName(alterationName);
        if(jsonAlteration != null) {
            try {
                String confidence = jsonAlteration.getString(CONFIDENCE_KEY);
                return Float.parseFloat(confidence);
            } catch (JSONException e) {
                Log.i(TAG, "Failed to get confidence in alteration: " + alterationName + " inside test: " + fileName);
            }
        }
        return -1;
    }

    /**
     * Get an alteration associated image
     * @param alterationName name of an existing alteration inside this test
     * @return String with the path to the image associated with the test if it has been set,
     * null if the image hasn't been set or if there isn't any alteration named with the given param
     * @author Luca Moroldo - g3
     */
    public String getAlterationImagePath(String alterationName) {
        if(alterationsImagesPath.containsKey(alterationName))
            return alterationsImagesPath.get(alterationName);
        else
            Log.i(TAG, "No image path set for alteration " + alterationName + " in test " + fileName);
        return null;
    }

    /**
     * @param alterationName alterationName name of an existing alteration inside this test
     * @return notes text, null if there isn't any alteration named with the given param or if there aren't notes in this test
     * @author Luca Moroldo - g3
     */
    public String getAlterationNotes(String alterationName) {
        JSONObject jsonAlteration = getAlterationWithName(alterationName);
        if(jsonAlteration != null) {
            try {
                return jsonAlteration.getString(NOTES_KEY);
            } catch (JSONException e) {
                Log.i(TAG, "Failed to get notes from alteration: " + alterationName + " inside test: " + fileName);
            }
        }
        return null;
    }

    /**
     * @param alterationName alterationName name of an existing alteration inside this test
     * @return tags array, null  if there isn't any alteration named with the given param
     * @author Luca Moroldo - g3
     */
    public String[] getAlterationTags(String alterationName) {

        JSONObject jsonAlteration = getAlterationWithName(alterationName);
        if(jsonAlteration != null) {
            try {
                return Utils.getStringArrayFromJSON(jsonAlteration, TAGS_KEY);
            } catch (JSONException e) {
                Log.i(TAG, "Failed to get tags from altaration: " + alterationName + " inside test: " + fileName);
            }
        }
        return null;
    }

    /**
     * @return JSONObject associated to this test
     */
    public JSONObject getJsonObject() { return jsonObject; }

    /**
     * @param confidence Float that will be associated to this test with key 'confidence'
     * @modify jsonObject of this TestElement
     */
    public void setConfidence(float confidence) {
        try {
            jsonObject.put(CONFIDENCE_KEY, Float.toString(confidence));
        } catch (JSONException e) {
            Log.i(TAG, "Failed to set confidence in test " + fileName);
        }
    }

    /**
     * @param text String that will be set in this test with key 'extracted_text'
     * @modify jsonObject of this TestElement
     * @author Luca Moroldo - g3
     */
    public void setRecognizedText(String text) {
        try {
            jsonObject.put(EXTRACTED_TEST_KEY, text);
        } catch (JSONException e) {
            Log.i(TAG, "Failed to set recognized text in test " + fileName);
        }
    }

    /**
     * associate a image path to an alteration of this test
     * @param alterationName name of an existing alteration inside this test
     * @param imagePath String with the path to the image related to the alteration test
     * @modify jsonObject of this TestElement
     * @return true if image was set correctly, false if alteration name doesn't exist
     * @author Luca Moroldo - g3
     */
    public boolean setAlterationImagePath(String alterationName, String imagePath) {
        if(alterationsImagesPath.containsKey(alterationName)) {
            alterationsImagesPath.put(alterationName, imagePath);
            return true;
        }
        Log.i(TAG, "No alteration found in " + fileName + " with name " + alterationName);
        return false;
    }

    /**
     * Associate a recognized text to the alteration inside this test, if present
     * @param alterationName alterationName name of an existing alteration inside this test
     * @param text recognized text of the alteration that will be set
     * @modify jsonObject of this TestElement
     * @author Luca Moroldo - g3
     */
    public void setAlterationRecognizedText(String alterationName, String text) {

        JSONObject jsonAlteration = getAlterationWithName(alterationName);
        if(jsonAlteration != null) {
            try {
                jsonAlteration.put(EXTRACTED_TEST_KEY, text);
            } catch (JSONException e) {
                Log.i(TAG, "Failed to set confidence of alteration: " + alterationName + " inside test: " + fileName);
            }
        }
    }

    /**
     * @param alterationName alterationName name of an existing alteration inside this test
     * @param alterationConfidence value of the confidence of the alteration that will be set
     * @modify jsonObject of this TestElement
     * @author Luca Moroldo - g3
     */
    public void setAlterationConfidence(String alterationName, float alterationConfidence) {

        JSONObject jsonAlteration = getAlterationWithName(alterationName);
        if(jsonAlteration != null) {
            try {
                jsonAlteration.put(CONFIDENCE_KEY, Float.toString(alterationConfidence));
            } catch (JSONException e) {
                Log.i(TAG, "Failed to set confidence of alteration: " + alterationName + " inside test: " + fileName);
            }
        }
    }

    @Override
    public String toString() { return jsonObject.toString(); }


    /**
     * Get from JSON test data an alteration JSONObject with the name given as argument
     * @param alterationName name of an alteration inside this test
     * @return JSONObject associated with the alteration name given as argument, null if it doesn't exist
     */
    private JSONObject getAlterationWithName(String alterationName) {
        JSONObject jsonAlterations = null;
        try {
            jsonAlterations = jsonObject.getJSONObject(ALTERATIONS_KEY);
        } catch (JSONException e) {
            Log.i(TAG, "No alteration found in " + fileName );
            return null;
        }

        try {
            JSONObject jsonAlteration = jsonAlterations.getJSONObject(alterationName);
            return jsonAlteration;
        } catch (JSONException e) {
            Log.i(TAG, "There is no alteration with name " + alterationName + " inside test " + fileName);
            return null;
        }
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
