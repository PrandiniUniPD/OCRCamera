package unipd.se18.ocrcamera.performancetester;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import unipd.se18.ocrcamera.R;


public class DownloadDbFragment extends Fragment {

    private Button clickButtonDownload;
    private Button clickButtonLogin;
    private final String LOGINGINFORMATION_FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/ingsoftwareftp.txt";
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_download_db, container, false);
    }

    /**
     * Instantiate the UI elements and check if is possible to do the login.
     *
     * @author Stefano Romanello (g3)
     */
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutDownload = (LinearLayout) getActivity().findViewById(R.id.LayoutDownload);
        layoutLogin = (LinearLayout) getActivity().findViewById(R.id.LayoutLogin);
        txtHostname = (EditText) getActivity().findViewById(R.id.txtHostnameDownload);
        txtPassword = (EditText) getActivity().findViewById(R.id.txtPasswordDownload);
        txtUsername = (EditText) getActivity().findViewById(R.id.txtUsernameDownload);
        txtInternetStatus = (TextView) getActivity().findViewById(R.id.txtInternetStatusDownload);
        txtPermissionStatus = (TextView) getActivity().findViewById(R.id.txtPermissionStatusDownload);
        txtLoginStatus = (TextView) getActivity().findViewById(R.id.txtLoginStatusDownload);

        ///Load other UI elements
        clickButtonDownload = (Button) getActivity().findViewById(R.id.downloadDbButton);
        clickButtonDownload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoDownloadTask task = new PhotoDownloadTask(getContext());
                task.execute();
            }
        });

        clickButtonLogin = (Button) getActivity().findViewById(R.id.downloadLoginButton);
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

        try {
            File dirDocuments = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"");
            if(!dirDocuments.exists() || !dirDocuments.isDirectory())
            {
                dirDocuments.mkdir();
            }

            FileOutputStream fOut = new FileOutputStream(LOGINGINFORMATION_FILE);

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
            layoutLogin.setVisibility(View.GONE);
            txtLoginStatus.setVisibility(View.VISIBLE);
        }

        //Check if folder for photos exist
        File dirPhotos = new File(PHOTOS_FOLDER);
        if(!dirPhotos.exists() || !dirPhotos.isDirectory())
        {
            dirPhotos.mkdir();
        }

        layoutDownload.setVisibility(View.VISIBLE);
        layoutLogin.setVisibility(View.GONE);
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
     *
     * @author Stefano Romanello (g3)
     */
    private void verifyDoLogin()
    {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        //Check and in case Ask for permission
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
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
    }




}
