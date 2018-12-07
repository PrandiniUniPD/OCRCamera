package unipd.se18.ocrcamera;

import android.annotation.SuppressLint;
import android.icu.text.LocaleDisplayNames;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class DownloadDbActivity extends AppCompatActivity {

    final String photosFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/OCRCameraDB";
    private ProgressBar progressBar;
    private TextView textViewProgress;
    private TextView textViewCurrentDownload;
    private ScrollView textViewCurrentDownloadScroll;
    private int curentProgress=0;
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

        ///Load UI elements
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textViewProgress = (TextView) findViewById(R.id.textViewProgress);
        textViewCurrentDownload = (TextView) findViewById(R.id.textViewCurrentDownload);
        textViewCurrentDownloadScroll = (ScrollView) findViewById(R.id.scrollView);
        clickButton = (Button) findViewById(R.id.downloadDbButton);
        //final Button clickButton = (Button) findViewById(R.id.downloadDbButton);
        clickButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickButton.setEnabled(false);
                //downloadFromFTP();

                PhotoDownloadTask task = new PhotoDownloadTask(DownloadDbActivity.this);
                task.execute();

                //Reset objects for when I run multiple tests
                textViewCurrentDownload.setText("");
                curentProgress=0;
            }
        });
    }

    /**
     * Download the images from the FTP Server
     *
     * @author Stefano Romanello (g3)
     */
    private void downloadFromFTP() {
        new Thread(new Runnable() {
            public void run() {
                try {

                    //*******************************************************************//
                    //*******************************************************************//
                    //****************************CONNECTION*****************************//
                    //*******************************************************************//
                    //*******************************************************************//

                    // PLEASE DON'T USE THESE FOR PERSONAL USAGE!
                    // FTP CREDENTIALS
                    // USERNAME: epiz_22864730
                    // PASSWORD: 8M0tNtsJCsw
                    // HOSTNAME: ftpupload.net
                    // PLEASE DON'T USE THESE FOR PERSONAL USAGE!
                    // Communicate with Group 3 if you need something from the server.

                    //new ftp client
                    FTPClient ftp = new FTPClient();
                    //try to connect
                    ftp.connect("ftpupload.net");
                    //login to server
                    if (!ftp.login("epiz_22864730", "8M0tNtsJCsw")) {
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
                    ftp.changeWorkingDirectory("/htdocs/foto/");
                    System.out.println("Current directory is " + ftp.printWorkingDirectory());

                    //*******************************************************************//
                    //*******************************************************************//
                    //************************RETRIVE FILES LIST*************************//
                    //*******************************************************************//
                    //*******************************************************************//

                    //get list of filenames
                    FTPFile[] ftpFiles = ftp.listFiles();

                    int numFiles=ftpFiles.length;

                    //Set progress bar lenght
                    progressBar.setMax(numFiles);

                    Log.d("ftp","num file " + ftpFiles.length);

                    if (ftpFiles != null && ftpFiles.length > 0) {

                        //loop thru files
                        for (FTPFile file : ftpFiles) {

                            //Update scroll bars also for non files objects (better performace).
                            //Non file objects are maximum 2/3
                            progressBar.setProgress(++curentProgress);
                            textViewProgress.setText("Status: "+curentProgress+" of "+progressBar.getMax());

                            if (!file.isFile()) {
                                //Send message if object is not a file
                                final String StringToUI="Skipped: not a file" +System.getProperty("line.separator"); //runUiThread requires final variables
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textViewCurrentDownload.append(StringToUI);
                                        textViewCurrentDownloadScroll.smoothScrollTo(0,textViewCurrentDownload.getBottom());
                                    }
                                });
                            }
                            else {
                                //*******************************************************************//
                                //*******************************************************************//
                                //**************************DOWNLOAD FILES***************************//
                                //*******************************************************************//
                                //*******************************************************************//
                                //get output stream
                                File fileExists = new File(photosFolder + "/" + file.getName());
                                //Don't download already downloaded files
                                if (!fileExists.exists()) {
                                    OutputStream output;
                                    output = new FileOutputStream(photosFolder + "/" + file.getName());
                                    //get the file from the remote system
                                    ftp.retrieveFile(file.getName(), output);
                                    //close output stream
                                    output.close();
                                    Log.d("ftp", "Downloaded: " + photosFolder + "/" + file.getName());

                                    //Send message if the file is not already downloaded
                                    final String StringToUI="Downloaded: " + file.getName() +System.getProperty("line.separator"); //runUiThread requires final variables
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textViewCurrentDownload.append(StringToUI);
                                            textViewCurrentDownloadScroll.smoothScrollTo(0,textViewCurrentDownload.getBottom());
                                        }
                                    });
                                } else {
                                    //Send message if the file is already downloaded
                                    final String StringToUI="Skipped: " + file.getName() +System.getProperty("line.separator"); //runUiThread requires final variables
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textViewCurrentDownload.append(StringToUI);
                                            textViewCurrentDownloadScroll.smoothScrollTo(0,textViewCurrentDownload.getBottom());
                                        }
                                    });
                                }
                            }
                        }
                    }

                    ftp.logout();
                    ftp.disconnect();
                    Log.d("ftp", "Finished");
                    final String StringToUI="Finished"; //runUiThread requires final variables
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewCurrentDownload.append(StringToUI);
                            textViewCurrentDownloadScroll.smoothScrollTo(0,textViewCurrentDownload.getBottom());
                            clickButton.setEnabled(true);
                        }
                    });
                } catch (Exception ex) {
                    final String StringToUI=ex.toString(); //runUiThread requires final variables
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewProgress.setText("Something went wrong");
                            textViewCurrentDownload.append(StringToUI);
                            clickButton.setEnabled(true);
                        }
                    });
                    ex.printStackTrace();
                }
            }
        }).start();
    }
}
