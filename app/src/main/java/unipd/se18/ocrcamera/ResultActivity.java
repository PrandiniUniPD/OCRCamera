package unipd.se18.ocrcamera;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import unipd.se18.ocrcamera.inci.Ingredient;
import unipd.se18.ocrcamera.inci.IngredientsExtractor;
import unipd.se18.textrecognizer.OCR;
import unipd.se18.textrecognizer.OCRListener;
import unipd.se18.textrecognizer.TextRecognizer;

import static unipd.se18.textrecognizer.TextRecognizer.getTextRecognizer;



/**
 * Class used for showing the result of the OCR processing
 * @author Pietro Prandini (g2) - Francesco Pham (g3) - modified by Luca Moroldo (g3)
 */
public class ResultActivity extends AppCompatActivity {

    private final String TAG = "ResultActivity";

    /**
     * listview used to show the ingredients extracted according with the INCI database
     */
    private ListView ingredientsListView;

    /**
     * view used to show progress messages
     */
    private TextView emptyTextView;

    /**
     * progress bar used to show the progress on the ingredients extraction from the photo taken
     */
    private ProgressBar progressBar;

    /**
     * Contains the last photo taken by the user
     */
    private Bitmap lastPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        // UI components
        ImageView mImageView = findViewById(R.id.img_captured_view);
        ingredientsListView = findViewById(R.id.ingredients_list);
        progressBar = findViewById(R.id.progress_bar);

        //set on empty list view
        emptyTextView= findViewById(R.id.empty_list);
        emptyTextView.setText(R.string.finding_text);
        ingredientsListView.setEmptyView(emptyTextView);

        // Floating action buttons listeners (Francesco Pham)
        FloatingActionButton fabNewPic = findViewById(R.id.newPictureFab);
        fabNewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultActivity.this, CameraActivity.class));
            }
        });

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        //load the path to the last taken picture, can be null if the user didn't take any picture
        String lastImagePath = prefs.getString("imagePath", null);

        //only if lastImagePath is not null we set our view
        if(lastImagePath != null) {

            // Bitmap of the lastPhoto saved
            lastPhoto = BitmapFactory.decodeFile(lastImagePath);

            // Sets the image to the view
            mImageView.setImageBitmap(
                    // Scales the image firstly
                    Bitmap.createScaledBitmap(
                            lastPhoto,
                            lastPhoto.getWidth(),
                            lastPhoto.getHeight(),
                            false
                    )
            );

            //create a listener for the end of the text extraction by the OCR
            OCRListener textExtractionListener = new OCRListener() {
                //function called when the OCR extraction is finished
                @Override
                public void onTextRecognized(String text) {
                    //search for ingredients in the INCI db and update the UI
                    new AsyncIngredientsExtraction(ResultActivity.this).execute(text);

                    //save photo in the gallery and the last recognized text
                    saveTheResult(text);
                }

                @Override
                public void onTextRecognizedError(int code) {
                    /*
                     Text not correctly recognized
                     -> prints the error on the screen and doesn't save it in the preferences
                     */
                    String errorText = R.string.extraction_error
                            + " (" + R.string.error_code + code + ")";
                    Log.e(TAG, errorText);
                }
            };

            //get an OCR instance
            OCR textRecognizer = getTextRecognizer(TextRecognizer.Recognizer.mlKit,
                    textExtractionListener);

            //extract text
            textRecognizer.getTextFromImg(lastPhoto);
            progressBar.setVisibility(ProgressBar.VISIBLE);
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

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

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
            case R.id.gallery:
                Intent gallery_intent = new Intent(ResultActivity.this, GalleryActivity.class);
                startActivity(gallery_intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /**
     * Class used to run extract ingredients from INCI db and update the UI setting a list view with
     * the recognized ingredients list
     * @author Francesco Pham
     */
    private static class AsyncIngredientsExtraction extends AsyncTask<String, Void, List<Ingredient>> {

        private WeakReference<ResultActivity> activityReference;

        AsyncIngredientsExtraction(ResultActivity context){
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            //show progress bar
            ResultActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.progressBar.setVisibility(ProgressBar.VISIBLE);
            activity.emptyTextView.setText(R.string.searching_ingredients);
        }

        /**
         *
         * @param strings text scanned for ingredients
         * @return a list of ingredients, null if the list is empty or the param is null or empty
         */
        @Override
        protected List<Ingredient> doInBackground(String... strings) {

            String ocrText = strings[0];

            ResultActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return null;
            IngredientsExtractor extractor = IngredExtractorSingleton.getInstance(activity);

            //check if text is empty or null
            if(ocrText == null || ocrText.equals(""))
                return null;

            //extract ingredients
            List<Ingredient> ingredientList = extractor.findListIngredients(ocrText);

            //if the list is empty then return null
            if(ingredientList.size() == 0)
                return null;

            return ingredientList;
        }

        @Override
        protected void onPostExecute(List<Ingredient> ingredients) {

            ResultActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.progressBar.setVisibility(ProgressBar.INVISIBLE);

            //if something has been found then set the list of recognized ingredients
            if(ingredients != null) {
                AdapterIngredient adapter =
                        new AdapterIngredient(
                                activity,
                                ingredients
                        );
                activity.ingredientsListView.setAdapter(adapter);
            } else
                activity.emptyTextView.setText(R.string.no_ingredient_found);
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

        //I cant understand the ingredients yet, for now I put everything as one ingredient
        ArrayList<String> txt = new ArrayList<>();
        String formattedText=String.valueOf(Html.fromHtml(text));
        txt.add(formattedText);
        try {
            GalleryManager.storeImage(getBaseContext(),lastPhoto,txt,"0%");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

