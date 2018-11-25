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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

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

        //Load the openCV library
        System.loadLibrary("opencv_java3");
        Log.i("openCV", "Loaded the library");


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

        double angle = -computeSkew(pathImage);

        lastPhoto = rotateBitmap(BitmapFactory.decodeFile(pathImage), (float)angle);

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


    public double computeSkew(String inFile) {
        //Load this image in grayscale
        Log.d("openCV", "Image path = "+inFile);
        Mat img = Imgcodecs.imread( inFile, Imgcodecs.IMREAD_GRAYSCALE );
        Size size = img.size();

        //Invert the colors (because objects are represented as white pixels, and the background is represented by black pixels)
        Core.bitwise_not( img, img );

        Imgproc.Canny(img, img, 50, 200, 3, false);

        //Create a 4 dimensions vector usign matrix
        MatOfInt4 lines = new MatOfInt4();

        //Process the image with the Probabilistic Hough Transform
        Imgproc.HoughLinesP(img, lines,  1, Math.PI/180, 50, 50, 10);

        boolean isEmpty=lines.empty();
        Log.d("openCV", "Lines empty = "+isEmpty);
        Mat disp_lines = new Mat(size, CV_8UC1, new Scalar(0, 0, 0));
        double angle=0;
        Log.d("openCV", "rows = "+lines.cols()+"\ncols = "+lines.cols());

        for (int i=0; i<lines.rows(); i++)
        {
            double[] vec = lines.get(i,0);
            //Log.d("openCV", "Vec dimensions = "+vec.length);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2,y2);

            Imgproc.line(disp_lines, start, end, new Scalar(255,0,0));

            angle += Math.atan2(y2-y1, x2-x1);

            //Log.d("openCV", "angle("+(i)+")="+angle);

        }
        angle /=lines.rows();
        double degreesAngle = Math.toDegrees(angle);
        Log.i("openCV", "Mean angle="+degreesAngle);
        return degreesAngle;

    }
}

