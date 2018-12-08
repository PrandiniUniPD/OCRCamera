package unipd.se18.ocrcamera;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Activity that shows informations taken from inci db about the ingredients found in the ocr text
 * @author Francesco Pham
 */
public class IngredientsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        //get ocr text
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String OCRText = prefs.getString("text", null);

        if(OCRText != null) {

            //NB: THESE METHODS CAN TAKE A LONG EXECUTION TIME AND SHOULD BE MOVED INTO A SEPARATE THREAD
            //load inci db
            InputStream inputStream = getResources().openRawResource(R.raw.incidb);
            Inci inci = new Inci(inputStream);

            //find ingredients in inci db
            final ArrayList<Ingredient> ingredients = inci.findListIngredients(OCRText);

            //show results using adapter
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
