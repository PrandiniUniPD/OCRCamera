package com.example.mattia.fotocamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
* @author Fasan Giovanni
*/
public class OCRActivity extends AppCompatActivity {
    //private static final String TAG = "AndroidCameraApi";
    Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        OcrManager manager = new OcrManager();
        //initialize OCR library
        manager.initAPI();
        extras = getIntent().getExtras();
        //get a photo's path from MainActivity
        String path=extras.getString("PATH_I_NEED");
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        //recognize the text from image
        String testo = manager.getTextFromImg(bitmap);
        TextView t= findViewById(R.id.textView);
        t.setText(testo);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
    }
}
