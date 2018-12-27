package unipd.se18.ocrcamera;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import unipd.se18.ocrcamera.inci.Ingredient;
import unipd.se18.ocrcamera.inci.IngredientsExtractor;

public class SearchResultsActivity extends AppCompatActivity {

    private static final String TAG = "SearchResultsActivity";


    private ListView mIngredientsListView;

    private IngredientsExtractor ingredientsExtractor;

    private TextView mMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //layout components
        mIngredientsListView = findViewById(R.id.ingredients_list);
        mMessageTextView = findViewById(R.id.message_text_view);

        //mMessageTextView is used to show messages
        mIngredientsListView.setEmptyView(mMessageTextView);

        //inci db ingredients finder
        ingredientsExtractor = IngredExtractorSingleton.getInstance(getApplicationContext());

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            List<Ingredient> ingredientsFound = ingredientsExtractor.findListIngredients(query);
            if(ingredientsFound != null && ingredientsFound.size() > 0) {
                //show ingredients list if any
                AdapterIngredient adapter =
                        new AdapterIngredient(getApplicationContext(), ingredientsFound);
                mIngredientsListView.setAdapter(adapter);

            } else {
                //message that nothing has been found
                mMessageTextView.setText("Nothing found");
            }

        }
    }

    @Override
    public boolean onSearchRequested() {

        Log.d(TAG, "search triggered");

        return false;  // don't go ahead and show the search box
    }

}
