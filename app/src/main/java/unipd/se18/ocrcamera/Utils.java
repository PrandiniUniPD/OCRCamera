package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
     * @return String with the text inside the file pointed by filePath, empty string if file doesn't exist
     */
    public static String getTextFromFile(String filePath) {

        String text ="";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            text = br.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;

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

        int start = filePath.lastIndexOf("/") + 1;
        int end = filePath.lastIndexOf(".");
        if (end > start)
            return filePath.substring(start, end);
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
