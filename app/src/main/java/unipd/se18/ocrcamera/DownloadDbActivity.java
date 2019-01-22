package unipd.se18.ocrcamera;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class DownloadDbActivity extends AppCompatActivity {

    private Button clickButton;

    /**
     * Instantiate the UI elements
     *
     * @author Stefano Romanello (g3)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_db);

        ///Load UI element
        clickButton = (Button) findViewById(R.id.downloadDbButton);
        clickButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoDownloadTask task = new PhotoDownloadTask(DownloadDbActivity.this);
                task.execute();
            }
        });
    }
}
