package unipd.se18.ocrcamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class TestResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        RecyclerView results = findViewById(R.id.test_entries_list);
        //results.setAdapter(new AdapterTestEntry(this, JSONReportParser.parseReport(stringofJSONContent))); //TODO
    }
}
