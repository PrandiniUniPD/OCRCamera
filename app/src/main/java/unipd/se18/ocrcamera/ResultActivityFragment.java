package unipd.se18.ocrcamera;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Fragment that manages the gallery
 * @author Leonardo Pratesi (refactoring for Fragment conversion)
 */
public class ResultActivityFragment extends Fragment {


    /**
     * String for LOGS
     */
    private static final String TAG = "ResultActivityFragment";

    /**
     * The TextView of the extracted test from the captured photo.
     */
    private TextView mOCRTextView;

    /**
     * Bitmap of the lastPhoto saved
     */
    private Bitmap lastPhoto;



    public ResultActivityFragment() {
        //null constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //refresh the result activity with the new image
        // TODO find a better way

        View view = inflater.inflate(R.layout.activity_result, container, false);

        ImageView mImageView = view.findViewById(R.id.img_captured_view);
        mOCRTextView = view.findViewById(R.id.ocr_text_view);
        mOCRTextView.setMovementMethod(new ScrollingMovementMethod());


        //Get image path and text of the last image from preferences
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        String pathImage = prefs.getString("filePath", null);
        String OCRText = prefs.getString("text", null);

        lastPhoto = BitmapFactory.decodeFile(pathImage);
        if (lastPhoto != null) {
            mImageView.setImageBitmap(Bitmap.createScaledBitmap(lastPhoto, lastPhoto.getWidth(), lastPhoto.getHeight(), false));
        } else {
            Log.e("ResultActivity", "error retrieving last photo");
        }

        if (OCRText != null) {
            //do things here
        }
            // text from OCR
            ResultActivityFragment.AsyncLoad ocrTask =
                    new AsyncLoad(mOCRTextView,getString(R.string.processing));
            ocrTask.execute(lastPhoto);


        return view;
        }

    /**
     * Execute a task and post the result on the TextView given on construction
     * (g3) - modified by Leonardo Pratesi for Fragment Integration
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mOCRTextView.setText(finalTextRecognized);
                        }
                    });
                }
                else
                {
                    final String finalTextRecognized = textRecognized;
                    getActivity().runOnUiThread(new Runnable() {
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
            SharedPreferences sharedPref = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("text", s);
            editor.apply();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(),
                    progressMessage,
                    "");
        }
    }


}
