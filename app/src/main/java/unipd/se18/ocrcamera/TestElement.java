package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class that contains a single test element, used in PhotoTester to build a single test and on AdapterTestElement to show data in a listview
 * @author Luca Moroldo, Francesco Pham
 */

public class TestElement {

    private Bitmap picture;
    private JSONObject jsonObject;
    private String fileName;
    private HashMap<String, Bitmap> alterationsBitmaps;

    /**
     *
     * @param picture Bitmap associated to the jsonObject
     * @param jsonObject JSONObject containing test data (ingredients, tags, notes, alterations if any)
     * @param fileName name of the test
     */
    public TestElement(Bitmap picture, JSONObject jsonObject, String fileName) {
        this.picture = picture;
        this.jsonObject = jsonObject;
        this.fileName = fileName;
        //prepare alterations bitmap if there is any
        String[] alterationsNames = getAlterationsNames();
        if(alterationsNames != null) {
            alterationsBitmaps = new HashMap<String, Bitmap>();
            for(String alterationName : alterationsNames) {
                alterationsBitmaps.put(alterationName, null);
            }
        }
    }

    /**
     * Array of Strings, each string is an ingredient, ingredients are separated on comma
     * @return Array of strings, each string is an ingredient
     * @throws JSONException
     */
    public String[] getIngredientsArray() throws JSONException {
        String ingredients = getIngredients();
        String[] ingredientsArr = ingredients.trim().split("\\s*,\\s*"); //split removing whitespaces
        return ingredientsArr;
    }

    public String getIngredients() throws  JSONException {
        String ingredients = jsonObject.getString("ingredients");
        return ingredients;
    }

    public String[] getTags() throws JSONException { return Utils.getStringArrayFromJSON(jsonObject, "tags"); }

    public Bitmap getPicture() {
        return picture;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     *
     * @return String with notes associated with the test, can be empty
     * @throws JSONException
     */
    public String getNotes() throws JSONException { return jsonObject.getString("notes"); }

    /**
     * Getter for alterations filenames of a test (e.g. cropped photo) if any
     * @return array of strings if the element has any alteration (each string is a filename), null otherwise
     * @author Luca Moroldo - g3
     */
    public String[] getAlterationsNames() {

        try {
            JSONObject alterations = jsonObject.getJSONObject("alterations");
            if(alterations != null) {
                ArrayList<String> alterationsNames= new ArrayList<String>();

                Iterator<String> keys = alterations.keys();

                while(keys.hasNext()) {
                    String key = keys.next();
                    alterationsNames.add(key);
                }

                return alterationsNames.toArray(new String[0]);
            }

        } catch (JSONException e) {
            Log.i("TestElement", "No alteration associated to " + fileName);
        }
        return null;
    }

    /**
     * @return confidence if set, -1 otherwise
     * @throws JSONException
     */
    public float getConfidence() throws JSONException {
        String confidence = jsonObject.getString("confidence");
        if(confidence != null)
            return Float.parseFloat(confidence);
        else
            return -1;
    }

    /**
     * @return recognized text if set, null otherwise
     * @throws JSONException
     */
    public String getRecognizedText() throws  JSONException {
        String recognizedText = jsonObject.getString("extracted_text");
        if(recognizedText != null)
            return recognizedText;
        return null;
    }

    /**
     * Get an alteration extracted text
     * @param alterationName name of an existing alteration inside this test
     * @return alteration recognized text if it's set, null if recognized text hasn't been set or if there isn't any alteration named with the given param
     * @throws JSONException
     * @author Luca Moroldo - g3
     */
    public String getAlterationRecognizedText(String alterationName) throws JSONException {
        JSONObject jsonAlterations = jsonObject.getJSONObject("alterations");
        if(jsonAlterations != null) {
            JSONObject jsonAlteration = jsonAlterations.getJSONObject(alterationName);

            if(jsonAlteration != null) {
                return jsonAlteration.getString("extracted_text");
            } else {
                Log.i("TestElement", "There is no alteration with name " + alterationName + " inside " + fileName);
            }
        }
        return null;
    }

    /**
     * Get an alteration confidence
     * @param alterationName name of an existing alteration inside this test
     * @return confidence if it has been set, -1 if the confidence hasn't been set or if there isn't any alteration named with the given param
     * @throws JSONException
     * @author Luca Moroldo - g3
     */
    public float getAlterationConfidence(String alterationName) throws JSONException {
        JSONObject jsonAlterations = jsonObject.getJSONObject("alterations");
        if(jsonAlterations != null) {
            JSONObject jsonAlteration = jsonAlterations.getJSONObject(alterationName);
            if(jsonAlteration != null) {
                String confidence = jsonAlteration.getString("confidence");
                if(confidence != null)
                    return Float.parseFloat(confidence);
            } else {
                Log.i("TestElement", "There is no alteration with name " + alterationName + " inside " + fileName);
            }
        }
        return -1;
    }

    /**
     * Get an alteration associated bitmap
     * @param alterationName name of an existing alteration inside this test
     * @return bitmap associated with the test if it has been set, null if the bitmap hasn't been set or if there isn't any alteration named with the given param
     * @author Luca Moroldo - g3
     */
    public Bitmap getAlterationBitmap(String alterationName) {
        if(alterationsBitmaps.containsKey(alterationName))
            return alterationsBitmaps.get(alterationName);
        return null;
    }

    /**
     * @param alterationName alterationName name of an existing alteration inside this test
     * @return notes text set, null if notes text have not been set or if there isn't any alteration named with the given param
     * @throws JSONException
     * @author Luca Moroldo - g3
     */
    public String getAlterationNotes(String alterationName) throws JSONException {
        JSONObject jsonAlterations = jsonObject.getJSONObject("alterations");
        if(jsonAlterations != null) {
            JSONObject jsonAlteration = jsonAlterations.getJSONObject(alterationName);
            if(jsonAlteration != null) {
                return jsonAlteration.getString("notes");
            } else {
                Log.i("TestElement", "There is no alteration with name " + alterationName + " inside " + fileName);
            }
        }
        return null;
    }

    /**
     * @param alterationName alterationName name of an existing alteration inside this test
     * @return tags array, null  if there isn't any alteration named with the given param
     * @throws JSONException
     * @author Luca Moroldo - g3
     */
    public String[] getAlterationTags(String alterationName) throws JSONException {
        JSONObject jsonAlterations = jsonObject.getJSONObject("alterations");
        if(jsonAlterations != null) {
            JSONObject jsonAlteration = jsonAlterations.getJSONObject(alterationName);
            if(jsonAlteration != null) {
                return Utils.getStringArrayFromJSON(jsonAlteration, "tags");
            } else {
                Log.i("TestElement", "There is no alteration with name " + alterationName + " inside " + fileName);
            }
        }
        return null;
    }

    public JSONObject getJsonObject() { return jsonObject; }

    public void setConfidence(float confidence) throws JSONException { jsonObject.put("confidence", Float.toString(confidence)); }

    public void setRecognizedText(String text) throws JSONException { jsonObject.put("extracted_text", text); }

    /**
     * associate a bitmap file to an alteration of a test
     * @param alterationName name of an existing alteration inside this test
     * @param bitmap image related to the test alteration
     * @return true if bitmap was set correctly, false if alteration name doesn't exist
     * @author Luca Moroldo - g3
     */
    public boolean setAlterationBitmap(String alterationName, Bitmap bitmap) {
        if(alterationsBitmaps.containsKey(alterationName)) {
            alterationsBitmaps.put(alterationName, bitmap);
            return true;
        }
        return false;
    }

    /**
     * Associate a recognized text to the alteration inside this test, if present
     * @param alterationName alterationName name of an existing alteration inside this test
     * @param text recognized text of the alteration that will be set
     * @modify jsonObject of this TestElement
     * @throws JSONException
     * @author Luca Moroldo - g3
     */
    public void setAlterationRecognizedText(String alterationName, String text) throws JSONException {
        JSONObject jsonAlterations = jsonObject.getJSONObject("alterations");
        if(jsonAlterations != null) {
            JSONObject jsonAlteration = jsonAlterations.getJSONObject(alterationName);
            if(jsonAlteration != null) {
                jsonAlteration.put("extracted_text", text);
            } else {
                Log.i("TestElement", "There is no alteration with name " + alterationName + " inside " + fileName);
            }
        }
    }

    /**
     * @param alterationName alterationName name of an existing alteration inside this test
     * @param alterationConfidence value of the confidence of the alteration that will be set
     * @throws JSONException
     * @author Luca Moroldo - g3
     */
    public void setAlterationConfidence(String alterationName, float alterationConfidence) throws JSONException {
        JSONObject jsonAlterations = jsonObject.getJSONObject("alterations");
        if(jsonAlterations != null) {
            JSONObject jsonAlteration = jsonAlterations.getJSONObject(alterationName);
            if(jsonAlteration != null) {
                jsonAlteration.put("confidence", Float.toString(alterationConfidence));
            } else {
                Log.i("TestElement", "There is no alteration with name " + alterationName + " inside " + fileName);
            }
        }
    }

    @Override
    public String toString() { return jsonObject.toString(); }


}
