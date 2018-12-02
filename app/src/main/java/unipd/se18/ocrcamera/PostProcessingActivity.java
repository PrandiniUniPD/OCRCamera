package unipd.se18.ocrcamera;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.UCropView;

/**
 * @author Leonardo Rossi (g2)
 */
public class PostProcessingActivity extends AppCompatActivity
{

    private UCropView cropView;
    private Button btnConfirmCrop;
    private MenuItem btnResetChanges;
    private Uri captureImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_processing);

        //Get intent extra
        captureImageUri = getIntent().getParcelableExtra("imgUri");

        //Cropview initialization
        cropView = findViewById(R.id.cropView);
        cropView.getCropImageView().setImageURI(captureImageUri);
        cropView.getOverlayView().setShowCropFrame(true);
        cropView.getOverlayView().setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE);

        //UI components initialization
        btnConfirmCrop = findViewById(R.id.btnConfirmCrop);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crop_menu, menu);

        //Menu reset button initialization
        btnResetChanges = menu.findItem(R.id.btnResetChanges);

        /**
         * When clicked, resets the cropView to original rotation, zoom and crop frame
         * @author Alberto Valente
         */
        btnResetChanges.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                cropView.resetCropImageView();
                cropView.getCropImageView().setImageURI(captureImageUri);
                return true;
            }
        });
        return true;
    }
}
