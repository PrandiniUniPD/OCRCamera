package unipd.se18.ocrcamera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Activity for showing the result of the tests
 * Luca Moroldo (g2) - Pietro Prandini (g3)
 */
public class TestResultActivity extends AppCompatActivity {
    /**
     * String used for the logs of this class
     */
    private static final String TAG = "TestResultActivity";

    /**
     * The custom request code requested for permission use
     */
    private static final int MY_READ_EXTERNAL_STORAGE_REQUEST_CODE = 300;

    /**
     * Prepares the activity to show the test results.
     * More details at: {@link ActivityCompat#checkSelfPermission(Context, String)}
     * @param savedInstanceState Bundle of the last instance state of the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the layout
        setContentView(R.layout.activity_test_result);

        // Checks the permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
            ActivityCompat.requestPermissions(this, permissions, MY_READ_EXTERNAL_STORAGE_REQUEST_CODE);
            return;
        }

        // Sets the view of the list
        ListView listEntriesView = findViewById(R.id.test_entries_list);

        // Sets the elements of the list as AsyncTask
        AsyncReport report = new AsyncReport(listEntriesView,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "OCRCameraDB",
                getString(R.string.processing));
        report.execute();
    }

    /**
     * Execute the ocr task for every test pic in the storage
     * Luca Moroldo (g3) - Pietro Prandini (g2)
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncReport extends AsyncTask<Void, Void, Void> {
        /**
         * The ListView where showing the results.
         */
        private ListView listEntriesView;

        /**
         * File object that represents where find the directory of the test pics.
         */
        private File environment;

        /**
         * The String of the test pics directory name.
         */
        private String dirName;

        /**
         * The String of the message to show when the task is in progress
         */
        private String progressMessage;

        /**
         * The ProgressDialog object used while the AsyncTask is running
         */
        private ProgressDialog progressDialog;

        /**
         * Instance of PhotoTester used for doing the tests
         */
        private PhotoTester tester;

        /**
         * The String where will be stored the report
         */
        private String report;

        /**
         * Constructor of the class
         * @param listEntriesView The ListView used for showing the results as list
         * @param environment File object that represents where find the directory of the test pics
         * @param dirName The String of the test pics directory name
         * @param progressMessage The String of the message to show when the task is in progress
         */
        AsyncReport(ListView listEntriesView,File environment,
                    String dirName, String progressMessage) {
            this.listEntriesView = listEntriesView;
            this.environment = environment;
            this.dirName = dirName;
            this.progressMessage = progressMessage;
        }

        /**
         * Prepares the task to start
         * More details at: {@link AsyncTask#onPreExecute()}
         */
        @Override
        protected void onPreExecute() {
            // Shows the dialog to the user
            progressDialog = ProgressDialog.show(TestResultActivity.this,
                    progressMessage,
                    "");
        }

        /**
         * Does the task in background - Does the tests
         * More details at: {@link AsyncTask#doInBackground(Object[])}
         * @param voids objects received during the execution
         * @modify tester Instance of PhotoTester used for doing the tests
         * @modify listEntriesView The ListView where showing the results.
         * @modify report The String where will be stored the report
         * @return the result of the objects processed
         */
        @Override
        protected Void doInBackground(Void... voids) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String path = environment + "/" + dirName;
                    Toast.makeText(
                            TestResultActivity.this,
                            "Directory where the photo should be stored:\n"
                                    + path,Toast.LENGTH_LONG
                    ).show();
                }
            });
            this.tester = new PhotoTester(environment,dirName);

            try {
                report = tester.testAndReport();
            } catch (InterruptedException e) {
                e.printStackTrace();
                report = "Elaboration interrupted";
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AdapterTestElement adapter =
                            new AdapterTestElement(
                                    TestResultActivity.this,
                                    tester.getTestElements()
                            );
                    listEntriesView.setAdapter(adapter);
                }
            });
            return null;
        }

        /**
         * End of the task
         * More details at: {@link AsyncTask#onPostExecute(Object)}
         * @param v The object returned by the processing
         * @modify progressDialog The ProgressDialog object used while the AsyncTask is running
         * @modify listEntriesView The ListView where showing the results.
         */
        @Override
        protected void onPostExecute(Void v) {
            progressDialog.dismiss();

            //add statistics author: Francesco Pham
            TextView statsView = new TextView(TestResultActivity.this);
            String statsText = "";

            statsText = tester.getTagsStatsString();

            statsView.setText(statsText);
            listEntriesView.addHeaderView(statsView);
        }
    }

    /**
     * Catches and controls the response of the permissions request
     * More details at: {@link Intent}, {@link Manifest.permission},
     * {@link AppCompatActivity#onRequestPermissionsResult(int, String[], int[])}
     * @param requestCode The code assigned to the request
     * @param permissions The list of the permissions requested
     * @param grantResults The results of the requests
     * @author Pietro Prandini (g2)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_READ_EXTERNAL_STORAGE_REQUEST_CODE: {
                if(grantResults.length == 0 ||
                        grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // Permissions is not granted
                    // notifies it by a toast
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TestResultActivity.this,
                                    R.string.permissions_not_granted, Toast.LENGTH_LONG).show();
                        }
                    });
                    // Destroy the activity
                    finish();
                }
            }
        }

    }
}
