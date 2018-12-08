package unipd.se18.ocrcamera;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.io.InputStream;
import java.util.ArrayList;

public class IngredientsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String OCRText = prefs.getString("text", null);

        if(OCRText != null) {

            //load inci db
            InputStream inputStream = getResources().openRawResource(R.raw.incidb);
            Inci inci = new Inci(inputStream);

            //find ingredients in inci db
            final ArrayList<Ingredient> ingredients = inci.findListIngredients(OCRText);

            final ListView listEntriesView = findViewById(R.id.ingredients_list);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AdapterIngredient adapter =
                            new AdapterIngredient(
                                    IngredientsActivity.this,
                                    ingredients
                            );
                    listEntriesView.setAdapter(adapter);
                }
            });
        }
    }
}
