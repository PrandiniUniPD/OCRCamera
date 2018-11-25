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
 * @author Giovanni Furlan (g2), Leonardo Rossi (g2), Pietro Prandini (g2)
 */
public class PostProcessingActivity extends AppCompatActivity
{

    private Button btnConfirmCrop;
    final Uri resultImageUri = Uri.fromFile(new File("/data/user/0/unipd.se18.ocrcamera/cache/AFile.jpg"));


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_processing);

        //UI components initialization
        btnConfirmCrop = findViewById(R.id.btnConfirmCrop);

        //Get intent extra
        String filePath = getIntent().getStringExtra("imgUri");

        //Build Uri from filePath adding scheme
        Uri.Builder builder = new Uri.Builder().scheme("file").path(filePath);
        final Uri captureImageUri = builder.build();

        //Create a new result file and take his Uri
        //final Uri resultImageUri = Uri.fromFile(new File("/data/user/0/unipd.se18.ocrcamera/cache/AFile.jpg"));

        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(true);
        options.setFreeStyleCropEnabled(true);
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarTitle("Focus on the ingredients");
        UCrop.of(captureImageUri, resultImageUri)
                .withOptions(options)
                .start(PostProcessingActivity.this);

        btnConfirmCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Raplace image of Uri with cropped one
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            //Raplace image of Uri with cropped one
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("imagePath", resultImageUri.getPath());
            edit.apply();

            //An intent that will launch the activity that will analyse the photo
            Intent i = new Intent(PostProcessingActivity.this, ResultActivity.class);
            startActivity(i);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }
}
