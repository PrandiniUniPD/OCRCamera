package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Class used for showing the result of the OCR processing
 */
public class ResultActivity extends AppCompatActivity {

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        extras = getIntent().getExtras();
        String path=extras.getString("PATH_I_NEED");

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        TextView textView = (TextView) findViewById(R.id.ocr_text_view);
        textView.setMovementMethod(new ScrollingMovementMethod());

        TextExtractor manager = new TextExtractor();
        String text = manager.getTextFromImg(bitmap);

        ImageView imageView = (ImageView) findViewById(R.id.img_captured_view);
        imageView.setImageBitmap(bitmap);
        textView.setText(text);

    }
}
