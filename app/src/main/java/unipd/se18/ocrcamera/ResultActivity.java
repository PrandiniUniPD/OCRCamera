package unipd.se18.ocrcamera;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class used for showing the result of the OCR processing
 */
public class ResultActivity extends AppCompatActivity {

    /**
     * The TextView of the extracted test from the captured photo.
     */
    private TextView mOCRTextView;

    /**
     * Bitmap of the lastPhoto saved
     */
    private Bitmap lastPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // UI components
        ImageView mImageView = findViewById(R.id.img_captured_view);
        mOCRTextView = findViewById(R.id.ocr_text_view);
        mOCRTextView.setMovementMethod(new ScrollingMovementMethod());

        FloatingActionButton fab = findViewById(R.id.newPictureFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultActivity.this, CameraActivity.class));
            }
        });


        //Get image path and text of the last image from preferences
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String pathImage = prefs.getString("imagePath", null);
        String OCRText = prefs.getString("text", null);

        lastPhoto = BitmapFactory.decodeFile(pathImage);

        if (lastPhoto != null) {
            mImageView.setImageBitmap(Bitmap.createScaledBitmap(lastPhoto, lastPhoto.getWidth(), lastPhoto.getHeight(), false));
        } else {
            Log.e("ResultActivity", "error retrieving last photo");
        }

        //Displaying the text, from OCR or preferences
        if(OCRText != null) {
            // Text in preferences
            if(OCRText.equals("")) {
                mOCRTextView.setText(R.string.no_text_found);
            } else {
                //Show the text of the last image
                mOCRTextView.setText(OCRText);
            }
        } else{
            // text from OCR
            AsyncLoad ocrTask = new AsyncLoad(mOCRTextView,getString(R.string.processing));
            ocrTask.execute(lastPhoto);
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
            case R.id.test:
                Intent i = new Intent(ResultActivity.this, TestResultActivity.class);
                startActivity(i);
                return true;
            case R.id.download_photos:
                Intent download_intent = new Intent(ResultActivity.this, DownloadDbActivity.class);
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
     * Execute a task and post the result on the TextView given on construction
     * (g3) - modified by Rossi Leonardo
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncLoad extends AsyncTask<Bitmap, Void, String> {

        private ProgressDialog progressDialog;
        private TextView resultTextView;
        private String progressMessage;
        private Bitmap workingImage;

        AsyncLoad(TextView view, String progressMessage) {
            this.resultTextView = view;
            this.progressMessage = progressMessage;
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            TextExtractor ocr = new TextExtractor();
            String textRecognized = "";

            //Im passing only one image to the async and is the image that i want to work with. No need for a dedicate variable
            workingImage=bitmaps[0];

            if(lastPhoto != null) {
                textRecognized = ocr.getTextFromImg(lastPhoto);

                if(textRecognized.equals(""))
                {
                    textRecognized = getString(R.string.no_text_found);
                    final String finalTextRecognized = textRecognized;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mOCRTextView.setText(finalTextRecognized);
                        }
                    });
                }
                else
                {
                    final String finalTextRecognized = textRecognized;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mOCRTextView.setText(finalTextRecognized);
                        }
                    });
                }
            } else {
                Log.e("NOT_FOUND", "photo not found");
            }
            return textRecognized;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            // Saving in the preferences
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("text", s);
            editor.apply();

            //I cant understand the ingredients yet, for now I put everything as one ingredient
            ArrayList<String> txt = new ArrayList<>();
            String testoFormattato=String.valueOf(Html.fromHtml(s));
            txt.add(testoFormattato);
            try {
                GalleryManager.storeImage(getBaseContext(),workingImage,txt,"0%");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ResultActivity.this,
                    progressMessage,
                    "");
        }
    }
}

