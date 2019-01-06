package unipd.se18.ocrcamera;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import unipd.se18.ocrcamera.inci.Ingredient;
import unipd.se18.ocrcamera.inci.IngredientsExtractor;


/**
 * This activity allow the user to manually search for ingredients, providing smart suggestions
 * while the user is typing.
 * It can be launched with an ACTION_SEARCH intent
 * @author Luca Moroldo g3
 */
public class SearchResultsActivity extends AppCompatActivity {

    private static final String TAG = "SearchResultsActivity";

    /**
     * List used to show the results of a search
     */
    private ListView mIngredientsListView;

    /**
     * TexvView used to show messages when the result of a search is empty
     */
    private TextView mMessageTextView;

    /**
     * An extension of EditText that show suggestions with a dropdown list
     */
    private AutoCompleteTextView mAutoCompleteTextView;

    /**
     * Button that on click run a search and update mIngredientsListView with results
     */
    private Button mSearchButton;

    /**
     * Stores all the names of the ingredients inside the INCI db
     */
    private ArrayList<String> fullIngredientsList;

    /**
     * Adapter used to update the dropdown suggestions list
     */
    private ArrayAdapter<String> dropDownListAdapter;

    /**
     * Used to find ingredients from the INCI db with a query
     */
    private IngredientsExtractor ingredientsExtractor;

    /**
     * Size of the dropdown suggestions list
     */
    private static final int MAX_DROPDOWN_SUGGESTIONS = 40;

    /**
     * Minimum query size to show succestions
     */
    private static final int SUGGESTION_THRESHOLD = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //get layout components
        mIngredientsListView = findViewById(R.id.ingredients_list);
        mMessageTextView = findViewById(R.id.message_text_view);
        mAutoCompleteTextView = findViewById(R.id.ingredients_auto_complete_text_view);
        mSearchButton = findViewById(R.id.auto_complete_text_view_button);

        //mMessageTextView is used to show messages
        mIngredientsListView.setEmptyView(mMessageTextView);

        //get an instance of the ingredients extractor
        ingredientsExtractor = InciSingleton.getInstance(getApplicationContext()).getIngredientsExtractor();


        //add an item list click to show a dialog with ingredient's data
        mIngredientsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ingredient selectedIngredient = (Ingredient) parent.getItemAtPosition(position);
                String inciName = selectedIngredient.getInciName();
                String description = selectedIngredient.getDescription();
                String function = selectedIngredient.getFunction();

                //show ingredient's data - fragment credits: Francesco Pham (g3)
                FragmentManager fm = getSupportFragmentManager();
                IngredientDetailsFragment detailsFragment =
                        IngredientDetailsFragment.newInstance(inciName, description, function);
                detailsFragment.show(fm, "fragment_ingredient_details");
            }
        });


        //set button click listener to get text inside AutoCompleteTV and search for ingredients
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String query = mAutoCompleteTextView.getText().toString();

                //find similar ingredients list - toUpperCase() used because the database stores
                //data uppercase only
                List<Ingredient> ingredientsFound =
                        ingredientsExtractor.findListIngredients(query.toUpperCase());

                //show ingredients list if any
                AdapterIngredient adapter =
                        new AdapterIngredient(getApplicationContext(), ingredientsFound);
                mIngredientsListView.setAdapter(adapter);

                if(ingredientsFound.size() == 0)
                    mMessageTextView.setText(getString(R.string.empty_ingredients_list_message));

                //hide dropdown hints to make ingredients list visible
                mAutoCompleteTextView.dismissDropDown();
            }
        });


        //load the complete ingredients list and setup the AutoCompleteTextView
        new AutoCompleteTVSetup().run();


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
            List<Ingredient> ingredientsFound = ingredientsExtractor.findListIngredients(query.toUpperCase());

            if(ingredientsFound != null && ingredientsFound.size() > 0) {

                //show ingredients list if any
                AdapterIngredient adapter =
                        new AdapterIngredient(getApplicationContext(), ingredientsFound);
                mIngredientsListView.setAdapter(adapter);

                //in case of results, hide dropdown suggestions
                mAutoCompleteTextView.dismissDropDown();
            } else {
                //message that nothing has been found
                mMessageTextView.setText("Nothing found");
            }
        } else {

            //if the intent action is not an ACTION_SEARCH then close the activity and display an error
            Log.e(TAG, "Activity called without ACTION_SEARCH intent");
            Toast.makeText(getApplicationContext(), "Error doing search", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    /**
     * Load all INCI ingredients names, set them as suggestions with a custom filter for the
     * AutoCompleteTextView and set a suggestion click listener.
     * The custom filter will show the suggestion in lexicographical order, privileging words that
     * starts with the constraint query or contains a substring that starts with the constraint
     * query.
     */
    private class AutoCompleteTVSetup implements Runnable {
        @Override
        public void run() {

            //start suggesting with more than SUGGESTION_THRESHOLD chars typed
            mAutoCompleteTextView.setThreshold(SUGGESTION_THRESHOLD);

            //get an ArrayList<String> of ingredients names
            fullIngredientsList = getFullIngredientsList();

            //instance the suggestion adapter with the custom suggestion filter
            dropDownListAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    (ArrayList<String>) fullIngredientsList.clone() ){

                //override getFilter to use the custom suggestions filter
                private Filter filter;
                @NonNull
                @Override
                public Filter getFilter() {

                    // return filter if not null
                    if(filter == null) {
                        filter = new IngredientsFilter();
                    }
                    return filter;
                }
            };

            mAutoCompleteTextView.setAdapter(dropDownListAdapter);


            //on suggestion click: update ingredients listview
            mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String ingredientClicked = parent.getItemAtPosition(position).toString();

                    //update TextView and set cursor at the end of the text
                    mAutoCompleteTextView.setText(ingredientClicked);
                    mAutoCompleteTextView.setSelection(mAutoCompleteTextView.getText().length());
                    mSearchButton.performClick();
                }
            });


            //if user press enter key then run a search
            mAutoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(KeyEvent.KEYCODE_ENTER == keyCode) {
                        mSearchButton.performClick();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    /**
     * This filter provides smart suggestions from the full list of INCI ingredients
     * @author Luca Moroldo g3
     */
    private class IngredientsFilter extends Filter {

        /**
         * Stores the size of the last constraint to understand if the user is adding or removing
         * letters
         */
        private int lastSize;

        /**
         * Stores the last dropdown list to save computation time
         */
        private List<String> lastList;

        private IngredientsFilter() {
            super();
            lastSize = 0;
            lastList = getFullIngredientsList();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //Variable constraint contains the chars typed by the user inside the
            //autocomplete textview


            FilterResults results = new FilterResults();

            //without a constraint, show empty suggestions
            if(constraint == null || constraint.length() == 0) {
                results.count = 0;
                return  results;
            }


            // if the constraint is more restricting than the last, we can use as full
            // list the last one and save time
            List<String> fullList = null;
            if(constraint.length() - lastSize > 0) {
                fullList = lastList;
            } else {
                // otherwise we need to get a copy of the full ingredients list
                fullList = (ArrayList<String>) fullIngredientsList.clone();
            }
            //update last size
            lastSize = constraint.length();

            //convert the constraint to upper case as the ingredients from the db use
            //upper case
            final String stringConstraint = constraint.toString().toUpperCase();



            //add to the suggestions any ingredient that contains the constraint
            ArrayList<String> filteredList = new ArrayList<>();
            for(String ingredient : fullList) {
                if(ingredient.contains(stringConstraint)) {
                    filteredList.add(ingredient);
                }
            }

            //show sorted suggestions (if a suggestions starts with the constraint then show
            //it before)
            Collections.sort(filteredList, new Comparator<String> () {
                @Override
                public int compare(String a, String b) {

                    //if both strings starts with the constraint, then show the shorter before
                    if(a.startsWith(stringConstraint) && b.startsWith(stringConstraint))
                        return a.length() - b.length();

                    //if one of the strings starts with the constraint, then show it before
                    //using a big value
                    if(a.startsWith(stringConstraint))
                        return -100;
                    if(b.startsWith(stringConstraint))
                        return +100;

                    String[] subIngredientsA = a.split(" ");
                    String[] subIngredientsB = b.split(" ");

                    //if any of the substrings starts with the constraint, show the full string
                    //before
                    for(int i = 1; i < subIngredientsA.length; i++)
                        if(subIngredientsA[i].startsWith(stringConstraint))
                            return -50;
                    for(int i = 1; i < subIngredientsB.length; i++)
                        if(subIngredientsB[i].startsWith(stringConstraint))
                            return 50;

                    return a.compareToIgnoreCase(b);
                }
            });


            results.values = filteredList;
            results.count = filteredList.size();

            lastList = (ArrayList<String>) results.values;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //this function is called after perform filtering. Update the suggestions list and
            //publish the results if any

            dropDownListAdapter.clear();

            if(results.count > 0) {

                ArrayList<String> filteredResults = (ArrayList<String>) results.values;
                for(int i = 0; i < filteredResults.size() && i < MAX_DROPDOWN_SUGGESTIONS; i++) {
                    dropDownListAdapter.add(filteredResults.get(i));
                }
            }
            dropDownListAdapter.notifyDataSetChanged();

        }
    }

    /**
     * Get the full ingredients names list from the INCI database
     * @return ArrayList of strings, each string is a name of an ingredient
     */
    private ArrayList<String> getFullIngredientsList() {
        //read INCI database
        List<Ingredient> listInciIngredients = InciSingleton.
                getInstance(getApplicationContext()).getListInciIngredients();

        ArrayList<String> fullIngredientsList = new ArrayList<>();
        //create an arraylist with all ingredient names
        for(Ingredient ingredient : listInciIngredients)
            fullIngredientsList.add(ingredient.getInciName());
        return fullIngredientsList;
    }

}
