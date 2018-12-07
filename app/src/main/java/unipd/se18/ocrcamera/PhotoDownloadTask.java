package unipd.se18.ocrcamera;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


/**
 * @author Leonardo Rossi (g2) and Stefano Romanello (g3)
 */
public class PhotoDownloadTask extends AsyncTask<Void, Integer, Void>
{

    private FTPClient ftp;
    //private ArrayList<String> messages;
    private Context context;
    private ProgressBar progressBar;
    private TextView textViewProgress;
    private Button button;
    private Integer currentProgress;
    private ScrollView scrollCurrentDownload;

    //Constants
    // PLEASE DON'T USE THESE FOR PERSONAL USAGE!
    // FTP CREDENTIALS
    // USERNAME: epiz_22864730
    // PASSWORD: 8M0tNtsJCsw
    // HOSTNAME: ftpupload.net
    // PLEASE DON'T USE THESE FOR PERSONAL USAGE!
    // Communicate with Group 3 if you need something from the server.
    private final String USERNAME = "epiz_22864730";
    private final String PASSWORD = "8M0tNtsJCsw";
    private final String HOSTNAME = "ftpupload.net";
    private final String PHOTOS_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/OCRCameraDB";
    private final String REMOTE_FOLDER = "/htdocs/foto/";
    private final String TAG = "FTP";
    DownloadDbActivity activity;
    PhotoDownloadTask(Context context) { this.context = context; }

    @Override
    protected void onPreExecute()
    {
        activity = (DownloadDbActivity) context;
        ftp = new FTPClient();
        progressBar = ((DownloadDbActivity) context).findViewById(R.id.progressBar);
        textViewProgress = ((DownloadDbActivity) context).findViewById(R.id.textViewProgress);
        button = ((DownloadDbActivity) context).findViewById(R.id.downloadDbButton);
        currentProgress = 0;
        button.setEnabled(false);
        scrollCurrentDownload = activity.findViewById(R.id.scrollView);
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        try
        {
            Boolean isConnected = connectToServer();

            if (isConnected)
            {
                retrieveFiles();
            }
        }
        catch (Exception e)
        {
            sendMessageToUI(e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void params)
    {
        //Get objects ready for next download
        currentProgress=0;
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
     */
    private Boolean connectToServer() throws IOException
    {
        //Trying to connect to the server
        ftp.connect(HOSTNAME);

        //Logging in into the server
        if (!ftp.login(USERNAME, PASSWORD))
        {
            ftp.logout();
            return false;
        }

        int reply = ftp.getReplyCode();
        //FTPReply stores a set of constants for FTP reply codes.
        if (!FTPReply.isPositiveCompletion(reply))
        {
            ftp.disconnect();
            return false;
        }

        //enter passive mode
        ftp.enterLocalPassiveMode();
        //get system name
        System.out.println("Remote system is " + ftp.getSystemType());
        //change current directory
        ftp.changeWorkingDirectory(REMOTE_FOLDER);
        System.out.println("Current directory is " + ftp.printWorkingDirectory());

        return true;
    }

    /**
     * Retrieves files from the server
     * @throws IOException if an error occurs while retrieving files from server
     */
    private void retrieveFiles() throws IOException
    {
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

    /**
     * Update the textView in the UI
     */
    private void sendMessageToUI(String message)
    {
        final String messageToSend = message;
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                TextView txtViewCurrentDownload = activity.findViewById(R.id.textViewCurrentDownload);
                txtViewCurrentDownload.append(messageToSend);
                scrollCurrentDownload.smoothScrollTo(0, txtViewCurrentDownload.getBottom());
            }
        });
    }
}
