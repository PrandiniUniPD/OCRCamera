package unipd.se18.ocrcamera;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class used for showing the result of the OCR processing
 */
public class ResultActivity extends AppCompatActivity {
    private String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;


    private Button takePictureButton;
    private TextView showTextView;
    private ImageView photoImageView;

    private static final String TAG = ResultActivity.class.getSimpleName();
    private static final String TESS_DATA = "/tessdata";

    TextExtractor tessOCR;
    private String tessDataPath;

    InternalStorageManager internalStorageBitmapManager;
    InternalStorageManager internalStorageTextManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        tessDataPath = prepareTessData(TESS_DATA);
        tessOCR = new TextExtractor(tessDataPath, TextExtractor.ITA);

        internalStorageBitmapManager = new InternalStorageManager(getApplicationContext(), "Photos",getString(R.string.photoFileName));

        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        showTextView = (TextView) findViewById(R.id.showTextView);
        takePictureButton = (Button) findViewById(R.id.takePictureButton);

        takePictureButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                dispatchTakePictureIntent();
            }
        });


        final Bitmap bitmap = internalStorageBitmapManager.loadBitmapFromInternalStorage();
        if(bitmap != null) {
            photoImageView.setImageBitmap(bitmap);

            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("manager", Context.MODE_PRIVATE);
            showTextView.setText(sharedPref.getString("recogText", ""));

            /*
            @SuppressLint("StaticFieldLeak") AsyncLoad loadTask = new AsyncLoad(showTextView, "Running OCR") {
                @Override
                protected String doInBackground(Bitmap... bitmaps) {
                    String recogText = "";

                    recogText = tessOCR.getTextWithConfidenceFromImg(bitmaps[0]);
                    return recogText;
                }
            };
            loadTask.execute(bitmap);
            */
        }
    }


    //Launch an intent to take a picture
    //Stores the image to external memory and save path in mCurrentPhotoPath
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getApplicationContext(), "Error creating file.", Toast.LENGTH_SHORT);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                photoFile.deleteOnExit();
            }
        }
    }

    /**
     *
     * @return an image file from external directory
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);


        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    //Show image on activity and save it in internal storage as bitmap, modifies photoPath in shared preference
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;

            //reduce quality for better performance
            options.inSampleSize = 2;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);

            //rotate image by 90 deg
            Matrix matrix = new Matrix();
            matrix.postRotate(90); // anti-clockwise by 90 degrees
            bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            photoImageView.setImageBitmap(bitmap);

            @SuppressLint("StaticFieldLeak") AsyncLoad loadTask = new AsyncLoad(showTextView, "Running OCR") {
                @Override
                protected String doInBackground(Bitmap... bitmaps) {

                    String recogText = "";
                    internalStorageBitmapManager.saveBitmapToInternalStorage(bitmaps[0]);

                    recogText = tessOCR.getTextWithConfidenceFromImg(bitmaps[0]);
                    return recogText;
                }
            };
            loadTask.execute(bitmap);

            /*
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 6;
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);

            //rotate image by 90 deg
            Matrix matrix = new Matrix();
            matrix.postRotate(90); // anti-clockwise by 90 degrees
            bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap .getWidth(), bitmap .getHeight(), matrix, true);

            photoImageView.setImageBitmap(bitmap);

            showTextView.setText(tessOCR.getTextWithConfidenceFromImg(bitmap));
            internalStorageBitmapManager.saveBitmapToInternalStorage(bitmap);
            */
        }

    }

    /**
     * @param dir where all assets are copied
     * load all files inside "assets" to external dir
     * @return path to tess data
     */
    private String prepareTessData(String dir) {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }

        //copy all files inside "assets" to external dir
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(getExternalFilesDir(dir), filename);
                out = new FileOutputStream(outFile);


                copyFile(in, out);


                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
        String path = getExternalFilesDir("/").getPath() + "/";
        return path;
    }

    /**
     *
     * @param in input
     * @param out output
     * @throws IOException
     *
     * Copy data from in to out
     */
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }


    /**Execute a task and post the result on the TextView given on construction
     *
     */
    private class AsyncLoad extends AsyncTask<Bitmap, Void, String> {

        private ProgressDialog progressDialog;
        private TextView resultTextView;
        private String progressMessage;

        public AsyncLoad(TextView view, String progressMessage) {
            this.resultTextView = view;
            this.progressMessage = progressMessage;
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            resultTextView.setText((s));


            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("manager", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("recogText", s);
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
