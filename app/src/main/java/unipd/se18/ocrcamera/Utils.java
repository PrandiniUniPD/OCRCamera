package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Utils {
    /**
     * @param filePath name of a jpeg file to convert to bitmap
     * @return image converted to bitmap
     */
    public static Bitmap loadBitmap(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        return bitmap;
    }


    /**
     * @param filePath
     * @return String with the text inside the fileName.txt
     */
    public static String getIngredientsFromFile(String filePath) {

        return null;
    }

    /**
     *
     * @param filePath path to file
     * @return file extension if exists, null otherwise
     */
    public static String getFileExtension(String filePath) {
        int strLength = filePath.lastIndexOf(".");
        if(strLength > 0)
            return filePath.substring(strLength + 1).toLowerCase();
        return null;
    }

    /**
     *
     * @param filePath path to file
     * @return file name without extension if exists, null otherwise
     */
    public static String getFilePrefix(String filePath) {
        int strLength = filePath.lastIndexOf(".");
        if(strLength > 0)
            return filePath.substring(0,strLength);
        return null;
    }
}
