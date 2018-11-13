package unipd.se18.ocrcamera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = { Manifest.permission.READ_EXTERNAL_STORAGE };
            ActivityCompat.requestPermissions(this, permissions, MY_READ_EXTERNAL_STORAGE_REQUEST_CODE);
            return;
        }


        /*
        ListView listEntriesView = findViewById(R.id.test_entries_list);
        //TODO execute in background
        AsyncReport report = new AsyncReport(listEntriesView, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "OCRCameraDB",
                getString(R.string.processing));
        report.execute();
        */


        //TODO this solution must be tested

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("file/*");
        startActivityForResult(Intent.createChooser(i, "Choose directory"), 666);





    }

    /**
     *
     * @param uri URI pointing a file inside the storage
     * @return String path of the uri
     */
    public String getRealPathFromURI(Uri uri)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, proj,
                null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 666) {

            String filePath = getRealPathFromURI(data.getData());


            File directory = new File(new File(filePath).getParent());
            Log.v(TAG, "PATH => " + filePath);

            ListView listEntriesView = findViewById(R.id.test_entries_list);

            AsyncReport report = new AsyncReport(listEntriesView, directory,
                    getString(R.string.processing));
            report.execute();

        }
    }

    //TODO execute in background
    /**
     * Execute the ocr task for every pics and store the report in a String
     * Luca Moroldo (g3) - Pietro Prandini (g2)
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncReport extends AsyncTask<Void, Void, Void> {

        private ListView listEntriesView;
        private File directory;
        private String progressMessage;
        private ProgressDialog progressDialog;
        private PhotoTester tester;
        private String report;

        AsyncReport(ListView listEntriesView,File directory, String progressMessage) {
            this.listEntriesView = listEntriesView;
            this.directory = directory;

            this.progressMessage = progressMessage;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            this.tester = new PhotoTester(directory);
            report = tester.testAndReport();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AdapterTestEntry adapter = new AdapterTestEntry(TestResultActivity.this, JSONReportParser.parseReport(report));
                    listEntriesView.setAdapter(adapter);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            progressDialog.dismiss();
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
