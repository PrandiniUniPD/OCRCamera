package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;

/**
 *  Class to open everyImage and show it in a bigger view
 *  the image is saved in a private directory and the opened with this activity
 */
public class ImageViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.immagine);
        ImageView image = findViewById(R.id.imageView);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(this.openFileInput("myImage"));
            image.setImageBitmap(bitmap);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show();
            this.finish();
        }

    }
}
