package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * Class used for showing the result of the OCR processing
 * @author modified by Francesco Pham
 */
public class ResultActivity extends AppCompatActivity {

    // UI components
    private ListView ingredientsListView;
    private ProgressBar progressBar;

    private final String TAG = "ResultActivity";

    private String OCRText;

    private CountDownLatch latch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // UI components
        ImageView mImageView = findViewById(R.id.img_captured_view);
        ingredientsListView = findViewById(R.id.ingredients_list);
        progressBar = findViewById(R.id.progress_bar);

        //set on empty list view
        View emptyListView = findViewById(R.id.empty_list);
        ingredientsListView.setEmptyView(emptyListView);

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
        OCRText = prefs.getString("text", null);

        //Bitmap of the lastPhoto saved
        Bitmap lastPhoto = BitmapFactory.decodeFile(pathImage);

        if (lastPhoto != null) {

            latch = new CountDownLatch(1);

            //extract ingredients
            IngredientsExtractionThread ingredientsExtractionThread = new IngredientsExtractionThread();
            ingredientsExtractionThread.start();

            if(OCRText == null) {
                //text from ocr
                OcrThread ocrThread = new OcrThread(lastPhoto);
                ocrThread.start();
            }

            latch.countDown(); //signal ingredientsExtractionThread to continue with extraction

            mImageView.setImageBitmap(Bitmap.createScaledBitmap(lastPhoto, lastPhoto.getWidth(), lastPhoto.getHeight(), false));
        }
        else
            Log.e("ResultActivity", "error retrieving last photo");

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
     * Thread for ocr text extraction and saving into preferences
     * @author modified by Francesco Pham
     */
    private class OcrThread extends Thread{
        private Bitmap photo;

        OcrThread(Bitmap photo){
            this.photo = photo;
        }

        public void run(){
            // text from OCR
            TextExtractor ocr = new TextExtractor();
            OCRText = ocr.getTextFromImg(photo);

            // Saving ocr text in the preferences
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("text", OCRText);
            editor.apply();
        }
    }

    /**
     * Thread for loading of inci db, extractor inizialization,
     * ingredients extraction from ocr text, and display into list view.
     * @author Francesco Pham
     */
    private class IngredientsExtractionThread extends Thread{
        public void run(){
            //load inci db and initialize ingredient extractor
            InputStream inciDbStream = ResultActivity.this.getResources().openRawResource(R.raw.incidb);
            List<Ingredient> listInciIngredients = Inci.getListIngredients(inciDbStream);

            progressBar.incrementProgressBy(20);

            InputStream wordListStream = ResultActivity.this.getResources().openRawResource(R.raw.inciwordlist);
            TextAutoCorrection textCorrector = new TextAutoCorrection(wordListStream);

            progressBar.incrementProgressBy(20);

            IngredientsExtractor ingredientsExtractor = new PrecorrectionIngredientsExtractor(listInciIngredients, textCorrector);

            progressBar.incrementProgressBy(20);

            //wait for ocr to finish
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            progressBar.incrementProgressBy(20);

            //extract ingredients from ocr text
            if(OCRText!=null && !OCRText.equals("")) {
                List<Ingredient> ingredients = ingredientsExtractor.findListIngredients(OCRText);
                if(ingredients.size() != 0) showIngredients(ingredients);
            }

            progressBar.incrementProgressBy(20);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * Show results on a list view
     * @param ingredients Ingredients to be displayed
     * @author Francesco Pham
     */
    private void showIngredients(final List<Ingredient> ingredients) {
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

