package unipd.se18.ocrcamera;


import android.content.Intent;
import android.content.SharedPreferences;
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


        SharedPreferences dbLogin = getSharedPreferences("db_login", MODE_PRIVATE);
        String username = dbLogin.getString("username", null);
        String password = dbLogin.getString("password", null);

        ///Load UI element
        clickButton = (Button) findViewById(R.id.downloadDbButton);
        clickButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoDownloadTask task = new PhotoDownloadTask(DownloadDbActivity.this);
                task.execute();
            }
        });

        clickButton.setEnabled(false);
        if(username == null && password == null) {
            Intent loginIntent = new Intent(DownloadDbActivity.this, LoginToDBActivity.class);
            startActivity(loginIntent);
        } else {
            clickButton.setEnabled(true);
        }
    }
}
