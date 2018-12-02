package com.example.user.codereview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This Activity receive bundle from mainActivity and show
 * the data
 * */

public class SecondActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView imView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        textView = findViewById(R.id.secondText);

        Intent intent = getIntent();
        Bundle extra = intent.getBundleExtra("Bundle");
        String s = extra.getString("message");
        Bitmap ImageBitmap = extra.getParcelable("BitmapImage");

        textView.setText(s);
        imView.setImageBitmap(ImageBitmap);

    }





}
