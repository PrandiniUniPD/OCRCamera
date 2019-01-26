package unipd.se18.ocrcamera;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import unipd.se18.ocrcamera.inci.LevenshteinStringDistance;


/**
 * This activity allow the user to manually search for ingredients, providing smart suggestions
 * while the user is typing.
 * Currently it is launched with an ACTION_SEARCH intent from the SearchView contained in the
 * menu, in ResultActivity.java
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
     * Minimum query size to show suggestions
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


        //set button click listener to get text inside AutoCompleteTV, search for ingredients and
        //update mIndgredientsListView adapter. If a research produce an empty result then
        //finds a hint for the user.
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //toUpperCase() used because the database stores data with uppercase only
                String query = mAutoCompleteTextView.getText().toString().toUpperCase();

                //find similar ingredients list
                List<Ingredient> ingredientsFound =
                        ingredientsExtractor.findListIngredients(query);

                //show ingredients list if any
                AdapterIngredient adapter =
                        new AdapterIngredient(getApplicationContext(), ingredientsFound);
                mIngredientsListView.setAdapter(adapter);

                //is the research result is empty, then suggest the closest ingredient in terms
                //of Levenshtein distance
                if(ingredientsFound.size() == 0) {
                    mMessageTextView.setText(getString(R.string.empty_ingredients_list_message));
                    String suggestion = searchSuggestion(query);
                    if(suggestion != null) {
                        SpannableString clickableSuggestion = getClickableSuggestion(suggestion);
                        mMessageTextView.append("\n" + getString(R.string.did_you_mean));
                        mMessageTextView.append(clickableSuggestion);
                        mMessageTextView.append("?");
                        mMessageTextView.setMovementMethod(LinkMovementMethod.getInstance());
                    }

                }

                //hide dropdown hints to make ingredients list visible
                mAutoCompleteTextView.dismissDropDown();
            }
        });

        //add on item list click listener to show a dialog with ingredient's data
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

        //load the complete ingredients list and setup the AutoCompleteTextView
        new AutoCompleteTVSetup().run();

        //handle the calling intent
        handleIntent(getIntent());
    }


    /**
     * If the activity is re-launched then handle the new intent.
     * More information about onNewIntent:
     * https://developer.android.com/reference/android/app/Activity.html#onNewIntent(android.content.Intent)
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * Given an action search intent, create a list view of similar ingredients retrieved
     * from INCI database
     * @param intent an action search intent
     */
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //get query text
            String query = intent.getStringExtra(SearchManager.QUERY);

            //update textview text and set cursor to the end
            mAutoCompleteTextView.setText(query);
            mAutoCompleteTextView.setSelection(query.length());

            //act like the search button was pressed (this will perform a research and update the UI)
            mSearchButton.performClick();
        } else {

            //if the intent action is not an ACTION_SEARCH then close the activity
            // and display an error
            Log.e(TAG, "Activity called without ACTION_SEARCH intent");
            Toast.makeText(getApplicationContext(), "Error doing search", Toast.LENGTH_SHORT)
                    .show();
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
            //specify to run this code on background to avoid slowing down the UI
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            //start suggesting with more than SUGGESTION_THRESHOLD chars typed
            mAutoCompleteTextView.setThreshold(SUGGESTION_THRESHOLD);

            //instance the suggestion adapter with the custom suggestion filter
            dropDownListAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    getFullIngredientsList()){

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


            //if user press enter key then perform search
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
     * This filter provides smart suggestions selected from the full list of INCI ingredients by
     * ordering suggestions alphabetically and prioritizing shorter words that starts with the
     * constraint given by the user.
     * More info about Filter:
     * @see http://developer.android.com/reference/android/widget/Filter
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
            //this function is called every time the user type anything inside the
            //autocomplete textview; the constraint is what the user typed so far

            FilterResults results = new FilterResults();

            //without a constraint, show empty suggestions
            if(constraint == null || constraint.length() == 0) {
                results.count = 0;
                return  results;
            }

            // if the constraint is more restricting than the last, use as full
            // list the last one and save time
            List<String> fullList = null;
            if(constraint.length() - lastSize > 0) {
                fullList = lastList;
            } else {
                // otherwise get a copy of the full ingredients list
                fullList = getFullIngredientsList();
            }

            //convert the constraint to upper case as the ingredients from the db use
            //upper case only
            final String stringConstraint = constraint.toString().toUpperCase();

            //add to the suggestions any ingredient that contains the constraint
            ArrayList<String> filteredList = new ArrayList<>();
            for(String ingredient : fullList) {
                if(ingredient.contains(stringConstraint)) {
                    filteredList.add(ingredient);
                }
            }

            //sort suggestions (if a suggestions starts with the constraint then show
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

            //update variables for any other constraint
            lastList = (ArrayList<String>) results.values;
            lastSize = constraint.length();

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


    /**
     * Search a suggestion for a mistyped text looking in the INCI database, the suggestion returned
     * will have the smallest Levenshtein distance.
     * @param query text for which will be searched a suggestion
     * @return a suggestion that has the minimum Levenshtein distance inside the full ingredients
     * list.
     */
    private String searchSuggestion(String query) {
        String suggestion = null;
        LevenshteinStringDistance levenshteinStringDistance =
                new LevenshteinStringDistance();
        ArrayList<String> fullList = getFullIngredientsList();

        //find the closest ingredient in terms of Levenshtein distance
        int minIndex = -1;
        double minDistance = Double.MAX_VALUE;
        for(int i = 0; i < fullList.size(); i++) {

            double distance = levenshteinStringDistance
                    .getNormalizedDistance(fullList.get(i), query);

            if(distance < minDistance) {
                minDistance = distance;
                minIndex = i;
            }
        }

        if(minIndex > -1)
            suggestion = fullList.get(minIndex);

        return suggestion;
    }


    /**
     * Given a non-null text, return a SpannableString that once clicked updates
     * mAutoCompleteTextView and perform a search.
     * @param text String to convert into SpannableString
     * @return clickable SpannableString
     */
    private SpannableString getClickableSuggestion(final String text) {
        SpannableString spannableString = new SpannableString(text);

        //create an onclick listener that, on click, updates mAutoCompleteTextView and perform
        //a research
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                mAutoCompleteTextView.setText(text);
                mAutoCompleteTextView.setSelection(text.length());
                mSearchButton.performClick();
            }
        };

        //set the on click listener for the whole text.
        //Spanned.SPAN_EXCLUSIVE_EXCLUSIVE do not expand to include text inserted at either
        // their starting or ending point
        //see: https://developer.android.com/reference/android/text/Spanned.html#SPAN_EXCLUSIVE_EXCLUSIVE
        spannableString.setSpan(
                clickableSpan,
                0,
                text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        return spannableString;
    }
}
