package unipd.se18.ocrcamera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;

/**
 * Activity for printing the result
 * Luca Moroldo (g2) - Pietro Prandini (g3)
 */
public class TestResultActivity extends AppCompatActivity {
    private static final String TAG = "TestResultActivity";
    private static final int MY_READ_EXTERNAL_STORAGE_REQUEST_CODE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
            ActivityCompat.requestPermissions(this, permissions, MY_READ_EXTERNAL_STORAGE_REQUEST_CODE);
            return;
        }



        //try 2
        ListView listEntriesView = findViewById(R.id.test_entries_list);
        //TODO execute in background
        AsyncReport report = new AsyncReport(listEntriesView,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "OCRCameraDB",
                getString(R.string.processing));
        report.execute();


    }


    //TODO fix select folder dialog
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 666) {
            String FilePath = data.getData().getPath();
            String FileName = data.getData().getLastPathSegment();
            int lastPos = FilePath.length() - FileName.length();
            String folderPath = FilePath.substring(0, lastPos);

            Log.v(TAG, "PATH => " + folderPath);

        }
    }

    /**
     * Execute the ocr task for every pics and store the report in a String
     * Luca Moroldo (g3) - Pietro Prandini (g2)
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncReport extends AsyncTask<Void, Void, Void> {

        private ListView listEntriesView;
        private File environment;
        private String dirName;
        private String progressMessage;
        private ProgressDialog progressDialog;
        private PhotoTester tester;
        private String report;

        AsyncReport(ListView listEntriesView,File environment, String dirName, String progressMessage) {
            this.listEntriesView = listEntriesView;
            this.environment = environment;
            this.dirName = dirName;
            this.progressMessage = progressMessage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String path = environment + "/" + dirName;
                    Toast.makeText(TestResultActivity.this,"Directory where the photo should be stored:\n" + path,Toast.LENGTH_LONG).show();
                }
            });
            this.tester = new PhotoTester(environment,dirName);
            report = tester.testAndReport();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AdapterTestElement adapter = new AdapterTestElement(TestResultActivity.this, tester.getTestElements());
                    listEntriesView.setAdapter(adapter);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            progressDialog.dismiss();

            //add statistics author: Francesco Pham
            TextView statsView = new TextView(TestResultActivity.this);
            String statsText = "";
            try {
                statsText = tester.getTagsStatsString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            statsView.setText(statsText);
            listEntriesView.addHeaderView(statsView);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(TestResultActivity.this,
                    progressMessage,
                    "");
        }
    }

    /**
     * Controls the output of the permissions requests.
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
                if(grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // Notify by a toast
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
