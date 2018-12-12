package unipd.se18.ocrcamera;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TimingLogger;
import android.widget.ListView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Activity that shows informations taken from inci db about the ingredients found in the ocr text
 * @author Francesco Pham
 */
public class IngredientsActivity extends AppCompatActivity {
    private final String TAG = "IngredientsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        //get ocr text
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String OCRText = prefs.getString("text", null);

        if(OCRText != null) {
            InciThread inciThread = new InciThread(OCRText);
            inciThread.start();
        }
    }

    class InciThread extends Thread{
        private String ocrText;
        private ProgressDialog progressDialog;

        public InciThread(String ocrText){
            this.ocrText = ocrText;
            progressDialog = ProgressDialog.show(IngredientsActivity.this, getString(R.string.processing), "");
        }

        public void run(){
            TimingLogger timings = new TimingLogger(TAG, "inci execution");

            //load inci db
            InputStream inputStream = getResources().openRawResource(R.raw.incidb);
            Inci inci = new Inci(getApplicationContext(), inputStream);

            timings.addSplit("load db");

            //find ingredients in inci db
            final ArrayList<Ingredient> ingredients = inci.findListIngredients(ocrText, Inci.Algorithm.TEXT_PRECORRECTION);

            timings.addSplit("search in db");

            //sort list of ingredients by similarity
            Collections.sort(ingredients, new Comparator<Ingredient>() {
                public int compare(Ingredient one, Ingredient other) {
                    if (one.getOcrTextSimilarity() >= other.getOcrTextSimilarity()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });

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

            timings.dumpToLog();
            progressDialog.dismiss();
        }
    }
}
