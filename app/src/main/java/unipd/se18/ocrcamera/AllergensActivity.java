package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergens);

        //initialize values used to show the list of allergens
        allergensView = findViewById(R.id.allergens_list_view);
        AllergensManager mAllergensManager= new AllergensManager(this);

        //initialize the whole list of allergens
        wholeList= mAllergensManager.getAllergensList();
        //initislize user's own list
        ArrayList<Allergen> userList= mAllergensManager.getSelectedAllergensList();

        //create adapter with the list of the users' allergens
        adapter= new AllergenListAdapter(this, R.layout.allergen_single, userList);
        allergensView.setAdapter(adapter);

        //create EditText View
        final EditText searchBar= findViewById(R.id.allergenText);

        //create button and set listener
        Button searchButton= findViewById(R.id.allergens_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String searchedIngredient= searchBar.getText().toString();
               Log.i(TAG, "The typed string is "+searchedIngredient);
               //list of all allergens shown as result of the search
               ArrayList<Allergen> searchResultList= search(searchedIngredient);

               //show list to user
               adapter= new AllergenListAdapter(AllergensActivity.this,
                       R.layout.allergen_single, searchResultList);
                allergensView.setAdapter(adapter);
            }
        });
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
}
