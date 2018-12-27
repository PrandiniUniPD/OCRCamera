package unipd.se18.ocrcamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AllergensActivity extends AppCompatActivity {

    ListView allergensView;
    AllergenListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergens);

        //initialize values used to show the list of allergens
        allergensView = findViewById(R.id.allergens_list_view);

        //list of all allergens shown as result of the search
        ArrayList<Allergen> allergensList = new ArrayList<>();

        Allergen a =new Allergen("aaaa", true );
        Allergen b =new Allergen("bbbb", false);
        Allergen c = new Allergen("cccc", true);
        Allergen d = new Allergen("dddd", false);
        Allergen e = new Allergen("eeee", true);
        Allergen f= new Allergen("ffff", false);
        //add allergens to the list
        allergensList.add(a);
        allergensList.add(b);
        allergensList.add(c);
        allergensList.add(d);
        allergensList.add(e);
        allergensList.add(f);

        //create adapter
        adapter= new AllergenListAdapter(this, R.layout.allergen_single, allergensList);
        allergensView.setAdapter(adapter);
    }
}
