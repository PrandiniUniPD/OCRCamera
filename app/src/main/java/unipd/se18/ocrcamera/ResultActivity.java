package unipd.se18.ocrcamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Class used for showing the result of the OCR processing
 */
public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // UI components
        TextView mOCRTextView = findViewById(R.id.ocr_text_view);
        ImageView mImageView = findViewById(R.id.img_captured_view);

    }
}
