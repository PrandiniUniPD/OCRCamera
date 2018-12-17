
package unipd.se18.ocrcamera;




import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
 *
 *
 */

    public class ImageLibraryActivity extends AppCompatActivity {

    private GridView imageGrid;
    private ArrayList<Bitmap> bitmapList; //Array of images
    private final String PHOTOS_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/OCRCameraDB";
    private int progress;
    Thread thread1;
    Handler mHandlerThread; //handler per comunicare dal thread alla View
    private static final int UPDATE = 101;
    private static final int START_PROGRESS = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.imagegallery);
        this.imageGrid = (GridView) findViewById(R.id.griglia);
        this.bitmapList = new ArrayList<Bitmap>();


        // listener per interagire con le foto
        imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(ImageLibraryActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // thread that load images in the bitmapList
        thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File path = new File(PHOTOS_FOLDER);
                    if (path.exists()) {
                        String[] fileNames = path.list();
                        for (int i = 0; i < 30; i++) {
                            try {
                                File f = new File(PHOTOS_FOLDER, fileNames[i]);
                                bitmapList.add(BitmapFactory.decodeStream(new FileInputStream(f)));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = UPDATE;
                mHandlerThread.sendMessage(message);

            }
        });
        thread1.start();
    }

    //gestione dei messaggi dell'handler
    @Override
    protected void onResume() {
        super.onResume();
        mHandlerThread = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == UPDATE) {
                    imageGrid.setAdapter(new ImageAdapter(getApplicationContext(), bitmapList));
                }
                else if (msg.what == START_PROGRESS){
                    thread1.start();
                }

            }

        };
    }

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




        //this.imageGrid.setAdapter(new ImageAdapter(this, this.bitmapList));







