package unipd.se18.ocrcamera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity for showing the result of the tests
 * Pietro Prandini (g2)
 */
public class TestsListActivity extends AppCompatActivity {
    /**
     * String used for the logs of this class
     */
    private static final String TAG = "TestsListActivity";

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
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
            ActivityCompat.requestPermissions(this, permissions,
                    MY_READ_EXTERNAL_STORAGE_REQUEST_CODE);
            return;
        }

        // Sets the view of the list
        ListView listEntriesView = findViewById(R.id.test_entries_list);

        // Sets the elements of the list as AsyncTask
        AsyncReport report = new AsyncReport(listEntriesView);
        report.execute();
    }

    /**
     * Execute the ocr task for every test pic in the storage
     * TraPietro Prandini (g2)
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncReport extends AsyncTask<Void, Integer, Void> {
        /**
         * The ListView where showing the results.
         */
        private ListView listEntriesView;

        /**
         * The String of the test pics directory path.
         */
        private String dirPath;

        /**
         * The String of the message to show when the task is in progress
         */
        private String progressMessage;

        /**
         * Instance of PhotoTester used for doing the tests
         */
        private PhotoTester tester;

        /**
         * The String where will be stored the report
         */
        private String report;

        /**
         * The progress bar used for indicating the progress of the tests
         */
        private ProgressBar progressBar;

        /**
         * The text used for indicating the progress of the tests
         */
        private TextView progressText;

        /**
         * The number of tested elements so far
         */
        private int testedElements = 0;
        /**
         * Constructor of the class
         * @param listEntriesView The ListView used for showing the results as list
         */
        AsyncReport(ListView listEntriesView) {
            this.listEntriesView = listEntriesView;
            this.dirPath = PhotoDownloadTask.PHOTOS_FOLDER;
        }

        /**
         * Prepares the task to start
         * More details at: {@link AsyncTask#onPreExecute()}
         */
        @Override
        protected void onPreExecute() {
            progressBar = findViewById(R.id.tests_progress_bar);
            progressText = findViewById(R.id.progress_testing_text);
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
            this.tester = new PhotoTester(getApplicationContext(), dirPath);
            progressBar.setMax(tester.getTestSize());

            // Listener useful for updating the progress bar
            TestListener testListener = new TestListener() {
                @Override
                public void onTestFinished() {
                    // +1 test finished -> +1 progress bar
                    publishProgress(++testedElements);
                }

                @Override
                public void onAlterationAnalyzed() {
                    // not useful in this case
                }
            };
            tester.setTestListener(testListener);

            // publishes progress
            try {
                report = tester.testAndReport();
            } catch (InterruptedException e) {
                e.printStackTrace();
                report = "Elaboration interrupted";
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TestsListAdapter adapter =
                            new TestsListAdapter(
                                    TestsListActivity.this,
                                    tester.getTestElements()
                            );
                    listEntriesView.setAdapter(adapter);
                }
            });

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(testedElements);
            String progress = "Tested: " + values[0] +
                    " of " + tester.getTestSize();
            progressText.setText(progress);
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
            //add statistics author: Francesco Pham
            TextView statsView = new TextView(TestsListActivity.this);
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
                            Toast.makeText(TestsListActivity.this,
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
