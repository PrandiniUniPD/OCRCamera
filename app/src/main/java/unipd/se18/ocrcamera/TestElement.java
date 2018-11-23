package unipd.se18.ocrcamera;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that contains a single test element, used in PhotoTester to build a single test and on AdapterTestElement to show data in a listview
 * @author Luca Moroldo, Francesco Pham
 */

public class TestElement {

    private Bitmap picture;
    private JSONObject jsonObject;
    private String fileName;

    public TestElement(Bitmap picture, JSONObject jsonObject, String fileName) {
        this.picture = picture;
        this.jsonObject = jsonObject;
        this.fileName = fileName;
    }

    public String[] getIngredientsArray() throws JSONException {
        String ingredients = getIngredients();
        String[] ingredientsArr = ingredients.trim().split("\\s*,\\s*"); //split removing whitespaces
        return ingredientsArr;
    }

    public String getIngredients() throws  JSONException {
        String ingredients = jsonObject.getString("ingredients");
        return ingredients;
    }

    public String[] getTags() throws JSONException {
        return Utils.getStringArrayFromJSON(jsonObject, "tags");
    }

    public Bitmap getPicture() {
        return picture;
    }

    public String getFileName() {
        return fileName;
    }

    public String getNotes() throws JSONException {
        return jsonObject.getString("notes");
    }

    /**
     * @return confidence if set, 0 otherwise
     * @throws JSONException
     */
    public float getConfidence() throws JSONException {
        String confidence = jsonObject.getString("confidence");
        if(confidence != null)
            return Float.parseFloat(confidence);
        else
            return 0;
    }

    /**
     * @return confidence if set, 0 otherwise
     * @throws JSONException
     */
    public float getConfidenceWithoutIngredientFilter() throws JSONException {
        String confidence = jsonObject.getString("confidence_without_ingredient_filter");
        if(confidence != null)
            return Float.parseFloat(confidence);
        else
            return 0;
    }

    /**
     *
     * @return recognized text if set, empty string otherwise
     * @throws JSONException
     */
    public String getRecognizedText() throws  JSONException {
        String recognizedText = jsonObject.getString("extracted_text");
        if(recognizedText != null)
            return recognizedText;
        return "";
    }

    /**
     *
     * @return recognized ingredients if set, empty string otherwise
     * @throws JSONException
     */
    public String getIngredientsFiltered() throws  JSONException {
        String ingredients = jsonObject.getString("ingredients_filtered");
        if(ingredients != null)
            return ingredients;
        return "";
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setConfidence(float confidence) throws JSONException {
        jsonObject.put("confidence", confidence);
    }
    public void setConfidenceWithoutIngredientFilter(float confidence) throws JSONException {
        jsonObject.put("confidence_without_ingredient_filter", confidence);
    }
    public void setRecognizedText(String text) throws JSONException {
        jsonObject.put("extracted_text", text);
    }
    public void setIngredientsFiltered(String text) throws JSONException {
        jsonObject.put("ingredients_filtered", text);
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }
}
