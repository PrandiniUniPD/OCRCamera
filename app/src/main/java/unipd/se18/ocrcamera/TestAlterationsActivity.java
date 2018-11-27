package unipd.se18.ocrcamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class TestAlterationsActivity extends AppCompatActivity {

    /**
     * The TestElement entry with alterations
     */
    protected static TestElement entry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_alterations);
        ListView listEntriesView = findViewById(R.id.test_entries_list);
        AdapterTestAlterations adapter = new AdapterTestAlterations(TestAlterationsActivity.this, entry);
        listEntriesView.setAdapter(adapter);
    }
}
