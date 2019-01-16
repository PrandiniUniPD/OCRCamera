package unipd.se18.ocrcamera;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Credentials;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;


public class DownloadDbActivity extends AppCompatActivity {

    private Button clickButtonDownload;
    private Button clickButtonLogin;
    private final String LOGININFORMATION_FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/ingsoftwareftp.txt";
    private final String LOGININFORMATION_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"";
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

        verifyDoLogin();

    }

    /**
     * Load the infos from EditText and create the login file
     *
     * @author Stefano Romanello (g3)
     */
    private void doLogin()
    {
        //When I do the login I will save the credentials so I don't need to re-write them each time
        //If the creadentials are wrong they will be auto-deleted when I try to download the files.
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        String hostname = txtHostname.getText().toString();
        try {
            File dirDocuments = new File(LOGININFORMATION_DIRECTORY);
            if(!dirDocuments.exists() || !dirDocuments.isDirectory())
            {
                dirDocuments.mkdir();
            }

            FileOutputStream fOut = new FileOutputStream(LOGININFORMATION_FILE);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));

            bw.write(username);
            bw.newLine();
            bw.write(password);
            bw.newLine();
            bw.write(hostname);

            bw.close();

            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            layoutLogin.setVisibility(View.GONE);
            txtLoginStatus.setVisibility(View.VISIBLE);
        }


        //In the login section I also check if the folder for photos exists
        File dirPhotos = new File(PHOTOS_FOLDER);
        if(!dirPhotos.exists() || !dirPhotos.isDirectory())
        {
            dirPhotos.mkdir();
        }


        DownloadCredentials credentials = new DownloadCredentials(username,password,hostname);
        //Launch only the login part and understand if the the login is successful
        VerifyLoginCredentials loginCredentials = new VerifyLoginCredentials();
        loginCredentials.execute(credentials);

        setCustomLoginListener(new OnCustomLoginListener()
        {
            public void onLogin(Boolean returnStatus)
            {
                if(returnStatus)
                {
                    layoutDownload.setVisibility(View.VISIBLE);
                    layoutLogin.setVisibility(View.GONE);
                    txtLoginStatus.setVisibility(View.GONE);
                }
                else
                {
                    layoutDownload.setVisibility(View.GONE);
                    txtLoginStatus.setVisibility(View.VISIBLE);
                    layoutLogin.setVisibility(View.VISIBLE);
                }
            }
        });

    }
    /**
     * Verify if the user granted the permission
     *
     * @author Stefano Romanello (g3)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode==REQUEST_PERMISSION_CODE)
        {
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
        }
    }

    /**
     * Verify if the user can do the login
     *
     * @author Stefano Romanello (g3)
     */
    private void verifyDoLogin()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //Check and in case Ask for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        }

        //Verify if the login is already done and if there is internet connection
        final File file = new File(LOGININFORMATION_FILE);

        if(cm.getActiveNetworkInfo() == null) //No internet
        {
            txtInternetStatus.setVisibility(View.VISIBLE);
        }
        else if (!file.exists()) //No file, have to do the login
        {
            layoutLogin.setVisibility(View.VISIBLE);
        }
        else if(file.exists()) //Can do the login
        {
            layoutDownload.setVisibility(View.VISIBLE);
        }
    }


    private class VerifyLoginCredentials extends AsyncTask<DownloadCredentials, Integer, Boolean>
    {

        private FTPClient ftp;
        private final String REMOTE_FOLDER = "/htdocs/foto/";

        @Override
        protected void onPreExecute()
        {
            ftp = new FTPClient();
        }

        /***
         * Method used to check if the credentials are correct
         * @param voids 1st position username 2nd position password 3rd position hostname
         * @return true if the credentials are correct.
         */
        @Override
        protected Boolean doInBackground(DownloadCredentials... voids)
        {
            if(voids.length!=3)
                return connectToServer(voids[0].username, voids[0].password, voids[0].hostname);
            else
                return false; //Return false because I don't have enough credentials.
        }

        @Override
        protected void onPostExecute(Boolean params)
        {
            loginListener.onLogin(params);
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {

        }

        /**
         * Tries to connect the FTPclient to the server
         * @throws IOException if an error occurs during the connection to the server
         * @return True if connected, false otherwise
         * @author Stafano Romanello
         */
        private Boolean connectToServer(String username,String  password,String  hostname)
        {
            //Trying to connect to the server
            //IOException needed by the class of the ftp client
            try {
                ftp.connect(hostname);
                //Logging in into the server
                if (!ftp.login(username, password)) {
                    ftp.logout();
                    throw new IOException();
                }
                int reply = ftp.getReplyCode();
                //FTPReply stores a set of constants for FTP reply codes.
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftp.disconnect();
                    throw new IOException();
                }
                //enter passive mode
                ftp.enterLocalPassiveMode();
                //change current directory
                ftp.changeWorkingDirectory(REMOTE_FOLDER);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    private interface OnCustomLoginListener {
        void onLogin(Boolean connectionStatus);
    }

    OnCustomLoginListener loginListener;

    private void setCustomLoginListener(OnCustomLoginListener loginListener) {
        loginListener = loginListener;
    }

    public class DownloadCredentials
    {
        public String hostname;
        public String password;
        public String username;
        public DownloadCredentials(String host, String psw, String user)
        {
            hostname=host;
            password=psw;
            username=user;
        }
    }
}



