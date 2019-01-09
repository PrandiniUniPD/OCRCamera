package unipd.se18.ocrcamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static android.graphics.BitmapFactory.decodeStream;

/** Testing Activity to see if blur could work
 * Leonardo Pratesi - gruppo 1
 * Activity accessible by the options menu on the application
 *
 */
public class BlurCalculatioAllImages extends AppCompatActivity {

    /**
     *  Directory of the Photos
     */
    private final String PHOTOS_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/OCRCameraDB";


    //private final String PHOTOS_FIX_ERROR = "/storage/emulated/0/Images/OCRCameraDB";
    /**
     * ListView to show all the ingredients in a List
     */
    ListView listView;

    /**
     * ArrayList of object (see class blur object)
     */
    ArrayList<BlurObject> arrayBlur = new ArrayList<>();

    /**
     * List of elements inside PHOTOS_FOLDER
     */
    String fileNames[];




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.listavalori);
        super.onCreate(savedInstanceState);

        File path = new File(PHOTOS_FOLDER);
        Log.e("info", path.getAbsolutePath());
        fileNames = path.list();

        Log.e("info", PHOTOS_FOLDER);

        if (fileNames != null) {
            setGallery(PHOTOS_FOLDER);
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

    /**
     * Method that creates a blurObject for every image in the folder
     * @param imagepath
     */
    public void setGallery(String imagepath) {

        int conta = 0;
        for (int i = 0; i < imagepath.length(); i++) {                    //path.length = number of elements in the folder
            try {
                //extension= getExtension(fileNames[i]);

                File f = new File(PHOTOS_FOLDER, fileNames[i]);                               //crea un oggetto BlurObject per ogni elemento della cartella
                Bitmap image = BitmapFactory.decodeStream(new FileInputStream(f));            //se è un file diverso da un immagine non viene costruito l'oggetto
                BlurObject obj = new BlurObject(fileNames[i], image);
                arrayBlur.add(obj);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("errore", "filenotfound");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Log.e("errore", "illegalargument");
            }
            conta++; //check how many photos load
            Log.e("check", Integer.toString(conta));
        }
    }

    /**
     *  Method to show the max value of the blur
     * @param list list of BlurObjects
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

}



