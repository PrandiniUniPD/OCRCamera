package unipd.se18.ocrcamera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author Leonardo Rossi (g2) and Stefano Romanello (g3)
 */
public class PhotoDownloadTask extends AsyncTask<Void, Integer, Void>
{

    private FTPClient ftp;
    private Context context;
    private ProgressBar progressBar;
    private TextView textViewProgress;
    private Button button;
    private Integer currentProgress;
    private ScrollView scrollCurrentDownload;
    private TextView txtViewCurrentDownload;
    private TextView txtLoginStatus;
    private LinearLayout layoutDownload;
    private LinearLayout layoutLogin;

    private final String PHOTOS_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/OCRCameraDB";
    private final String LOGINGINFORMATION_FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/ingsoftwareftp.txt";
    private final String REMOTE_FOLDER = "/htdocs/foto/";
    private final String TAG = "FTP";
    DownloadDbActivity activity;
    PhotoDownloadTask(Context context) { this.context = context; }

    @Override
    protected void onPreExecute()
    {
        activity = (DownloadDbActivity) context;
        ftp = new FTPClient();
        progressBar = activity.findViewById(R.id.progressBar);
        textViewProgress = activity.findViewById(R.id.textViewProgress);
        button = activity.findViewById(R.id.downloadDbButton);
        scrollCurrentDownload = activity.findViewById(R.id.scrollView);
        txtViewCurrentDownload = activity.findViewById(R.id.textViewCurrentDownload);
        txtLoginStatus = (TextView) activity.findViewById(R.id.txtLoginStatusDownload);
        layoutDownload = (LinearLayout) activity.findViewById(R.id.LayoutDownload);
        layoutLogin = (LinearLayout) activity.findViewById(R.id.LayoutLogin);

        //Reset download from the prevous downloads + disable button for preventing multiple downloads
        currentProgress = 0;
        button.setEnabled(false);
        txtViewCurrentDownload.setText("");
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
         Boolean isConnected = connectToServer();

         if (isConnected)
         {
             retrieveFiles();
         }

        return null;
    }

    @Override
    protected void onPostExecute(Void params)
    {
        button.setEnabled(true);
    }

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        //Update the progressBar
        progressBar.setProgress(values[0]);
        textViewProgress.setText("Status: " + values[0]+" of " + progressBar.getMax());
    }

    /**
     * Tries to connect the FTPclient to the server
     * @throws IOException if an error occurs during the connection to the server
     * @return True if connected, false otherwise
     * @author Stafano Romanello
     */
    private Boolean connectToServer()
    {
        //Line position in the credential file
        //0 username
        //1 password
        //2 hostname

        //Trying to connect to the server
        try {
            String[] credentials = getFTPCredentials();
            if(credentials.length>0) {
                ftp.connect(credentials[2]);

                //Logging in into the server
                if (!ftp.login(credentials[0], credentials[1])) {
                    ftp.logout();
                }

                int reply = ftp.getReplyCode();
                //FTPReply stores a set of constants for FTP reply codes.
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftp.disconnect();
                }

                //enter passive mode
                ftp.enterLocalPassiveMode();
                //get system name
                System.out.println("Remote system is " + ftp.getSystemType());
                //change current directory
                ftp.changeWorkingDirectory(REMOTE_FOLDER);
                System.out.println("Current directory is " + ftp.printWorkingDirectory());
            }
        } catch (IOException e) {
            //Delete credentials file and ask again on the next reload
            final File file = new File(LOGINGINFORMATION_FILE);
            file.delete();
            showLoginError();
        }
        return true;
    }

    /**
     * Retrieves files from the server
     * @throws IOException if an error occurs while retrieving files from server
     * @author Stefano Romanello
     */
    private void retrieveFiles()
    {
        try {
            //get list of filenames
            FTPFile[] ftpFiles = ftp.listFiles();
            Log.d(TAG,"num file " + ftpFiles.length);

            if (ftpFiles != null && ftpFiles.length > 0)
            {
                progressBar.setMax(ftpFiles.length);
                //Looping through files
                for (FTPFile file: ftpFiles)
                {
                    //Send current progress to "onProgressUpdate"
                    publishProgress(++currentProgress);

                    if (!file.isFile())
                    {
                        //Send message if object is not a file
                        sendMessageToUI("Skipped: not a file" +System.getProperty("line.separator"));
                    }
                    else
                    {
                        //Creation of the destination file for the image downloaded from the server
                        File destinationFile = new File(PHOTOS_FOLDER + "/" + file.getName());

                        //Don't download already downloaded files
                        if (!destinationFile.exists())
                        {
                            OutputStream output;
                            output = new FileOutputStream(PHOTOS_FOLDER + "/" + file.getName());
                            //get the file from the remote system
                            ftp.retrieveFile(file.getName(), output);
                            //close output stream
                            output.close();
                            Log.d(TAG, "Downloaded: " + PHOTOS_FOLDER + "/" + file.getName());

                            //Send message if the file is not already downloaded
                            sendMessageToUI("Downloaded: " + file.getName() +System.getProperty("line.separator"));
                        }
                        else
                        {
                            //Send message if the file is already downloaded
                            sendMessageToUI("Skipped: " + file.getName() +System.getProperty("line.separator"));
                        }
                    }
                }
            }

            Log.d(TAG, "Finished");
            sendMessageToUI("Finished");
            ftp.logout();
            ftp.disconnect();

        }
        catch (Exception e)
        {
            sendMessageToUI("Error retriving files. Retry." + System.getProperty("line.separator") + e.toString());
        }
    }

    /**
     * Update the scrollable textView in the UI
     * @author Stefano Romanello
     */
    private void sendMessageToUI(String message)
    {
        final String messageToSend = message;
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                txtViewCurrentDownload.append(messageToSend);
                scrollCurrentDownload.smoothScrollTo(0, txtViewCurrentDownload.getBottom());
            }
        });
    }
    private void showLoginError()
    {
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                layoutDownload.setVisibility(View.GONE);
                txtLoginStatus.setVisibility(View.VISIBLE);
                layoutLogin.setVisibility(View.VISIBLE);
            }
        });
    }
    /**
     * Update the scrollable textView in the UI
     * Obtain the credentials from the file
     * @author Stefano Romanello
     */
    private String[] getFTPCredentials()
    {
        FileInputStream is;
        BufferedReader reader;
        final File file = new File(LOGINGINFORMATION_FILE);
        
        ArrayList lines= new ArrayList();
        if (file.exists()) {
            try {
                is = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(is));
                String line = reader.readLine();
                lines.add(line);
                while(line != null){
                    line = reader.readLine();
                    lines.add(line);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Object[] objNames = lines.toArray();
        String[] outCredentials = Arrays.copyOf(objNames, objNames.length, String[].class);
        return outCredentials;
    }
}
