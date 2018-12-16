
package unipd.se18.ocrcamera;




import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReferenceArray;

/** Classe che implementa una galleria a griglia di immagini
 * Leonardo Pratesi - gruppo 1
 * usa classe InternalStorageManager (di moroldo)
 *
 */

    public class ImageLibraryActivity extends AppCompatActivity {

    private GridView imageGrid;
    private ArrayList<Bitmap> bitmapList; //Array of images
    private final String PHOTOS_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/OCRCameraDB";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.imagegallery);
        this.imageGrid = (GridView) findViewById(R.id.griglia);
        this.bitmapList = new ArrayList<Bitmap>();
        imageGrid.setAdapter(new ImageAdapter(this, bitmapList));


        // listener per interagire con le foto
        imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(ImageLibraryActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

        public void start(View v)
        {
            new BackgroundTask().execute();
        }
    private class BackgroundTask extends AsyncTask<Void, Integer, String>
    {
        public int photoloaded= 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... arg0)
        {
            File path = new File(PHOTOS_FOLDER);
            if (path.exists()) {
                String[] fileNames = path.list();
                for (int i = 0; i < 20; i++) {
                    try {
                        File f = new File(PHOTOS_FOLDER, fileNames[i]);
                        bitmapList.add(BitmapFactory.decodeStream(new FileInputStream(f)));
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
            }

        }
            //imageGrid.setAdapter(new ImageAdapter(this, bitmapList));
            return "Lavoro Terminato!";
        }

        protected void onProgressUpdate(Integer... values)
        {


        }


        /** File path = new File(PHOTOS_FOLDER);
         System.out.println(path);
         System.out.println(path.list()); //il path non contiene elementi =null RISOLVERE


         if (path.exists()) {
         String[] fileNames = path.list();

         for (int i = 0; i < 20; i++) {
         System.out.println(fileNames[i]); //controllare che file carica


         works but very slow, create Thread?
         try {
         File f=new File(PHOTOS_FOLDER, fileNames[i]);
         bitmapList.add(BitmapFactory.decodeStream(new FileInputStream(f)));
         }
         catch (FileNotFoundException e)
         {
         e.printStackTrace();
         }

         */

        //non funziona
                    //InternalStorageManager dir = new InternalStorageManager(this, PHOTOS_FOLDER, fileNames[i]);
                    //bitmapList.add(dir.loadBitmapFromInternalStorage());

                }

        }
        //this.imageGrid.setAdapter(new ImageAdapter(this, this.bitmapList));






