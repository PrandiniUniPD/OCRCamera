package unipd.se18.ocrcamera;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TimingLogger;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

/**
 * Class used for showing the result of the OCR processing
 */
public class ResultActivity extends AppCompatActivity {

    //ListView of extracted ingredients
    private ListView ingredientsListView;

    private final String TAG = "ResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // UI components
        ImageView mImageView = findViewById(R.id.img_captured_view);
        ingredientsListView = findViewById(R.id.ingredients_list);

        //set on empty list view
        TextView emptyView = findViewById(R.id.empty_list);
        ingredientsListView.setEmptyView(emptyView);

        // Floating action buttons listeners (Francesco Pham)
        FloatingActionButton fabNewPic = findViewById(R.id.newPictureFab);
        fabNewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultActivity.this, CameraActivity.class));
            }
        });


        //Get image path and text of the last image from preferences
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String pathImage = prefs.getString("imagePath", null);
        String OCRText = prefs.getString("text", null);

        //Bitmap of the lastPhoto saved
        Bitmap lastPhoto = BitmapFactory.decodeFile(pathImage);

        if (lastPhoto != null) {
            mImageView.setImageBitmap(Bitmap.createScaledBitmap(lastPhoto, lastPhoto.getWidth(), lastPhoto.getHeight(), false));
        } else {
            Log.e("ResultActivity", "error retrieving last photo");
        }

        //Displaying the text, from OCR or preferences
        if(OCRText != null) {
            // Text in preferences
            if(!OCRText.equals("")) {
                ProgressDialog progressDialog = ProgressDialog.show(ResultActivity.this,
                        getString(R.string.processing),
                        getString(R.string.processing_ingredients));
                List<Ingredient> ingredients = extractIngredients(OCRText);
                showIngredients(ingredients);
                progressDialog.dismiss();
            }
        } else{
            // text from OCR
            executionThread inciThread = new executionThread(lastPhoto, ingredientsListView);
            inciThread.start();
        }
    }

    /**
     * Menu inflater
     * @author Francesco Pham
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.result_menu, menu);
        return true;
    }

    /**
     * Handling click events on the menu
     * @author Francesco Pham - modified by Stefano Romanello
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.test: {
                Intent i = new Intent(ResultActivity.this, TestResultActivity.class);
                startActivity(i);
                return true;
            }
            case R.id.download_photos:
                Intent download_intent = new Intent(ResultActivity.this, DownloadDbActivity.class);
                startActivity(download_intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Thread that executes ocr, extract ingredients and show results
     * @author Francesco Pham
     */
    private class executionThread extends Thread{
        private Bitmap photo;
        private ProgressDialog progressDialog;
        ListView listView;

        executionThread(Bitmap photo, ListView listView){
            this.photo = photo;
            this.listView = listView;
            progressDialog = ProgressDialog.show(ResultActivity.this,
                    getString(R.string.processing),
                    getString(R.string.processing_ocr));
        }

        public void run(){
            if(photo != null) {
                //execute ocr
                TextExtractor ocr = new TextExtractor();
                String textRecognized = ocr.getTextFromImg(photo);

                //extract ingredients from ocr text
                progressDialog.setMessage(getString(R.string.processing_ingredients));
                if(!textRecognized.equals("")){
                    List<Ingredient> ingredients = extractIngredients(textRecognized);
                    showIngredients(ingredients);
                }

                // Saving ocr text in the preferences
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("text", textRecognized);
                editor.apply();
            }
            else{
                Log.e(TAG, "photo not found");
            }

            progressDialog.dismiss();
        }


    }

    /**
     * ingredients extraction from the ocr text
     * @param ocrText The ocr text
     * @return List of Ingredient objects containing associated information
     * @author Francesco Pham
     */
    private List<Ingredient> extractIngredients(String ocrText){
        TimingLogger timings = new TimingLogger(TAG, "inci execution");

        //load inci db and initialize ingredient extractor
        List<Ingredient> listInciIngredients = Inci.getListIngredients(getApplicationContext());
        InputStream wordListStream = ResultActivity.this.getResources().openRawResource(R.raw.inciwordlist);
        TextAutoCorrection textCorrector = new TextAutoCorrection(wordListStream, 0.2);
        IngredientsExtractor ingredientsExtractor = new PrecorrectionIngredientsExtractor(listInciIngredients, textCorrector);

        timings.addSplit("load db");

        //find ingredients in inci db
        final List<Ingredient> ingredients = ingredientsExtractor.findListIngredients(ocrText);

        timings.addSplit("search in db");
        timings.dumpToLog();

        return ingredients;
    }

    /**
     * Show results on a list view
     * @param ingredients Ingredients to be displayed
     * @author Francesco Pham
     */
    private void showIngredients(final List<Ingredient> ingredients){
        //show results using adapter
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AdapterIngredient adapter =
                        new AdapterIngredient(
                                ResultActivity.this,
                                ingredients
                        );
                ingredientsListView.setAdapter(adapter);
            }
        });
    }
}

