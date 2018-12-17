package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/** Classe di testing per capire se il riconoscimento blur può funzionare
 * Leonardo Pratesi - gruppo 1
 *
 *
 */
public class BlurCalculatioAllImages extends AppCompatActivity {


    private final String PHOTOS_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/OCRCameraDB";
    ArrayList list = new ArrayList<Double>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.listavalori);
        super.onCreate(savedInstanceState);
        File path = new File(PHOTOS_FOLDER);
        String[] fileNames = path.list();

    /**    for (int i = 0; i < 10; i++) {
            try {
                File f = new File(PHOTOS_FOLDER, fileNames[i]);
                Bitmap image = BitmapFactory.decodeStream(new FileInputStream(f));
                list.add(CameraActivity.blurValue(image));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
*/
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(fileNames);
        mRecyclerView.setAdapter(mAdapter);
    }


    }





