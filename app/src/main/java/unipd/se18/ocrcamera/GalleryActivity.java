package unipd.se18.ocrcamera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

public class GalleryActivity extends AppCompatActivity {

    private static final String LOG_TAG = "GalleryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);

        String msg = "onCreate:";
        Log.d(LOG_TAG, msg);

        LinearLayout ll = (LinearLayout) findViewById(R.id.action_bar_linear_layout);
        ActionBarFragment.emphasizeButton(ll,R.id.action_bar_gallery_button);
    }

    @Override
    protected void onPause() {
        String msg = "onPause:";
        Log.d(LOG_TAG, msg);
        super.onPause();
    }
}
