package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AllergensActivity extends AppCompatActivity {

    private final String TAG = "AllergensActivity";
    ListView allergensView;
    AllergenListAdapter adapter;
    ArrayList<Allergen> wholeList;
    private Button mSearchButton;

    private AllergensManager mAllergensManager;

    private AutoCompleteTextView mAllergensAutoCompleteTextView;
    //show the suggestions dropdown list with at leas 2 chars typed
    private int SUGGESTION_THRESHOLD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergens);

        //Activity components
        allergensView = findViewById(R.id.allergens_list_view);
        mSearchButton= findViewById(R.id.allergens_search_button);
        mAllergensAutoCompleteTextView = findViewById(R.id.allergen_auto_complete_text_view);

        //initialize values used to show the list of allergens
        mAllergensManager= new AllergensManager(this);

        //initialize the whole list of allergens
        wholeList= mAllergensManager.getAllergensList();
        //initislize user's own list
        ArrayList<Allergen> userList= mAllergensManager.getSelectedAllergensList();

        //create adapter with the list of the users' allergens
        adapter= new AllergenListAdapter(this, R.layout.allergen_single, userList);
        allergensView.setAdapter(adapter);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String searchedIngredient= mAllergensAutoCompleteTextView.getText().toString();
               Log.i(TAG, "The typed string is "+searchedIngredient);
               //list of all allergens shown as result of the search
               ArrayList<Allergen> searchResultList= search(searchedIngredient);

               //show list to user
               adapter= new AllergenListAdapter(AllergensActivity.this,
                       R.layout.allergen_single, searchResultList);
                allergensView.setAdapter(adapter);

                //after a click hide dropdown suggestions
                mAllergensAutoCompleteTextView.dismissDropDown();
            }
        });

        //setup allergens AutoCompleteTextView
        new prepareAllergenAutoTextView().run();
    }

    /**
     * This method returns an arraylist of allergens as a result of the search
     * @param searchedIngredient a String to compare to the allergens names
     * @return resultList an ArrayList
     */
    private ArrayList<Allergen> search (String searchedIngredient) {
        ArrayList<Allergen> resultList= new ArrayList<Allergen>();
        for (int i=0; i<wholeList.size(); i++){
            Log.i(TAG, "looking for allergen");
            Allergen mAllergen= wholeList.get(i);
            if (mAllergen.getCommonName().equals(searchedIngredient)){
                Log.i(TAG, "Matching allergen Found!");
                resultList.add(mAllergen);
            }
        }
        return resultList;
    }

    /**
     * Runnable used to load the suggestions and setup the auto complete text view
     * On suggestion click: update the activity listview
     * On enter-key press: perform button click
     * Author: Luca Moroldo (g3)
     */
    private class prepareAllergenAutoTextView implements Runnable {
        @Override
        public void run() {

            //show dropdown suggestions if there are at least SUGGESTION_THRESHOLD chars
            mAllergensAutoCompleteTextView.setThreshold(SUGGESTION_THRESHOLD);

            //get the list of allergens
            ArrayList<Allergen> allergensList = mAllergensManager.getAllergensList();

            //create a list of allergens names
            ArrayList<String> allergenNamesList = new ArrayList<>();
            for(Allergen allergen : allergensList)
                allergenNamesList.add(allergen.getCommonName());

            //create and set an adapter for allergen names for the autoCompleteTextView
            ArrayAdapter<String> allergenNamesAdapter = new ArrayAdapter<>(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    allergenNamesList
            );
            mAllergensAutoCompleteTextView.setAdapter(allergenNamesAdapter);

            //on suggestion click: update the view
            mAllergensAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String allergenClicked = parent.getItemAtPosition(position).toString();

                    //update the textbox and set cursor at the end
                    mAllergensAutoCompleteTextView.setText(allergenClicked);
                    mAllergensAutoCompleteTextView.setSelection(allergenClicked.length());

                    //search for allergens with that name and update view
                    ArrayList<Allergen> searchResultList= search(allergenClicked);
                    adapter= new AllergenListAdapter(AllergensActivity.this,
                            R.layout.allergen_single, searchResultList);
                    allergensView.setAdapter(adapter);
                }
            });



            //if user press enter key then run a search
            mAllergensAutoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(KeyEvent.KEYCODE_ENTER == keyCode) {
                        mSearchButton.performClick();
                        return true;
                    }
                    return false;
                }
            });


            //Show the allergens when the textView is clicked
            mAllergensAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    mAllergensAutoCompleteTextView.showDropDown();
                }
            });
        }
    }
}
