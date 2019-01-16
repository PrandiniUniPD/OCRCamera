package unipd.se18.ocrcamera;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/*
 * @author Luca Moroldo
 * @auhor Pietro Balzan
 */
public class AllergensSearchFragment extends Fragment {

    private final String TAG = "AllergensSearchFragment";
    private ArrayList<Allergen> allergensFullList;
    private ListView allergensListView;
    private AllergenListAdapter mAllergensListAdapter;
    private Button mSearchButton;

    private AllergensManager mAllergensManager;

    private AutoCompleteTextView mAllergensAutoCompleteTextView;
    //show the suggestions dropdown list with at leas 2 chars typed
    private static final int SUGGESTION_THRESHOLD = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @android.support.annotation.Nullable Bundle savedInstanceState) {
        //the inflater is not considered as child of "parent"
        final boolean isParent = false;

        return inflater.inflate(R.layout.fragment_allergens_search, container,  false);
    }

    /**
     * This method is used to get the View that will make the fragment' layout in the Activity
     * @param fragmentView the view that was inflated in onCreateView
     * @param savedInstanceState
     * @author Pietro Balzan
     */
    @Override
    public void onViewCreated(@NonNull View fragmentView, @android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragmentView, savedInstanceState);

        //Fragments Layout components
        allergensListView = (ListView) fragmentView.findViewById(R.id.allergens_list_view);
        mSearchButton= (Button) fragmentView.findViewById(R.id.allergens_search_button);
        mAllergensAutoCompleteTextView = (AutoCompleteTextView)
                fragmentView.findViewById(R.id.allergen_auto_complete_text_view);

        //initialize the manager used to manipulate the list of allergens
        mAllergensManager = InciSingleton.getInstance(getActivity()).getAllergensManager();

        //initialize the whole list of allergens
        allergensFullList= mAllergensManager.getAllergensList();

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchedIngredient= mAllergensAutoCompleteTextView.getText().toString();
                Log.i(TAG, "The typed string is "+searchedIngredient);
                //list of all allergens shown as result of the search
                ArrayList<Allergen> searchResultList= search(searchedIngredient);

                //show list to user
                mAllergensListAdapter= new AllergenListAdapter(getActivity(),
                        R.layout.allergen_single, searchResultList);
                allergensListView.setAdapter(mAllergensListAdapter);

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
        for (int i=0; i<allergensFullList.size(); i++){
            Log.i(TAG, "looking for allergen");
            Allergen mAllergen= allergensFullList.get(i);
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
                    getActivity().getApplicationContext(),
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
                    mAllergensListAdapter= new AllergenListAdapter(getActivity(),
                            R.layout.allergen_single, searchResultList);
                    allergensListView.setAdapter(mAllergensListAdapter);
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
