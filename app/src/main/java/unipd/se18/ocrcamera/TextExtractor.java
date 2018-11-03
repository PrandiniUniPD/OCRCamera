package unipd.se18.ocrcamera;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

/**
 * Class the implements the common OCR wrapper to retrieve text from an image
 * @author Leonardo Rossi (g2)
 */
public class TextExtractor implements OCRInterface {
    MutableLiveData<String> extractedText;
    Context context;

    /**
     * It defines an object of type TextExtractor
     */
    public TextExtractor(Context context) {
        extractedText = new MutableLiveData<>();
        this.context = context;
    }

    /**
     * Extracts a text from a given image.
     * @param img The image in a Bitmap format
     * @return The String of the text recognized (empty String if nothing is recognized)
     * @author Leonardo Rossi (g2)
     */
    @Override
    public String getTextFromImg(Bitmap img) {
        //Defines the image that will be analysed to get the text
        FirebaseVisionImage fbImage = FirebaseVisionImage.fromBitmap(img);
        //Defines that will be used an on device text recognizer
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        textRecognizer.processImage(fbImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                //Saving of the retrieved text into shared preferences
                storeText(firebaseVisionText.getText());
                //If there's some text the live data is updated so that can be updated the UI too
                extractedText.setValue(firebaseVisionText.getText());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                extractedText.setValue("No text retrieved");
            }
        });

        return extractedText.getValue();
    }


    /**
     * Saves the given text into the shared preferences so that it can be reused in the future
     * @param text The text to save into the shared preferences
     * @author Leonardo Rossi (g2)
     */
    private void storeText(String text) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("text", text);
        editor.commit();
    }
}
