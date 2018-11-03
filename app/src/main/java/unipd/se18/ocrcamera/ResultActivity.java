package unipd.se18.ocrcamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Class used for showing the result of the OCR processing
 */
public class ResultActivity extends AppCompatActivity {
    private ImageView iv;
    private TextView tv;
    private TextExtractor te;
    //private File imgFile;
    private Bitmap img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        te = new TextExtractor(this.getApplicationContext());
        tv = (TextView)findViewById(R.id.ocr_text_view);
        tv.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();
        String message = intent.getStringExtra("imgPath");
        Log.e("ResultActivity", "" + message);
        iv = (ImageView)findViewById(R.id.img_captured_view);
        img = BitmapFactory.decodeFile(message);
        iv.setImageBitmap(img);
        tv.setText(te.getTextFromImg(img));
    }
}
