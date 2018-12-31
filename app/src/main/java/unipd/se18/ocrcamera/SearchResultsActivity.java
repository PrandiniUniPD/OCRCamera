package unipd.se18.ocrcamera;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import unipd.se18.ocrcamera.inci.Ingredient;
import unipd.se18.ocrcamera.inci.TextSplitIngredientsExtractor;

public class SearchResultsActivity extends AppCompatActivity {

    private static final String TAG = "SearchResultsActivity";


    private ListView mIngredientsListView;

    private TextSplitIngredientsExtractor ingredientsExtractor;

    private TextView mMessageTextView;

    private AutoCompleteTextView mAutoCompleteTextView;

    private List<Ingredient> listInciIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO: add button to do the ingredient web search rather than doing it on item click

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //layout components
        mIngredientsListView = findViewById(R.id.ingredients_list);
        mMessageTextView = findViewById(R.id.message_text_view);
        mAutoCompleteTextView = findViewById(R.id.ingredients_auto_complete_text_view);
        Button searchButton = findViewById(R.id.auto_complete_text_view_button);

        //set on search button clicked
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchShowIngredients(mAutoCompleteTextView.getText().toString());
            }
        });

        //mMessageTextView is used to show messages
        mIngredientsListView.setEmptyView(mMessageTextView);

        //add listener to launch a websearch on item click
        mIngredientsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ingredient selectedIngredient = (Ingredient) parent.getItemAtPosition(position);
                String inciName = selectedIngredient.getInciName();
                String description = selectedIngredient.getDescription();
                String function = selectedIngredient.getFunction();
                FragmentManager fm = getSupportFragmentManager();
                IngredientDetailsFragment detailsFragment = IngredientDetailsFragment.newInstance(inciName, description, function);
                detailsFragment.show(fm, "fragment_ingredient_details");
            }
        });

        //inci db ingredients finder
        listInciIngredients = InciSingleton.getInstance(getApplicationContext()).getListInciIngredients();
        ingredientsExtractor = new TextSplitIngredientsExtractor(listInciIngredients);

        new LoadSuggestions().run();

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * Given an action search intent, create a list view of similar ingredients retrieved
     * from INCI database
     * @param intent action search intent
     */
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //get query text
            String query = intent.getStringExtra(SearchManager.QUERY);

            //update textview text and set cursor to the end
            mAutoCompleteTextView.setText(query);
            mAutoCompleteTextView.setSelection(query.length());


            //find similar ingredients list
            searchShowIngredients(query);

        } else {
            //if the intent action is not an ACTION_SEARCH then close the activity and display an error
            Log.e(TAG, "Activity called without ACTION_SEARCH intent");
            Toast.makeText(getApplicationContext(), "Error doing search", Toast.LENGTH_SHORT).show();
            finish();
        }
    }




    /**
     * Load all INCI names, set them as suggestions for the AutoCompleteTextView and set a
     * suggestion click listener
     */
    private class LoadSuggestions implements Runnable {
        @Override
        public void run() {

            //create an arraylist with all ingredient names
            ArrayList<String> ingredientNamesList = new ArrayList<>();
            for(Ingredient ingredient : listInciIngredients)
                ingredientNamesList.add(ingredient.getInciName());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1,
                            ingredientNamesList
                    );

            //start suggesting with more that 2 chars typed
            mAutoCompleteTextView.setThreshold(2);
            mAutoCompleteTextView.setAdapter(adapter);


            //on suggestion click: update ingredients listview
            mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String ingredientClicked = parent.getItemAtPosition(position).toString();

                    //update TextView and set cursor at the end of the text
                    mAutoCompleteTextView.setText(ingredientClicked);
                    mAutoCompleteTextView.setSelection(ingredientClicked.length());

                    //find similar ingredients (will retrieve one result)
                    searchShowIngredients(ingredientClicked);
                }
            });
        }
    }


    private void searchShowIngredients(String query){
        final double percentMaxDistance = 0.25;
        int maxDistance = (int) (query.length()*percentMaxDistance);
        List<Ingredient> ingredientsFound = ingredientsExtractor.findMostSimilarIngredients(query, maxDistance);

        //show ingredients list if any
        AdapterIngredient adapter =
                new AdapterIngredient(getApplicationContext(), ingredientsFound);
        mIngredientsListView.setAdapter(adapter);

        if(ingredientsFound.size() == 0) {
            //message that nothing has been found
            mMessageTextView.setText("Nothing found");
        }
    }


}
