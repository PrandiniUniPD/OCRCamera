package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
    Double[] blurval = new Double[200];
    ListView listView;
    ArrayAdapter<String> adapter;






    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.listavalori);
        super.onCreate(savedInstanceState);
        File path = new File(PHOTOS_FOLDER);
        String[] fileNames = path.list();
        String[] texttoview = new String[8];

        for (int i = 2; i < 10; i++) {
            try {
                File f = new File(PHOTOS_FOLDER, fileNames[i]);
                Bitmap image = BitmapFactory.decodeStream(new FileInputStream(f));

                if (image != null)
                {
                    blurval[i]=CameraActivity.blurValue(image);
                    texttoview[i]= fileNames[i] + "value: "+ blurval[i];

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        listView = (ListView) findViewById(R.id.listview);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, texttoview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }


    }





