package unipd.se18.ocrcamera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

public class CameraActivity extends AppCompatActivity {

    private static final String LOG_TAG = "CameraActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        String msg = "onCreate:";
        Log.d(LOG_TAG, msg);

        //set emphasis to the corresponding activity button
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.action_bar_linear_layout);
        ActionBarFragment.emphasizeButton(linearLayout, R.id.action_bar_camera_button);
    }

    @Override
    protected void onPause() {
        String msg = "onPause:";
        Log.d(LOG_TAG, msg);
        super.onPause();
    }
}
