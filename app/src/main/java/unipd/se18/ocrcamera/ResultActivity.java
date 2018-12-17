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
import android.util.TimingLogger;
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


import unipd.se18.ocrcamera.recognizer.OCR;
import unipd.se18.ocrcamera.recognizer.OCRListener;
import unipd.se18.ocrcamera.recognizer.TextRecognizer;

import static unipd.se18.ocrcamera.recognizer.TextRecognizer.getTextRecognizer;

/**
 * Class used for showing the result of the OCR processing
 * @author Pietro Prandini (g2) - Francesco Pham (g3)
 */
public class ResultActivity extends AppCompatActivity {

    // UI components
    private ListView ingredientsListView;
    private ProgressBar progressBar;

    private final String TAG = "ResultActivity";

    private String OCRText;

    /**
     * Useful for testing
     */
    private TimingLogger timings;

    private CountDownLatch latch;
    /**
     * Listener used by the extraction process to notify results
     */
    private OCRListener textExtractionListener = new OCRListener() {
        @Override
        public void onTextRecognized(String text) {
            OCRText = text;
            saveTheResult(text);
            latch.countDown(); //signal ingredientsExtractionThread to continue with extraction
        }

        @Override
        public void onTextRecognizedError(int code) {
            /*
             Text not correctly recognized
             -> prints the error on the screen and saves it in the preferences
             */
            String errorText = R.string.extraction_error
                    + " (" + R.string.error_code + code + ")";
            OCRText = "";
            saveTheResult(errorText);
            latch.countDown(); //signal ingredientsExtractionThread to continue with extraction
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        timings = new TimingLogger(TAG, "Ingredients extraction times"); //just for testing

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
                // Instance of an OCR recognizer
                OCR ocrProcess = getTextRecognizer(TextRecognizer.Recognizer.mlKit,
                        textExtractionListener);

                // Runs the operations of text extraction
                ocrProcess.getTextFromImg(lastPhoto);
            }
            else latch.countDown();

            mImageView.setImageBitmap(Bitmap.createScaledBitmap(lastPhoto, lastPhoto.getWidth(), lastPhoto.getHeight(), false));
        } else {
            Log.e("ResultActivity", "error retrieving last photo");
        }
    }

    /**
     * Saves the result obtained in the "prefs" preferences (Context.MODE_PRIVATE)
     * - the name of the String is "text"
     * @param text The text extracted by the process
     * @author Pietro Prandini (g2)
     */
    private void saveTheResult(String text) {
        // Saving in the preferences
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("prefs",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("text", text);
        editor.apply();
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
            case R.id.test:
                Intent i = new Intent(ResultActivity.this, TestsListActivity.class);
                startActivity(i);
                return true;
            case R.id.download_photos:
                Intent download_intent = new Intent(ResultActivity.this,
                        DownloadDbActivity.class);
                startActivity(download_intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

            timings.addSplit("load csv");
            progressBar.incrementProgressBy(20);

            InputStream wordListStream = ResultActivity.this.getResources().openRawResource(R.raw.inciwordlist);
            TextAutoCorrection textCorrector = new TextAutoCorrection(wordListStream);

            timings.addSplit("load text corrector");
            progressBar.incrementProgressBy(20);

            IngredientsExtractor ingredientsExtractor = new PrecorrectionIngredientsExtractor(listInciIngredients, textCorrector);

            timings.addSplit("load ingredients extractor");
            progressBar.incrementProgressBy(20);

            //wait for ocr to finish
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(OCRText!=null && !OCRText.equals("")) {
                extractIngredients(OCRText, ingredientsExtractor);
            } else {
                // "Nothing recognized" is set automatically to the user view
                Log.d(TAG,"Nothing recognized");
            }
        }
    }

    /**
     * Extract the ingredients from a text
     * @param OCRText The text to be analyzed
     * @param ingredientsExtractor The extractor object
     * @author Francesco Pham (g3)
     */
    private void extractIngredients(String OCRText, IngredientsExtractor ingredientsExtractor) {
        timings.addSplit("waiting for ocr to finish");
        progressBar.incrementProgressBy(20);

        //extract ingredients from ocr text
        List<Ingredient> ingredients = ingredientsExtractor.findListIngredients(OCRText);
        if(ingredients.size() != 0) showIngredients(ingredients);

        timings.addSplit("extract ingredients");
        progressBar.incrementProgressBy(20);

        timings.dumpToLog();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
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

