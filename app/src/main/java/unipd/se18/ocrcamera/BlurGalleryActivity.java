package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/** Testing Activity to see if blur could work, shows a gallery with each image associated with it's blur value
 * Leonardo Pratesi - gruppo 1
 * Activity accessible by the options menu on the application
 *
 */
public class BlurGalleryActivity extends AppCompatActivity {

    /**
     *  Directory of the Photos
     */
    private final String PHOTOS_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/OCRCameraDB";


    //private final String PHOTOS_FIX_ERROR = "/storage/emulated/0/Images/OCRCameraDB";
    /**
     * ListView to show all the ingredients in a List
     */
    private ListView listView;

    /**
     * ArrayList of object (see class blur object)
     */
    private ArrayList<BlurObject> arrayBlur = new ArrayList<>();

    /**
     * List of elements inside PHOTOS_FOLDER
     */
    private String fileNames[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.listavalori);
        super.onCreate(savedInstanceState);

        File path = new File(PHOTOS_FOLDER);
        fileNames = path.list();
        if (fileNames != null) {
            try {               //added try catch after Li suggestion
                setGallery();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show();
            this.finish();
            //Exit from Gallery
        }

        //View preparation
        listView = (ListView) findViewById(R.id.listview);
        BlurObjectAdapter adapter = new BlurObjectAdapter(this, R.layout.listavalori, arrayBlur);
        listView.setAdapter(adapter);
        //show max blur value
        TextView maxview = findViewById(R.id.textViewMax);
        maxview.setText(String.valueOf(findMax(arrayBlur)));


    }

    /**
     * Method that creates a blurObject for every image in the folder
     *
     */
    private void setGallery() throws Exception {
        //counts how many images are loaded (for troubleshooting purpose)
        int count =0;
        for (String fileName : fileNames) {                                //imagepath.length = number of elements in the folder
            if (getExtension(fileName).equals("jpg")) {                    //checks if it is an image

                try {
                    File f = new File(PHOTOS_FOLDER, fileName); //creates a new object for each element
                    FileInputStream stream = new FileInputStream(f);
                    Bitmap image = BitmapFactory.decodeStream(stream);
                    BlurObject obj = new BlurObject(fileName, image);
                    arrayBlur.add(obj);
                    stream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("err", "filenotfound");
                    throw e;
                } catch (IllegalArgumentException e) {
                    Log.e("err", "illegalargument");
                    throw e;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("err", "IOException");
                    throw e;
                    }

                count++; //check how many photos load
                Log.e("check", Integer.toString(count));
            }
        }
    }

    /**
     *  Method to show the max value of the blur
     * @param list list of BlurObject
     * @return double value
     */
    public double findMax(ArrayList<BlurObject> list){

        double max=0;
        double temp=0;
        for (int i = 0; i < list.size(); i++)
        {
            temp=list.get(i).getBlur();
            if (max<list.get(i).getBlur())
            {
                max=temp;
            }
        }
        return max;
    }


    /**
     * Method to get file extension leonardo Pratesi
     * @param file
     * @return file extension
     */
    public String getExtension(String file) {
        int dotposition = file.lastIndexOf(".");
        String filename_Without_Ext = file.substring(0, dotposition);
        String ext = file.substring(dotposition + 1, file.length());
        Log.e(ext, ext);
        return ext;

    }

}



