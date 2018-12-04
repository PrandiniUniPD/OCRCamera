package com.example.group4.cameraapp;               // Reviewed by Balzan Pietro, to avoid confusion my comments are indicated by ***
                                                    // both at the start and at the end
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;                            //***from the StyleDoc:
import android.view.View;                            //   (If there are both static and non-static imports,
import android.widget.Button;                        //   a single blank line separates the two blocks)
import android.widget.ImageView;                     //   There are no other blank lines between import statements.
import android.widget.TextView;                      //   Also import statements should be written in ASCII Sort order
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
    //code to identify the camera activity (useful to get result from it)
    private int cameraCode = 100;        //***if you can't avoid "magic numbers" comment to explain what they mean***

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get layout objects using their xml id, set on the layout file
        takePictureButton = (Button) findViewById(R.id.button);  //***it's by no means unreadable but maybe you could add comments***
        imageView = (ImageView) findViewById(R.id.pictureShot);
        outText = (TextView) findViewById(R.id.outText);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);    //*** magic number "0" ***
            //requestCode -> application specific request code to match with a result reported to onRequestPermissionsResult(...).
        }
    }

    /**
    This method set the button enabled if the permmissions asked are granted by the user
    @param requestCode int camera enabler code when permissions granted
    @param permissions String[] the requested permissions
    @param grantResults int[] The grant results for the corresponding permissions
    @author Andrea Ton
     */                                         // ***param grantResults is missing from specifications, what does it do?***
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //requestCode used in the method .requestPermissions(...)
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
            String ingredients = scanner.getTextFromImg(BitmapFactory.decodeFile(file.getPath()));  //*** this part is a bit confusing
            outText.setText(ingredients);                                                   //    maybe it is a good idea to initialise 
        }                                                                  //    the Bitmap outside, before calling getTextFromImg()***
    }

    //****describe what this method does
    //   as you have done with the others***

    /**
    When picture is taken, the imageView object is the set with that pic.
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
    This method save the picture inside a File, in the Picture directory.
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
