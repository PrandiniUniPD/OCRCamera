package unipd.se18.ocrcamera;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * Activity that let user crop the image
 * */
public class CropActivity extends AppCompatActivity {

    /**
     * view that will contains the image to be cropped
     */
    private CropImageView cropImageView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        //retrieve the cropimageview and set it with specific options
        cropImageView = findViewById(R.id.cropImageView);
        setCropImageView();

        //set a listener to manage whether the crop is completed, on the CropImageView
        cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                handleResult(result);
            }
        });

        //retrieve the bitmap image of the last photo taken in the previous activity
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String pathImage = prefs.getString("imagePath", null);
        Bitmap photoBitmap = BitmapFactory.decodeFile(pathImage);

        //if bitmap is null then return to previous activity to take another photo
        if (photoBitmap != null){
            cropImageView.setImageBitmap(photoBitmap);
        }else{
            Log.e("bitmap_null", "Bitmap not found error");
            Toast.makeText(this, "Photo not Found!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * button listener that will start the cropping action on the image
     * @param view button with ID: cropPhoto
     */
    public void cropPhoto(View view){
        //trigger the cropping action
        cropImageView.getCroppedImageAsync();
    }

    /**
     * handle the result of the cropping process by passing it to the result activity
     * @param result result of the cropping process (should be the cropped image)
     */
    private void handleResult(CropImageView.CropResult result){
        ResultActivity.lastPhoto = result.getBitmap();

        //start the result activity that will retrieve barcode information
        Intent i = new Intent(CropActivity.this, ResultActivity.class);
        startActivity(i);
    }

    /**
     * Setup the CropImageView
     */
    private void setCropImageView(){
        cropImageView.setAspectRatio(1, 1);
        //width and height can change independently
        cropImageView.setFixedAspectRatio(false);
        //activate guidelines
        cropImageView.setGuidelines(CropImageView.Guidelines.ON);
        //set the shape of the crop selection
        cropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
        cropImageView.setScaleType(CropImageView.ScaleType.FIT_CENTER);
        //image will zoom whether crop area becomes very small
        cropImageView.setAutoZoomEnabled(true);
    }

}
