package com.example.group4.cameraapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
This is the first camera app I made for my group and for the first demo asked by the prof.
For convenience I used the integrated Camera application (and also because all the feature like zoom,
autofocus, ecc..., are going to work without any issue), which is the one pre-installed
on the device. (It could be the google camera, or the personalized one, however it seems to work
better with the google camera, that can be installed in every Android phone)
To prevent the app from crashing, the button is set disabled until the user grants the permissions
asked for accessing the camera. --> Check the official Android Developer tutorials for the guide I used
to manage permissions (https://developer.android.com/guide/topics/media/camera).
 */

public class MainActivity extends AppCompatActivity {

    private Button takePictureButton;
    private ImageView imageView;
    private Uri file;
    private TextView outText;
    private int cameraCode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takePictureButton = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.pictureShot);
        outText = (TextView) findViewById(R.id.outText);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }

    /**
    This method set the button enabled if the permmissions asked are granted by the user
    @param requestCode int camera enabler code when permissions granted
    @param permissions String[] contains result of checking if permissions are declared in manifest file
    @author Andrea Ton
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }

    /**
    This method implements also the OCR using the wrapper for the mlkit api,
    the OCR class is not asked for review.
    @param view View layout object that call this method (in this case a button)
    @author Andrea Ton
     */
    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile()); //creates a uri from a file
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, cameraCode);
            WrapperMLKit scanner = new WrapperMLKit(getApplicationContext(),file);
            String ingredients = scanner.getTextFromImg(BitmapFactory.decodeFile(file.getPath()));
            outText.setText(ingredients);
        }
    }

    /**
    @param requestCode int arbitrary constant used to identify activity requested
    @param resultCode int system code for activity result
    @param data Intent returned from activity called
    @author Andrea Ton
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == cameraCode) {
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(file);
            }
        }
    }

    /**
    @return File file saved in directory pictures, if dir exist, if not, get created.
    @author Andrea Ton
     */
    private static File getOutputMediaFile(){
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraDemo");
        if (!storageDir.exists()){
            if (!storageDir.mkdirs()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(storageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
    }

}
