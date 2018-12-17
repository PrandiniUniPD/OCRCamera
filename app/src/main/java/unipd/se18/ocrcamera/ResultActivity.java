package unipd.se18.ocrcamera;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
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
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * Class used for showing the result of the OCR processing
 */
public class ResultActivity extends AppCompatActivity {

    private static String TAG = "ResultActivity";

    /**
     * The TextView of the extracted test from the captured photo.
     */
    private TextView mOCRTextView;

    /**
     * Bitmap of the lastPhoto saved
     */
    private Bitmap lastPhoto;

    CropImageView mCropImageView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // UI components
        mOCRTextView = findViewById(R.id.ocr_text_view);
        mOCRTextView.setMovementMethod(new ScrollingMovementMethod());

        mCropImageView = findViewById(R.id.cropImageView);
        mCropImageView.setMultiTouchEnabled(true);

        //OnCropOverlayReleased will be called when the user release the finger from the mCropImageView
        //on any finger release i run again the OCR
        mCropImageView.setOnSetCropOverlayReleasedListener(new CropImageView.OnSetCropOverlayReleasedListener() {
            @Override
            public void onCropOverlayReleased(Rect rect) {
                Bitmap croppedBitmap = mCropImageView.getCroppedImage();
                //get text from OCR
                AsyncOCRExecute asyncOCRExecute = new AsyncOCRExecute();
                asyncOCRExecute.execute(croppedBitmap);
            }
        });



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

            mCropImageView.setImageBitmap(lastPhoto);

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

        AsyncLoad(TextView view, String progressMessage) {
            this.resultTextView = view;
            this.progressMessage = progressMessage;
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            TextExtractor ocr = new TextExtractor();
            String textRecognized = "";
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
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ResultActivity.this,
                    progressMessage,
                    "");
        }
    }

    /**
     * Class used to run async OCR and update the UI
     * @author Luca Moroldo
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncOCRExecute extends AsyncTask<Bitmap, Void, String> {

        private String progressDialogMessage;
        private String progressDialogTitle;
        private boolean isProgressDialogVisible;
        private ProgressDialog progressDialog;
        public AsyncOCRExecute() {
            super();
        }
        public AsyncOCRExecute(String progressDialogTitle, String progressDialogMessage) {
            this.progressDialogTitle = progressDialogTitle;
            this.progressDialogMessage = progressDialogMessage;
        }

        public void setProgressBarVisibility(boolean visibility) {
            if(progressDialogMessage == null)
                isProgressDialogVisible = false;
            else
                isProgressDialogVisible = visibility;
        }

        public void setProgressBarMessage(String message) {
            this.progressDialogMessage = message;
        }
        public void setProgressBarTitle(String title) {
            this.progressDialogTitle = title;
        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mOCRTextView.setText("Calculating..");
                }
            });
            if(isProgressDialogVisible) {
                progressDialog = ProgressDialog.show(
                        getApplicationContext(),
                        progressDialogTitle,
                        progressDialogMessage
                );
            }

        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            TextExtractor ocr = new TextExtractor();
            return ocr.getTextFromImg(bitmaps[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            final String finalText;
            if(s.equals("")) {
                finalText = getString(R.string.no_text_found);
            } else {
                finalText = s;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mOCRTextView.setText(finalText);
                }
            });

            if(isProgressDialogVisible) {
                progressDialog.dismiss();
            }


        }
    }
}

