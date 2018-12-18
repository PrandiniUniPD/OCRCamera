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
    private final String LOGININFORMATION_FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/ingsoftwareftp.txt";
    private final String PHOTOS_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/OCRCameraDB";
    private final int REQUEST_PERMISSION_CODE = 500;

    //Layout containing the login parts
    private LinearLayout layoutLogin;
    private EditText txtHostname;
    private EditText txtUsername;
    private EditText txtPassword;

    //Layout containing the download part
    private LinearLayout layoutDownload;

    //Error messages
    private TextView txtInternetStatus;
    private TextView txtPermissionStatus;
    private TextView txtLoginStatus;

    /**
     * Instantiate the UI elements and check if is possible to do the login.
     *
     * @author Stefano Romanello (g3)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_db);

        layoutDownload = findViewById(R.id.LayoutDownload);
        layoutLogin = findViewById(R.id.LayoutLogin);
        txtHostname = findViewById(R.id.txtHostnameDownload);
        txtPassword = findViewById(R.id.txtPasswordDownload);
        txtUsername = findViewById(R.id.txtUsernameDownload);
        txtInternetStatus = findViewById(R.id.txtInternetStatusDownload);
        txtPermissionStatus = findViewById(R.id.txtPermissionStatusDownload);
        txtLoginStatus = findViewById(R.id.txtLoginStatusDownload);

        ///Load other UI elements
        clickButtonDownload = findViewById(R.id.downloadDbButton);
        clickButtonDownload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoDownloadTask task = new PhotoDownloadTask(DownloadDbActivity.this);
                task.execute();
            }
        });

        clickButtonLogin = findViewById(R.id.downloadLoginButton);
        clickButtonLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        //I have to understand if I can download the images (permission)
        //and load the correct layout (login or download)
        verifyDoLogin();

    }

    /**
     * When I'm clicking the login button from the login layout,
     * Load the infos from EditText and create the login file
     *
     * @author Stefano Romanello (g3)
     */
    private void doLogin()
    {
        try {
            File dirDocuments = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"");
            if(!dirDocuments.exists() || !dirDocuments.isDirectory())
            {
                dirDocuments.mkdir();
            }

            FileOutputStream fOut = new FileOutputStream(LOGININFORMATION_FILE);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));

            bw.write(txtUsername.getText().toString());
            bw.newLine();
            bw.write(txtPassword.getText().toString());
            bw.newLine();
            bw.write(txtHostname.getText().toString());

            bw.close();

            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();

            //Hide the login layout and show the error text in case the login fails (writing permission)
            //If the actual login to the server will fail when I click the download button the login layout will popup asking the new credentials
            layoutLogin.setVisibility(View.GONE);
            txtLoginStatus.setVisibility(View.VISIBLE);
        }

        //Check if folder for photos exist
        File dirPhotos = new File(PHOTOS_FOLDER);
        if(!dirPhotos.exists() || !dirPhotos.isDirectory())
        {
            //If the photo doesnt exists I will create it
            dirPhotos.mkdir();
        }

        //Hide the login layout and show the download layout
        layoutDownload.setVisibility(View.VISIBLE);
        layoutLogin.setVisibility(View.GONE);
        //Hide also eventually the login error
        txtLoginStatus.setVisibility(View.GONE);

    }
    /**
     * Verify if the user granted the permission
     *
     * @author Stefano Romanello (g3)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    txtPermissionStatus.setVisibility(View.GONE);
                } else {
                    // permission denied
                    // load the textView for error and hide everything else
                    txtPermissionStatus.setVisibility(View.VISIBLE);
                    layoutLogin.setVisibility(View.GONE);
                    layoutDownload.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    /**
     * Verify if the user can do the login
     * Check internet and file for login
     *
     * @author Stefano Romanello (g3)
     */
    private void verifyDoLogin()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //Check and if there is no internet connection Ask for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        }

        //Verify login file
        final File file = new File(LOGININFORMATION_FILE);

        //Will show the correct layout even if I dont have the writing permission.

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
    }




}
