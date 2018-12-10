package unipd.se18.ocrcamera;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    private final int REQUEST_PERMISSION_CODE = 500;
    private LinearLayout layoutLogin;
    private LinearLayout layoutDownload;

    private EditText txtHostname;
    private EditText txtUsrname;
    private EditText txtPassword;


    private TextView txtInternetStatus;
    private TextView txtPermissionStatus;

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
        txtPermissionStatus = (TextView) findViewById(R.id.txtPermissionStatusDownload);
        
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Check and in case Ask for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        }

        //Verify if the login is already done and if there is internet connection
        final File file = new File(LOGINGINFORMATION_FILE);

        if(cm.getActiveNetworkInfo() == null) //No internet
        {
            txtInternetStatus.setVisibility(View.VISIBLE);
        }
        else if (!file.exists() && cm.getActiveNetworkInfo() != null) //No file, have to do the login
        {

            layoutLogin.setVisibility(View.VISIBLE);
        }
        else if(file.exists() && cm.getActiveNetworkInfo() != null) //Can do the login
        {
            layoutDownload.setVisibility(View.VISIBLE);
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
    /**
     * Verify if the user granted the permission
     *
     * @author Stefano Romanello (g3)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    txtPermissionStatus.setVisibility(View.GONE);

                } else {
                    // permission denied
                    txtPermissionStatus.setVisibility(View.VISIBLE);
                    layoutLogin.setVisibility(View.GONE);
                    layoutDownload.setVisibility(View.GONE);
                }
                return;
            }
        }
    }




}
