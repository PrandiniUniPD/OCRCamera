package unipd.se18.ocrcamera;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.label.internal.client.INativeImageLabeler;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.Semaphore;

/**
 * Implements the common OCR wrapper to retrieve text from an image.
 */
class TextExtractor implements OCRInterface {
    private FirebaseVisionImage fbImage;
    private FirebaseVisionTextRecognizer textRecognizer;
    private FirebaseVisionDocumentText documentText;
    private Context context;
    private String resultText;
    private FirebaseVisionText firebaseVisionText;

    /*protected OnSuccessListener successListener = new OnSuccessListener<FirebaseVisionText>(); {
        @Override
        public void onSuccess(FirebaseVisionText result) {
            sem1.release();
            //printText(result);
            //resultText.setValue(result.getText());
            //resultText.flush();
            //Log.d("getTextFromImg", "on success\n" + resultText.getValue());
        }
    };*/

    /*protected OnFailureListener failureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            // Task failed with an exception

            resultText.setValue("no Text recognized");
            //resultText.flush();
            //Log.d("getTextFromImg", "on fail");
            e.printStackTrace();
        }
    };*/

    public TextExtractor(Context applicationContext) {
        this.context = applicationContext;
        resultText = "";
    }

    /**
     * Extract a text from a given image.
     *
     * @param img The image in a Bitmap format
     * @return The String of the text recognized (empty String if nothing is recognized)
     */
    @Override
    public String getTextFromImg(Bitmap img) {
        //String res="";
        fbImage = FirebaseVisionImage.fromBitmap(img);
        textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        textRecognizer.processImage(fbImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText result) {
                firebaseVisionText = result;
                //resultText= result.getText();
            }
        });
        //.addOnSuccessListener(successListener).addOnFailureListener(failureListener)
        //Log.e("getTextFromImg", "final:" + resultText.getValue());
        return processText(firebaseVisionText);

    }
    public String processText(FirebaseVisionText result){
        return result.getText();
    }
}
