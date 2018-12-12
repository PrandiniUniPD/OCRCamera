package unipd.se18.ocrcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Provides methods to retrieve and store image with ingredients to populate a gallery view
 * @author Leonardo Rossi (g2), Stefano Romanello (g3)
 */
public class GalleryManager
{
    final private static String TAG = "@@GalleryManager";

    //The path of the directory where all the images are stored
    private static String DATA_DIRECTORY_PATH = "";
    //The name of the directory where all the images are stored
    private static String IMAGES_DIRECTORY_NAME = "ImagesFolder";
    //The complete path to reach images folder
    private static String PATH = "";

    /**
     * Loads images and metadata
     * @param context The reference to the activity where the gallery is displayed
     * @return A list of the photos with the corresponding metadata
     */
    public static ArrayList<PhotoStructure> getImages(Context context)
    {
        //Definition of the list that contains all the images in the data directory
        ArrayList<PhotoStructure> imagesStructures = new ArrayList<>();

        //Images directory reference set up
        setupImageDirectoryInfo(context);

        //Obtaining the reference to the directory
        File imageDirectory = new File(PATH);
        //If the directory doesn't exist it will be created
        if (!imageDirectory.exists()){ imageDirectory.mkdir(); }

        //Obtaining the files into the specified directory
        File[] images = imageDirectory.listFiles();
        //Metadata reading
        for (File image : images)
        {
            PhotoStructure toAdd = buildStructure(image);
            if (toAdd != null){ imagesStructures.add(toAdd); }
        }

        return imagesStructures;
    }

    /**
     * Stores image and metadata
     * @param toStore The image with the corresponding ingredients that has to be stored
     * @param ingredients The ingredients that has to be stored with the image
     */
    public static void storeImage(Bitmap toStore, String ingredients)
    {

    }

    /**
     * |||||||||||||||||||||||
     * ||   PRIVATE METHODS ||
     * |||||||||||||||||||||||
     */

    /**
     * Initializes the variables with which it is possible to get the data directory of the app
     * @param context The reference to the activity where the gallery is displayed
     */
    private static void setupImageDirectoryInfo(Context context)
    {
        DATA_DIRECTORY_PATH = context.getApplicationInfo().dataDir;
        PATH = DATA_DIRECTORY_PATH + File.separator + IMAGES_DIRECTORY_NAME;
    }

    /**
     * Reads the metadata from the given image
     * @param image The image from which the metadata have to be read
     * @return An object that contains the image with its metadata. Null if the given image can't be opened
     */
    private static PhotoStructure buildStructure(File image)
    {
        try
        {
            //Ingredients are read from image metadata
            ExifInterface metadataReader = new ExifInterface(image.getAbsolutePath());
            String ingredients = metadataReader.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);
            //A PhotoStructure object is built so that it can contain all image's information
            PhotoStructure structure = new PhotoStructure();
            structure.ingredients.add(ingredients);
            structure.photo = BitmapFactory.decodeFile(image.getAbsolutePath());

            return structure;
        }
        catch (IOException e)
        {
            //Log to keep trace which images can't be loaded
            Log.d(TAG, "Impossible to get file named: " + image.getName() + "at the path: " + image.getAbsolutePath());
            //Returning null so that this image will not be added to the list of the retrieved files
            return null;
        }
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
