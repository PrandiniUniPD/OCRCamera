package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

/**
 * Implements the common OCR wrapper to retrieve text from an image.
 */
class TextExtractor implements OCRInterface {

    private String txtresult="Testo: \n";

    /**
     * Extract a text from a given image.
     * @param img The image in a Bitmap format
     * @return The String of the text recognized (empty String if nothing is recognized)
     */
    @Override
    public String getTextFromImg(Bitmap img) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(img);
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText result) {
                txtresult=result.getText();
            }
        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                                txtresult="Fail";
                            }
                        });
        return txtresult;
    }

}
