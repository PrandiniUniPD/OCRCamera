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
import unipd.se18.barcodemodule.BarcodeListener;
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
    static Bitmap lastPhoto;
    private Bitmap originalPhoto;

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

        //retrieve the original not cropped image to recrop if needed
        //SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        //String pathName = prefs.getString("imagePath", null);
        //originalPhoto = BitmapFactory.decodeFile(pathName);

        if (lastPhoto != null) {
            mImageView.setImageBitmap(Bitmap.createScaledBitmap(lastPhoto, lastPhoto.getWidth(), lastPhoto.getHeight(), false));
        } else {
            Log.e("ResultActivity", "error retrieving last photo");
        }

        AsyncLoad ocrTask = new AsyncLoad();
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
     * analyze the photo and get the barcode
     * @param photo is the last photo taken
     * @modify mOCRTextView set with the code retrieved
     * @author Andrea Ton
     */
    private void getBarcode(Bitmap photo){

        //Barcode listener
        BarcodeListener barcodeListener = new BarcodeListener() {
            @Override
            public void onBarcodeRecognized(String barcode) {
                mOCRTextView.setText(getProductInfo(barcode));
            }

            @Override
            public void onBarcodeRecognizedError(int code) {
                switch(code){
                    //TODO manage error cases
                    case BARCODE_NOT_FOUND:
                        Log.e("Barcode not found", "No barcode was found");
                    case DECODING_ERROR:
                        Log.e("Decoding error", "Error decoding image");
                    case BITMAP_NOT_FOUND:
                        Log.e("Bitmap not found", "Bitmap not found");

                }
            }
        };

        Barcode barcodeRecognizer = barcodeRecognizer(BarcodeRecognizer.API.mlkit, barcodeListener);
        barcodeRecognizer.decodeBarcode(photo);
    }

    /**
     * Retrieve the product name and brand from online database
     * @param barcode is the barcode to be searched
     * @return product information retrieved from the database
     * @author Andrea Ton
     */
    private String getProductInfo(String barcode){
        EAN eanResolve = eanResolve(EANResolve.API.MIGNIFY);
        return eanResolve.decodeEAN(barcode);
    }

    /**
     * let the user recrop the original photo
     * @param view button to start the crop activity
     */
    public void recrop(View view){
        finish();
    }

    /**
     * Execute a task and post the result on the TextView (mOCRTextView)
     * @author Andrea Ton
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncLoad extends AsyncTask<Bitmap, Void, String> {

        private ProgressDialog progressDialog;

        //in background execute the code retrieving method
        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            getBarcode(lastPhoto);
            return null;
        }

        //after the execution dismiss the progress message
        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
        }

        //shows a progress message while the task is executed
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ResultActivity.this, getString(R.string.processing), "");
        }
    }
}

