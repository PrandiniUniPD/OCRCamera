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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import unipd.se18.barcodemodule.Barcode;
import unipd.se18.barcodemodule.BarcodeRecognizer;
import unipd.se18.eanresolvemodule.EAN;
import unipd.se18.eanresolvemodule.EANResolve;

import static unipd.se18.barcodemodule.BarcodeRecognizer.barcodeRecognizer;
import static unipd.se18.eanresolvemodule.EANResolve.eanResolve;

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
        String result = prefs.getString("result", null);

        lastPhoto = BitmapFactory.decodeFile(pathImage);

        if (lastPhoto != null) {
            mImageView.setImageBitmap(Bitmap.createScaledBitmap(lastPhoto, lastPhoto.getWidth(), lastPhoto.getHeight(), false));
        } else {
            Log.e("ResultActivity", "error retrieving last photo");
        }

        AsyncLoad ocrTask = new AsyncLoad(mOCRTextView, getString(R.string.processing));
        ocrTask.execute(lastPhoto);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Execute a task and post the result on the TextView given on construction
     * (g3) - modified by Rossi Leonardo - modified by Andrea Ton (barcode)
     * -modified by Elia Bedin (barcode to product name)
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncLoad extends AsyncTask<Bitmap, Void, String> {

        private ProgressDialog progressDialog;
        private TextView resultTextView;
        private String progressMessage;

        AsyncLoad(TextView view, String progressMessage) {
            this.resultTextView = view;
            this.progressMessage = progressMessage;
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            TextExtractor ocr = new TextExtractor();
            String textRecognized = "";
            String barcodeRecognized = "";
            String eanRecognized = "";

            if(lastPhoto != null) {
                //invoke a barcodeRecognizer choosing the api type
                Barcode barcodeRecognizer = barcodeRecognizer(BarcodeRecognizer.API.mlkit);
                //get the String of the barcode from the bitmap image
                barcodeRecognized = barcodeRecognizer.decodeBarcode(lastPhoto);
                //check if any barcode is found
                if(barcodeRecognized.equals(""))
                {
                    //if no barcode was found than try to get the text ocr
                    textRecognized = ocr.getTextFromImg(lastPhoto);

                    if(textRecognized.equals(""))
                    {
                    //if nothing was found than return a No text found message
                    textRecognized = getString(R.string.no_text_found);
                    final String finalTextRecognized = textRecognized;

                    runOnUiThread(() -> {
                            mOCRTextView.setText(finalTextRecognized);
                        });
                    }
                    else {
                        //if text is found, return it
                        final String finalTextRecognized = textRecognized;

                        runOnUiThread(() -> {
                            mOCRTextView.setText(finalTextRecognized);
                        });
                    }
                }
                else {

                    //invoke a EANResolve choosing the API type
                    EAN eanResolve = eanResolve(EANResolve.API.mignify);

                    eanRecognized = eanResolve.decodeEAN(barcodeRecognized);

                    //if barcode id found return the associated number, as a String,
                    //with a fixed "Barcode: " string to check the result is not from the ocr
                    //and "Product: " + product name
                    final String finalTextRecognized =
                            "Barcode: " + barcodeRecognized + "\nProduct: " + eanRecognized;

                    runOnUiThread(() -> {
                        mOCRTextView.setText(finalTextRecognized);
                    });
                }
            }
            else {
                Log.e("NOT_FOUND", "photo not found");
            }
            //using two different variables and one more if condition to avoid string conflict
            if(barcodeRecognized.equals("")) {
                return textRecognized;
            }
            else {
                return barcodeRecognized;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            // Saving in the preferences
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("result", s);
            editor.apply();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ResultActivity.this,
                    progressMessage,
                    "");
        }
    }
}

