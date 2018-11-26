package unipd.se18.ocrcamera;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt4;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


/**
 * Class used to process the image
 */
public class ImageProcessing {

    /**
     * Constructor of the class which initialize the openCV library
     * @author Thomas Porro (g1)
    */
    public ImageProcessing(){
        //Load the openCV library
        System.loadLibrary("opencv_java3");
        Log.i("openCV", "Loaded the library");
    }


    /**
     * Calculate the angle between the text and the horizontal
     * @param imagePath path of the image you want to analyze
     * @return the angle between the text and the horizontal
     * @author Thomas Porro (g1)
     */
    public double computeSkew(String imagePath) {

        //TODO some action if imagePath or the matrix lines is empty

        //Load this image in grayscale
        Log.d("openCV", "Image path = "+imagePath);
        Mat img = Imgcodecs.imread( imagePath, Imgcodecs.IMREAD_GRAYSCALE );

        //Invert the colors (because objects are represented as white pixels, and the background is represented by black pixels)
        Core.bitwise_not( img, img );

        //Detect the edges in the image
        Imgproc.Canny(img, img, 50, 200, 3, false);

        //Create a 4 dimensions vector using matrix
        MatOfInt4 lines = new MatOfInt4();

        //Process the image with the Probabilistic Hough Transform
        Imgproc.HoughLinesP(img, lines,  1, Math.PI/180, 50, 50, 10);

        //Detect if the matrix line is empty
        boolean isEmpty=lines.empty();
        Log.d("openCV", "Lines empty = "+isEmpty);

        double meanAngle=0;
        Log.d("openCV", "rows = "+lines.cols()+"\ncols = "+lines.cols());

        //Analizes line per line
        for (int i=0; i<lines.rows(); i++)
        {
            //Get points from the line of text
            double[] vec = lines.get(i,0);

            //First point
            double x1 = vec[0];
            double y1 = vec[1];

            //Second point
            double x2 = vec[2];
            double y2 = vec[3];

            //Sum all the angle of the lines of text
            meanAngle += Math.atan2(y2-y1, x2-x1);
        }

        //calculate the meanAngle by dividing it with the number of rows
        meanAngle /= lines.rows();

        //Transform the angle in degrees
        double degreesAngle = Math.toDegrees(meanAngle);
        Log.i("openCV", "Mean angle="+degreesAngle);
        return degreesAngle;
    }
}
