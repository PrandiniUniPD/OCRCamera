
package unipd.se18.ocrcamera;




import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;

/** Classe che implementa una galleria di immagini
 * Leonardo Pratesi - gruppo 1
 * usa classe CustomGalleryAdapter
 *
 */

    public class ImageLibraryActivity extends AppCompatActivity {

        Gallery simpleGallery;
        CustomGalleryAdapter customGalleryAdapter;
        ImageView selectedImageView;
        // array of images
        int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5,
                //R.drawable.image6, R.drawable.image7, R.drawable.image8, R.drawable.image9, R.drawable.image10, R.drawable.image11,
                //R.drawable.image12, R.drawable.image13
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.imagegallery);
            simpleGallery = (Gallery) findViewById(R.id.simpleGallery); // get the reference of Gallery
            selectedImageView = (ImageView) findViewById(R.id.selectedImageView); // get the reference of ImageView
            customGalleryAdapter = new CustomGalleryAdapter(getApplicationContext(), images); // initialize the adapter
            simpleGallery.setAdapter(customGalleryAdapter); // set the adapter
            simpleGallery.setSpacing(10);
            // perform setOnItemClickListener event on the Gallery
            simpleGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // set the selected image in the ImageView
                    selectedImageView.setImageResource(images[position]);

                }
            });
        }
    }

