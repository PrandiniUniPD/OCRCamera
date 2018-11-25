package unipd.se18.ocrcamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.callback.OverlayViewChangeListener;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.UCropView;

import java.io.File;
import java.net.URI;

/**
 * @author Leonardo Rossi (g2)
 */
public class PostProcessingActivity extends AppCompatActivity
{

    private UCropView cropView;
    private Button btnConfirmCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_processing);

        //Get intent extra
        String filePath = getIntent().getStringExtra("imgUri");

        Uri.Builder builder = new Uri.Builder().scheme("file").path(filePath);
        final Uri captureImageUri = builder.build();

        //Cropview initialization
        cropView = findViewById(R.id.cropView);
        cropView.getCropImageView().setImageURI(captureImageUri);
        cropView.getOverlayView().setShowCropFrame(true);
        cropView.getOverlayView().setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE);

        //UI components initialization
        btnConfirmCrop = findViewById(R.id.btnConfirmCrop);

        btnConfirmCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Uri resultImageUri = Uri.fromFile(new File("/data/user/0/unipd.se18.ocrcamera/cache/AFile.jpg"));
                UCrop.of(captureImageUri, resultImageUri).start(PostProcessingActivity.this);

                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("imagePath", resultImageUri.getPath());
                edit.apply();

                //An intent that will launch the activity that will analyse the photo
                Intent i = new Intent(PostProcessingActivity.this, ResultActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crop_menu, menu);
        return true;
    }
}
