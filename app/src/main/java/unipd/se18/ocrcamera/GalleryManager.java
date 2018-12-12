package unipd.se18.ocrcamera;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Provides methods to retrieve and store image with ingredients to populate a gallery view
 * @author Leonardo Rossi (g2), Stefano Romanello (g3)
 */
public class GalleryManager
{
    /**
     * Loads images and metadata
     * @param context The reference to the activity where the gallery is displayed
     * @return A list of the photos with the corresponding metadata
     */
    public static ArrayList<PhotoStructure> getImages(Context context)
    {
        return null;
    }

    /**
     * Stores image and metadata
     * @param toStore The image with the corresponding ingredients that has to be stored
     */
    public static void storeImage(ArrayList<PhotoStructure> toStore)
    {

    }

    /**
     * Represents gallery's data model
     */
    public static class PhotoStructure
    {
        private Bitmap photo;
        private ArrayList<String> ingredients;
    }
}
