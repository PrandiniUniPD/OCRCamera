package unipd.se18.ocrcamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class TestResultActivity extends AppCompatActivity {
    private  static final String TAG = "TestResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);


        //try 1
        /*
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

        File myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "folder");

        path = myDir.getAbsolutePath();
        Log.v(TAG, "PATH => " + path);

        //path += "/foto";

        //Log.v(TAG, "PATH => " + path);

        PhotoTester photoTester = new PhotoTester(path);
        String report = photoTester.testAndReport();
        ListView results = findViewById(R.id.test_entries_list);
        AdapterTestEntry adapter = new AdapterTestEntry(this, JSONReportParser.parseReport(report));
        results.setAdapter(adapter);
        */
        //End try 1


        //try 2
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);


        String path = directory.getAbsolutePath();

        Log.v(TAG, "PATH => " + path);

        Toast.makeText(getApplicationContext(), "The path of test directory is: " + path, Toast.LENGTH_LONG).show();
        PhotoTester photoTester = new PhotoTester(path);
        String report = photoTester.testAndReport();
        ListView results = findViewById(R.id.test_entries_list);
        AdapterTestEntry adapter = new AdapterTestEntry(this, JSONReportParser.parseReport(report));
        results.setAdapter(adapter);


        //end try 2

        //TODO fix select folder dialog
        /*
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent,666);

        */




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
}
