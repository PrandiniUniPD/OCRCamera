package unipd.se18.ocrcamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class TestResultActivity extends AppCompatActivity {

    /**
     * The path where the database is saved
     */
    private final String dirPath = ""; //TODO the dirpath where the database is saved

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        PhotoTester tester = new PhotoTester(dirPath);

        RecyclerView results = findViewById(R.id.test_entries_list);
        //results.setAdapter(new AdapterTestEntry()); //TODO
    }
}
