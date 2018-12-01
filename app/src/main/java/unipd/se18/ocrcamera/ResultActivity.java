package unipd.se18.ocrcamera;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


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

    /**
     * TextView of the ingredients
     */
    private TextView tViewInci;

    /**
     * Gallery's request code
     */
    private static final int PICK_IMG=100;

    ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //Load the openCV library
        ImageProcessing analyzeImage = new ImageProcessing();

        // UI components
        mImageView = findViewById(R.id.img_captured_view);
        mOCRTextView = findViewById(R.id.ocr_text_view);
        mOCRTextView.setMovementMethod(new ScrollingMovementMethod());

        tViewInci = findViewById(R.id.textViewInci);
        tViewInci.setMovementMethod(new ScrollingMovementMethod());

        FloatingActionButton fab = findViewById(R.id.newPictureFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultActivity.this, CameraActivity.class));
            }
        });

        FloatingActionButton gal = findViewById(R.id.newGalleryFloat);
        gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        //Get image path and text of the last image from preferences
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String pathImage = prefs.getString("imagePath", null);
        String OCRText = prefs.getString("text", null);

        //double angle = -analyzeImage.computeSkew(pathImage);

        //lastPhoto = rotateBitmap(BitmapFactory.decodeFile(pathImage), (float)angle);
        try{
            lastPhoto = analyzeImage.findText(pathImage);
        } catch (FileNotFoundException e) {}


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
                tViewInci.setText(Html.fromHtml("Ingredienti trovati: "+inciDetectorEtichetta(OCRText)+""));
            }
        } else{
            // text from OCR
            AsyncLoad ocrTask = new AsyncLoad(mOCRTextView,getString(R.string.processing));
            ocrTask.execute(lastPhoto);
        }
        Log.d("gallery", lastPhoto.toString());
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
     * @author Francesco Pham
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.test:
                Intent i = new Intent(ResultActivity.this, TestResultActivity.class);
                startActivity(i);
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
                            tViewInci.setText(Html.fromHtml("Ingredienti trovati: "+inciDetectorEtichetta(finalTextRecognized)+""));
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
                            tViewInci.setText(Html.fromHtml("Ingredienti trovati: "+inciDetectorEtichetta(finalTextRecognized)+""));
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


    public Bitmap rotateBitmap(Bitmap original, float degrees) {
        int width = original.getWidth();
        int height = original.getHeight();

        Matrix matrix = new Matrix();
        matrix.preRotate(degrees);

        Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, width, height, matrix, true);
        Bitmap mutableBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(original, 5.0f, 0.0f, null);

        return rotatedBitmap;
    }

    /**
     * @author Giovanni Fasan(g1), Giovanni Piva(g1)
     * @param text String in which you have to find the ingredients
     * @return String with the list of ingredients found in the text
     * Put in database csv: https://www.youtube.com/watch?v=i-TqNzUryn8
     */
    public String inciDetector(String text){
        String inci = "";
        try {
            InputStream is = getResources().openRawResource(R.raw.database);    //InputStream from database.csv
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));    //database reader
            String ingredient;  //String of inci ingredient
            while ((ingredient=reader.readLine())!= null){      //defines ingredient as each row of the database
                if (text.toUpperCase().contains(ingredient)) {      //check if no text found are inci ingredients
                    inci = inci + "<b>" + ingredient + "</b>; ";    //Create the string with the ingredients in bold
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return inci;
    }

    /**
     * @author Giovanni Fasan(g1), Giovanni Piva(g1)
     * @param text String in which you have to find the ingredients
     * @return String with ingredients list of the label
     * Put in database csv: https://www.youtube.com/watch?v=i-TqNzUryn8
     * problem contains: from the string "black" find the ingredient "LAC"
     * the problem is solved by a division of the string into characters to which I am still workings
     */
    public String inciDetectorEtichetta(String text){
        String inci = "";
        List listInci = new ArrayList();        //list where save the ingredients found

        try {
            InputStream is = getResources().openRawResource(R.raw.database);    //InputStream from database.csv
            is.mark(0);     //mark the position to restart the buffer from line 0 of database
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));    //database reader
            String ingredient;      //String of inci ingredient
            String noSpace = text.replaceAll("\n", " ");    //remove \n (start a new line) and replace with " ".
            String[] tokens = noSpace.split(",");   //split the text by "," for find the inci ingredients
            String box="";

            for (int i=0; i<tokens.length; i++) {
                while ((ingredient=reader.readLine())!=null){       //defines ingredient as each row of the database
                    if (tokens[i].toUpperCase().contains(ingredient)) {         //Use .toUpperCase because the database's words are uppercase. .contains find if tokens[i] is an ingredient
                        box=ingredient;     //save the ingredient in a box. after while i use the last ingredient that I found
                    }
                }
                is.reset();     //reset InputStream to the mark 0
                if (!listInci.contains(box)) {
                    listInci.add(box);          //check if there are duplicates
                }
            }

            if (listInci.size()>1) {
                for (int i = 0; i < listInci.size(); i++) {
                    inci = inci + "<b>" + listInci.get(i) + "</b>; ";           //Create the string with the ingredients in bold
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        return inci;
    }


    /**
     * @autor Giovanni Fasan(g1)
     * Function that opens a new activity
     * https://developer.android.com/reference/android/provider/MediaStore.Images.Media
     */
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);      //open device gallery
        startActivityForResult(gallery, PICK_IMG);  //start gallery's intent
    }

    /**
     * @autor Giovanni Fasan(g1)
     * @param requestCode integer code
     * @param resultCode integer code
     * @param data  Intent variable
     * onActivityResult start another activity and receive a result back
     * Save the data in a Uri variable. Convert the Uri variable to bitmap to be able to execute the ocr. Set the imageview and execute the ocr.
     * https://developer.android.com/reference/android/provider/MediaStore.Images.Media
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK && requestCode==PICK_IMG){
            Uri imageUri;
            imageUri = data.getData();
            try {
                lastPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                mImageView = findViewById(R.id.img_captured_view);
                mImageView.setImageBitmap(Bitmap.createScaledBitmap(lastPhoto, lastPhoto.getWidth(), lastPhoto.getHeight(), false));
                AsyncLoad ocrTask = new AsyncLoad(mOCRTextView,getString(R.string.processing));
                ocrTask.execute(lastPhoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

