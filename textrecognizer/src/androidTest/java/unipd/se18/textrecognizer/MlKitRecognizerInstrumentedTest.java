package unipd.se18.textrecognizer;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MlKitRecognizerInstrumentedTest {
    private OCR ocr;
    private OCRListener ocrListener;

    @Before
    public void getOCRInstance() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getTargetContext());
        ocrListener = mock(OCRListener.class);
        ocr = TextRecognizer.getTextRecognizer(TextRecognizer.Recognizer.mlKit,ocrListener);
    }

    @Test
    public void getTextFromImgNull() {
        ocr.getTextFromImg(null);
        verify(ocrListener).onTextRecognizedError(OCRListener.BITMAP_IS_NULL_FAILURE);
    }
}
