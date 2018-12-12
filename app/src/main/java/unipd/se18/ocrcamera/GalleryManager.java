package unipd.se18.ocrcamera;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Provides methods to retrieve and store image with ingredients to populate a gallery view
 * @author Leonardo Rossi (g2), Stefano Romanello (g3)
 */
public class GalleryManager
{
    final private static String TAG = "@@GalleryManager";
    //The prefix that is added to a new image's name
    final private static String NEW_IMAGE_PREFIX = "OCRApp_";
    //The format to which the current date and time are converted
    final private static String CONVERSION_FORMAT = "yyyMMdd_HHmmss";

    //The path of the directory where all the images are stored
    private static String DATA_DIRECTORY_PATH = "";
    //The name of the directory where all the images are stored
    private static String IMAGES_DIRECTORY_NAME = "OCRGallery";
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
        if (!imageDirectory.exists()){
            imageDirectory.mkdir();
        }

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
     * @param context The reference to the activity where the gallery is displayed
     * @param toStore The image with the corresponding ingredients that has to be stored
     * @param ingredients The ingredients that has to be stored with the image
     * @throws IOException if an error occurs during image saving or metadata writing
     */
    public static void storeImage(Context context, Bitmap toStore, ArrayList<String> ingredients) throws IOException
    {
        //Images directory reference set up
        setupImageDirectoryInfo(context);

        //New image's name
        SimpleDateFormat formatter = new SimpleDateFormat(CONVERSION_FORMAT);
        String currentDateAndTime = formatter.format(new Date());
        String imageName = NEW_IMAGE_PREFIX + currentDateAndTime;

        //Storing the given image into a file
        String filePath = saveToFile(toStore, imageName);
        //Metadata writing
        writeMetadata(filePath, ingredients);
    }

    /**
     * |||||||||||||||||||||||
     * ||   PRIVATE METHODS ||
     * |||||||||||||||||||||||
     */

    /**
     * Initializes the variables with which it is possible to get the data directory of the app
     * @param context The reference to the activity where the gallery is displayed
     * @author Leonardo Rossi - modified Romanello Stefano
     */
    private static void setupImageDirectoryInfo(Context context)
    {
        //DATA_DIRECTORY_PATH = context.getApplicationInfo().dataDir;
        DATA_DIRECTORY_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"";
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
     * Saves the specified image to the data directory
     * @param toStore The image that has to be stored
     * @param name The name with which the image has to be saved
     * @return The path of the file that is created
     * @throws IOException if it's impossible to find the file at the specified path or if it's impossible to write to the same file
     */
    private static String saveToFile(Bitmap toStore, String name) throws IOException
    {
        File image = new File(PATH, name);

        OutputStream outStream = new FileOutputStream(image);
        toStore.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        outStream.flush();
        outStream.close();


        return image.getAbsolutePath();
    }

    /**
     * Writes the specified metadata to the image
     * @param path The path of the image to which the metadata have to be written
     * @param metadata The information that has to be stored with the image
     * @throws IOException if it's impossible to reach the file at the specified path
     */
    private static void writeMetadata(String path, ArrayList<String> metadata) throws IOException
    {
        ExifInterface metadataWriter = new ExifInterface(path);
        for (String data : metadata) { metadataWriter.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, data); }
    }

    /**
     * Represents gallery's data model
     */
    public static class PhotoStructure
    {
        private Bitmap photo;
        private ArrayList<String> ingredients = new ArrayList<String>();
    }


    /**
     * Adapter for the cardView in the UI
     * @author Romanello Stefano
     * @request need the activity context and ArrayList<PhotoStructure> of photos to load.
     */
    public static class RecycleCardsAdapter extends RecyclerView.Adapter<RecycleCardsAdapter.CardViewHolder> {

        Context mainActivity;
        ArrayList<PhotoStructure> photosList = new ArrayList<PhotoStructure>();
        public RecycleCardsAdapter(Context context, ArrayList<PhotoStructure> _photos)
        {
            photosList=_photos;
            mainActivity = context;
        }

        /**
         * Holder tells how the card is made. It contains all the elements inside a card
         */
        public class CardViewHolder extends RecyclerView.ViewHolder{
            ImageView imageView;
            TextView txtTitle;
            public CardViewHolder(View itemView) {
                super(itemView);
                this.imageView = (ImageView) itemView.findViewById(R.id.image_holder);
                this.txtTitle = (TextView) itemView.findViewById(R.id.text_title);
            }
        }

        /**
         * Create a new card with the inflated the layout. At this point I just create an object containing the layout of the card.
         * @param parent the main ViewGroup where the card will be inflated
         * @param viewType identifier of the view that I want to implement using default views
         */
        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.cardlayoutgallery, parent, false);
            CardViewHolder cardViewHolder = new CardViewHolder(view);
            return cardViewHolder;
        }

        /**
         * Update the CardViewHolder contents with the item at the given position
         * @param holder Holder of the card containing all its elements
         * @param listPosition auto-increment value starting from 0 that tells me which position is working
         */
        @Override
        public void onBindViewHolder(final CardViewHolder holder, final int listPosition) {

            //Obtain the current working photo
            PhotoStructure currentPhoto = photosList.get(listPosition);
            //Load the bitmap from the PhotoStructure
            Bitmap lastPhoto = currentPhoto.photo;

            //Set imageView properties
            holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(lastPhoto, lastPhoto.getWidth(), lastPhoto.getHeight(), false));
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            //Load ingredients as String
            String finalIngredientsString="";
            for(int i=0; i<currentPhoto.ingredients.size(); i++)
            {
                finalIngredientsString+=currentPhoto.ingredients.get(i)+ ", ";
            }

            //Set txtView properties, remove the last comma+space
            finalIngredientsString = finalIngredientsString.substring(0, finalIngredientsString.length() - 2);
            holder.txtTitle.setText(finalIngredientsString);
        }

        /**
         * Used for set the number of pictures
         * @return integer number of the pictures
         */
        @Override
        public int getItemCount() {
            return photosList.size();
        }


    }

}
