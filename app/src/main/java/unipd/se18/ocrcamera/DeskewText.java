package unipd.se18.ocrcamera;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class DeskewText {

    static{
        // Load the native OpenCV library
        System.loadLibrary("opencv_java3");
    }

    public static void computeSkew(String filePath) {
        
        //Load this image in grayscale
        Mat img = Imgcodecs.imread( filePath, Imgcodecs.IMREAD_GRAYSCALE );
    }
}
