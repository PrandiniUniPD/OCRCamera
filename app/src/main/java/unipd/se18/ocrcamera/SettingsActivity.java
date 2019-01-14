package unipd.se18.ocrcamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

public class SettingsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        String msg = "onCreate:";
        Log.d(LOG_TAG, msg);

        LinearLayout ll = (LinearLayout) findViewById(R.id.action_bar_linear_layout);
        ActionBarFragment.enphasizeButton(ll,R.id.action_bar_settings_button);
    }

    @Override
    protected void onPause() {
        String msg = "onPause:";
        Log.d(LOG_TAG, msg);
        super.onPause();
    }
}
