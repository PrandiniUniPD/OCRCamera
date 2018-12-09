
package unipd.se18.ocrcamera;




import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReferenceArray;

/** Classe che implementa una galleria di immagini
 * Leonardo Pratesi - gruppo 1
 * usa classe CustomGalleryAdapter
 *
 */

    public class ImageLibraryActivity extends AppCompatActivity {

    private GridView imageGrid;
    private ArrayList<Bitmap> bitmapList;
    // array of images


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagegallery);
        this.imageGrid = (GridView) findViewById(R.id.griglia);
        this.bitmapList = new ArrayList<Bitmap>();

        File path = new File(Environment.getExternalStorageDirectory(), "iWallet/Images");
        if (path.exists()) {
            String[] fileNames = path.list();
            try {
                for (int i = 0; i < fileNames.length; i++) {
                    Bitmap bitmap = BitmapFactory.decodeFile(path.getPath() + "/" + fileNames[i]);
                    this.bitmapList.add(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.imageGrid.setAdapter(new ImageAdapter(this, this.bitmapList));
    }
}


