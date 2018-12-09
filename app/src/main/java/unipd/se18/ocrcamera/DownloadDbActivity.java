package unipd.se18.ocrcamera;


import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class DownloadDbActivity extends AppCompatActivity {

    private Button clickButtonDownload;
    private Button clickButtonLogin;
    private final String LOGINGINFORMATION_FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/ingsoftwareftp.txt";
    private final String PHOTOS_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/OCRCameraDB";
    private LinearLayout layoutLogin;
    private LinearLayout layoutDownload;

    private EditText txtHostname;
    private EditText txtUsrname;
    private EditText txtPassword;

    private TextView txtInternetStatus;

    /**
     * Instantiate the UI elements and check if is possible to do the login.
     *
     * @author Stefano Romanello (g3)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_db);

        layoutDownload = (LinearLayout)findViewById(R.id.LayoutDownload);
        layoutLogin = (LinearLayout)findViewById(R.id.LayoutLogin);
        txtHostname = (EditText) findViewById(R.id.txtHostnameDownload);
        txtPassword = (EditText) findViewById(R.id.txtPasswordDownload);
        txtUsrname = (EditText) findViewById(R.id.txtUsernameDownload);
        txtInternetStatus = (TextView) findViewById(R.id.txtInternetStatusDownload);
        
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        //Verify if the login is already done and if there is internet connection
        final File file = new File(LOGINGINFORMATION_FILE);
        if (!file.exists() && cm.getActiveNetworkInfo() != null) {
           //No file, have to do the login
            layoutLogin.setVisibility(View.VISIBLE);
        }
        else if(file.exists() && cm.getActiveNetworkInfo() != null)
        {
            //Can do the login
            layoutDownload.setVisibility(View.VISIBLE);
        }
        else if(cm.getActiveNetworkInfo() == null)
        {

            txtInternetStatus.setVisibility(View.VISIBLE);
        }

        ///Load other UI elements
        clickButtonDownload = (Button) findViewById(R.id.downloadDbButton);
        clickButtonDownload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoDownloadTask task = new PhotoDownloadTask(DownloadDbActivity.this);
                task.execute();
            }
        });

        clickButtonLogin = (Button) findViewById(R.id.downloadLoginButton);
        clickButtonLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });


    }

    /**
     * Load the infos from EditText and create the login file
     *
     * @author Stefano Romanello (g3)
     */
    private void doLogin()
    {
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(LOGINGINFORMATION_FILE);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));

            bw.write(txtUsrname.getText().toString());
            bw.newLine();
            bw.write(txtPassword.getText().toString());
            bw.newLine();
            bw.write(txtHostname.getText().toString());

            bw.close();

            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Check if folder for photos exist
        File dir = new File(PHOTOS_FOLDER);
        if(!dir.exists() || !dir.isDirectory())
        {
            dir.mkdir();
        }


        layoutDownload.setVisibility(View.VISIBLE);
        layoutLogin.setVisibility(View.GONE);

    }




}
