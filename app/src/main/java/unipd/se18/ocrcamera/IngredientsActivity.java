package unipd.se18.ocrcamera;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TimingLogger;
import android.widget.ListView;

import java.util.List;

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
            progressDialog = ProgressDialog.show(IngredientsActivity.this, getString(R.string.processing), getString(R.string.processing_ingredients));
        }

        public void run(){
            TimingLogger timings = new TimingLogger(TAG, "inci execution");

            //load inci db and initialize ingredient extractor
            List<Ingredient> listInciIngredients = Inci.getListIngredients(getApplicationContext());
            TextAutoCorrection textCorrector = new TextAutoCorrection(getApplicationContext());
            IngredientsExtractor ingredientsExtractor = new PrecorrectionIngredientsExtractor(listInciIngredients, textCorrector);

            timings.addSplit("load db");

            //find ingredients in inci db
            final List<Ingredient> ingredients = ingredientsExtractor.findListIngredients(ocrText);

            timings.addSplit("search in db");

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
