package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.GridView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/** Classe che realizza un Thread per caricare le immagini più velocemente a griglia
 * Leonardo Pratesi - gruppo 1
 *
 *
 */
public class GalleryLoaderTask extends AsyncTask<String, Void, Bitmap> {
    private String dir;
    private ArrayList<Bitmap> bitmapList;
    private GridView imageGrid;



    public GalleryThread(String dir, ArrayList<Bitmap> bitmapList, GridView imageGrid)
    {
        this.dir=dir;
        this.bitmapList=bitmapList;
        this.imageGrid=imageGrid;


    }

    public void run()
    {   File path = new File(dir);
        if (path.exists()) {
        String[] fileNames = path.list();
            for (int i = 0; i < 20; i++) {
                try {
                    File f = new File(dir, fileNames[i]);
                    bitmapList.add(BitmapFactory.decodeStream(new FileInputStream(f)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            imageGrid.setAdapter(new ImageAdapter(this, this.bitmapList));
    }

}
