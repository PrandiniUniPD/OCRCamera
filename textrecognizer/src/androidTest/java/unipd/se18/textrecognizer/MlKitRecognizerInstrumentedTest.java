package unipd.se18.textrecognizer;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.google.firebase.FirebaseApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * MlKitRecognizer instrumented test, which will execute on an Android device.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MlKitRecognizerInstrumentedTest {
    /**
     * The instance of the OCR recognizer
     */
    private OCR ocr;

    /**
     * The instance of the listener used by the recognizer for communicating events
     */
    private OCRListener ocrListener;

    /**
     * Prepares the environments for the tests
     */
    @Before
    public void getOCRInstance() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getTargetContext());
        ocrListener = mock(OCRListener.class);
        ocr = TextRecognizer.getTextRecognizer(TextRecognizer.Recognizer.mlKit,ocrListener);
    }

    /**
     * Test the extractor with a null for paramenter
     */
    @Test
    public void getTextFromImgNull() {
        ocr.getTextFromImg(null);
        verify(ocrListener).onTextRecognizedError(OCRListener.BITMAP_IS_NULL_FAILURE);
    }

    /*
    The class MlKitRecognizer is strictly related to the mlkit from Firebase.
    The testing about this class depends from the test of the firebase API.
     */
}
