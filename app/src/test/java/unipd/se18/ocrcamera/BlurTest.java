package unipd.se18.ocrcamera;

import android.graphics.Bitmap;

import org.junit.Before;
import org.junit.Test;
import org.opencv.android.OpenCVLoader;

import static org.junit.Assert.*;

/**
 * Class used to test Blur detection effectiveness
 * @author Leonardo Pratesi (g1)
 * NON FUNZIONA !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
public class BlurTest {


    @Before
     public void init() {
        OpenCVLoader.initDebug();
    }
    @Test
    public void test()
    {   String url1 = "https://www.pyimagesearch.com/wp-content/uploads/2015/09/detecting_blur_result_007.jpg";
        String url2 = "https://www.pyimagesearch.com/wp-content/uploads/2015/09/detecting_blur_result_008.jpg";

            Bitmap nitida = LoadImageURL.getBitmapFromURL(url1);
        Bitmap sfocata =LoadImageURL.getBitmapFromURL(url2);

        double nitida1 = CameraActivity.blurValue(nitida);
        double sfocata1 = CameraActivity.blurValue(sfocata);

        boolean workingDetect= true;
        if (nitida1 < sfocata1)
            workingDetect=false;

        assertTrue(workingDetect);


    }
}
