package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
    /**
     * @param filePath name of a jpeg file to convert to bitmap
     * @return image converted to bitmap
     */
    public static Bitmap loadBitmapFromFile(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        return bitmap;
    }


    /**
     * @param filePath
     * @return String with the text inside the fileName.txt
     */
    public static String getTextFromFile(String filePath) {

        return null;
    }

    /**
     * @param filePath path to file
     * @return file extension if exists, null otherwise
     */
    public static String getFileExtension(String filePath) {
        int strLength = filePath.lastIndexOf(".");
        if (strLength > 0)
            return filePath.substring(strLength + 1).toLowerCase();
        return null;
    }

    /**
     * @param filePath path to file
     * @return file name without extension if exists, null otherwise
     */
    public static String getFilePrefix(String filePath) {

        //TODO correct method - use UtilsTest to verify correction
        int strLength = filePath.lastIndexOf(".");
        if (strLength > 0)
            return filePath.substring(0, strLength);
        return null;
    }


    /**
     * @param json JSON object with containing the array given as param
     * @param name array name in JSON object
     * @return the array in the JSON object converted to String
     */
    public static String[] getStringArrayFromJSON(JSONObject json, String name) throws JSONException {
        JSONArray jsonArray = json.getJSONArray(name);
        String[] array = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = jsonArray.getString(i);
        }
        return array;
    }
}
