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
import android.widget.Toast;

import unipd.se18.barcodemodule.Barcode;
import unipd.se18.barcodemodule.BarcodeListener;
import unipd.se18.barcodemodule.BarcodeRecognizer;
import unipd.se18.barcodemodule.ErrorCode;
import unipd.se18.eanresolvemodule.EAN;
import unipd.se18.eanresolvemodule.EANResolve;
import unipd.se18.eanresolvemodule.EANResolveListener;
//import unipd.se18.eanresolvemodule.ErrorCode;

import static unipd.se18.barcodemodule.BarcodeRecognizer.barcodeRecognizer;
import static unipd.se18.eanresolvemodule.EANResolve.eanResolve;

/**
 * Class used for showing the result of the barcode processing
 */
public class ResultActivity extends AppCompatActivity
        implements BarcodeListener, EANResolveListener {

    /**
     * The TextView of the extracted test from the captured photo.
     */
    private TextView mOCRTextView;

    /**
     * Bitmap of the lastPhoto saved
     */
    private Bitmap lastPhoto;

    /**
     * Barcode listener implementation
     */
    @Override
    public void onBarcodeRecognized(String barcode) {
        getProductInfo(barcode);
        //mOCRTextView.setText(barcode);
    }

    @Override
    public void onBarcodeRecognizedError(ErrorCode error) {
        Log.e("BarcodeRecognizedError", error.toString());
        Toast.makeText(ResultActivity.this, error.getInfo(), Toast.LENGTH_SHORT).show();
    }

    /**
     * EANResolve listener implementation
     */
    @Override
    public void onProductFound(String product) {
        mOCRTextView.setText(product);
    }

    @Override
    public void onResolveError(unipd.se18.eanresolvemodule.ErrorCode error) {
        Log.e("BarcodeRecognizedError", error.toString());
        Toast.makeText(ResultActivity.this, error.getInfo(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Barcode object used to decode barcode
     */
    private Barcode barcodeRecognizer;

    /**
     * EAN object used to resolve barcode
     */
    private EAN eanResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // UI components
        ImageView mImageView = findViewById(R.id.img_captured_view);
        mOCRTextView = findViewById(R.id.ocr_text_view);
        mOCRTextView.setMovementMethod(new ScrollingMovementMethod());

        try {
            barcodeRecognizer = barcodeRecognizer(BarcodeRecognizer.API.mlkit, this);
        } catch(IllegalArgumentException e) {
            Log.e("Barcode object", e.getMessage());
        }

        try {
            eanResolver = eanResolve(EANResolve.API.MIGNIFY, this);
        } catch(IllegalArgumentException e) {
            Log.e("Barcode object", e.getMessage());
        }

        FloatingActionButton fab = findViewById(R.id.newPictureFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultActivity.this, CameraActivity.class));
            }
        });

        //Get the path of the last image from preferences
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String pathImage = prefs.getString("imagePath", null);

        lastPhoto = BitmapFactory.decodeFile(pathImage);

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
        try{
            barcodeRecognizer.decodeBarcode(photo);
        }catch(IllegalArgumentException e){
            Log.e("barcode error", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Retrieve the product name and brand from online database
     * @param barcode is the barcode to be searched
     * @author Andrea Ton, modified by Elia Bedin
     */
    private void getProductInfo(String barcode){
        try{
            eanResolver.decodeEAN(barcode);
        }catch(IllegalArgumentException e){
            Log.e("EanResolve error", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * let the user recrop the original photo
     * @param view button to start the crop activity
     * @author Andrea Ton
     */
    public void reCrop(View view){
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



