/**
 * This is one of first prototype of CommonDemo, in mainActivity it's possible
 * take a photo with MediaStore Camera or write a test and send it to SecondActivity
 * @author Roberto Vicentini helped by Andrea Ton
 */


package com.example.user.codereview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.net.URI;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText text;
    private ImageView photo;
    private Button bSwitchActivity;
    private Button bTakePhoto;
    private int REQUEST_IMAGE_CAPTURE=50;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.maintext);
        bSwitchActivity = findViewById(R.id.mainbutton);
        bSwitchActivity.setEnabled(false);

        photo = findViewById(R.id.photoView);
        bTakePhoto = findViewById(R.id.mainButton2);


        //Until we haven't permission the button bTakePhoto is disable

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            bTakePhoto.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA}, 0);
        }

    }
    

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 0){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                bTakePhoto.setEnabled(true);

            }

        }

    }


    @Override
    public void onClick(View view){

        switch(view.getId()) {

            //this button change secondActivity
            case (R.id.mainbutton):

                String sText = text.getText().toString();
                Intent intent = new Intent(this, SecondActivity.class);
                Bundle extras = new Bundle();
                extras.putParcelable("BitmapImage", imageBitmap);
                extras.putString("message", sText);
                intent.putExtra("bundle", extras);
                startActivity(intent);
                break;

            //this button take a photo
            case (R.id.mainButton2):

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                break;


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            bSwitchActivity.setEnabled(true);
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            photo.setImageBitmap(imageBitmap);
        }
    }
}
