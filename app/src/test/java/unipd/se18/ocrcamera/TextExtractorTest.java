package unipd.se18.ocrcamera;

import android.content.Context;
import android.graphics.Bitmap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
@author Elia Bedin, Andrea Ton
*/

class TextExtractorTest extends TextExtractor{

    TextExtractorTest(Context context) {
        super(context);
    }

    @Test
    void getTextFromImg_Empty() {
      Bitmap IMG_Generated = null;
      try {
          IMG_Generated = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
      } catch (Exception e) {
        e.getCause().printStackTrace();
      }
      String result = getTextFromImg(IMG_Generated);
      //assertNotNull(result);
      assertEquals(result, "No text retrieved");
    }
}
