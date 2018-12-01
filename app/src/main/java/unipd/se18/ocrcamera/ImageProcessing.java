package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.opencv.core.Core.BORDER_DEFAULT;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
import static org.opencv.imgproc.Imgproc.getRectSubPix;
import static org.opencv.imgproc.Imgproc.getRotationMatrix2D;
import static org.opencv.imgproc.Imgproc.morphologyEx;


/**
 * Class used to analyze the image
 */
public class ImageProcessing {

    /*
        Documentation of the Imgproc class available at:
        https://docs.opencv.org/java/2.4.2/org/opencv/imgproc/Imgproc.html
        We referenced a previous instance of the documentation since
        the newer one is still incomplete
     */

    //tag used to identify the log
    final String  TAG = "openCV";

    /**
     * Constructor of the class which initialize the openCV library
     * @author Thomas Porro (g1)
     */
    public ImageProcessing(){
        //TODO Verify if the library is correctly loaded
        //Load the openCV library
        System.loadLibrary("opencv_java3");
        Log.i(TAG, "Loaded the library");
    }

    /*
     * Converts the image into a matrix
     * @param imagePath path of the image you want to analyze
     * @return the converted matrix
     * @throws FileNotFoundException if the image path doesn't exist
     * @author Thomas Porro (g1), Oscar Garrido (g1)

    private Mat Bitmap2Mat
            */

    /**
     * Find, crop and rotate the text in an image
     * @param imagePath path of the image you want to analyze
     * @return the cropped image
     * @throws FileNotFoundException if imagePath doesn't exist
     * @author Thomas Porro (g1)
     */
    public Bitmap findText(String imagePath) throws FileNotFoundException{

        //converts the image into a matrix
        Mat img = Imgcodecs.imread( imagePath);

        //call of internal methods
        RotatedRect area = detectMaxTextArea(imagePath);
        img = crop(area, img);
        Bitmap image = conversion(img);
        return image;
    }

    /**
     * Calculate the angle between the text and the horizontal
     * @param imagePath path of the image you want to analyze
     * @return the angle between the text and the horizontal
     * @throws FileNotFoundException if imagePath is not valid
     * @author Thomas Porro (g1)
     */
    public double computeSkew(String imagePath) throws FileNotFoundException {


        Log.d(TAG, "Image path = "+imagePath);
        Mat img = Imgcodecs.imread( imagePath, Imgcodecs.IMREAD_GRAYSCALE );

        //Throw an Exception if "img" is empty
        if (!img.empty()){
            Log.e(TAG, "File not found");
            throw new FileNotFoundException();
        }

        //Invert the colors of "img" onto itself
        Core.bitwise_not( img, img );

        //Detect the edges in the image
        double threshold1 = 50;
        double threshold2 = 200;
        int apertureSize = 3;
        boolean L2gradient = false;
        Imgproc.Canny(img, img, threshold1,threshold2,apertureSize,L2gradient);

        //Create a 4 dimensions vector using matrix
        MatOfInt4 lines = new MatOfInt4();

        //Process the image with the Probabilistic Hough Transform
        double rho = 1;
        double theta = Math.PI/180;
        int th
        Imgproc.HoughLinesP(img, lines,  1, Math.PI/180, 50, 50, 10);

        double meanAngle=0;
        Log.d(TAG, "rows = "+lines.cols()+"\ncols = "+lines.cols());

        //Analizes the text line per line
        for (int i=0; i<lines.rows(); i++)
        {
            //Get points from the beginning and the ending of the line of text
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
        Log.i(TAG, "Mean angle="+degreesAngle);
        return degreesAngle;
    }

    /**
     * Detect in which region of the picture there is some text
     * @param imagePath the path of the image you want to analyze
     * @return the rectangle which contains the text
     * @throws FileNotFoundException if imagePath is not valid
     * @author Thomas Porro (g1)
     */
    public RotatedRect detectMaxTextArea(String imagePath) throws FileNotFoundException{

        //Turns the image in grayscale and puts it in a matrix
        Log.d(TAG, "Image path = "+imagePath);
        Mat img = Imgcodecs.imread( imagePath, Imgcodecs.IMREAD_GRAYSCALE );

        //Throw an Exception if the matrix img is empty
        if (img.empty()){
            Log.e(TAG, "File not found");
            throw new FileNotFoundException();
        }

        //transforms a grayscale image to a binary image using the gaussian algorithm
        double maxValue = 200;
        int blockSize = 21;
        double constant = 8;
        Mat Threshold = new Mat();
        Imgproc.adaptiveThreshold(img, Threshold, maxValue, ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, blockSize, constant);
        /*
            Methods used for debug:
            save(Threshold, "Threshold.jpeg");
        */

        //Detect the edges in the image
        Mat Canny = new Mat();
        Imgproc.Canny(Threshold, Canny, 20, 200, 3, false);
        //save(Canny, "Canny.jpeg");

        /*
            kernelSize is the size of the element matrix
            element is the matrix used for "morphology" and "dilated" transformations
         */
        Size kernelSize = new Size(20,20)
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, kernelSize);

        //fill the close edges created by "Canny"
        Mat Morphology = new Mat();
        Imgproc.morphologyEx(Canny, Morphology, Imgproc.MORPH_CLOSE, element);
        //save(Morphology, "Morphology.jpeg");

        //Smoothes an image using the median filter
        int kSize = 5;
        Mat blurredMat = new Mat();
        Imgproc.medianBlur(Canny, blurredMat,kSize);
        //save(blurredMat, "GaussianBlur.jpeg");


        //Dilates the image
        Mat Dilatated = new Mat();
        Imgproc.dilate(blurredMat, Dilatated, element);
        //save(Dilatated, "Dilatated.jpeg");

        //save the contours in a List of MatOfPoint (multidimensional method)
        List<MatOfPoint> contours = new ArrayList<>();
        int mode = 0;
        int method = 1;
        Imgproc.findContours(Dilatated, contours, new Mat(), mode, method);
        //the third parameter contains additional information that is not reused

        //EXPERIMENTAL: finds the text rectangle with the largest area and saves it in "max_contour"
        double maxArea = 0;
        MatOfPoint max_contour = new MatOfPoint();
        Iterator<MatOfPoint> iterator = contours.iterator();
        while (iterator.hasNext()){
            MatOfPoint contour = iterator.next();
            double area = Imgproc.contourArea(contour);
            if(area > maxArea){
                maxArea = area;
                max_contour = contour;
            }
        }

        //creates a rotated rectangle based on "max_contour"
        RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(max_contour.toArray()));
        return rect;
    }

    /**
     * Converts the matrix into a Bitmap
     * @param matrix the matrix you want to convert
     * @return the bitmap corresponding to the matrix
     * @author Thomas Porro (g1)
     */
    public Bitmap conversion(Mat matrix){
        Bitmap image = Bitmap.createBitmap(matrix.width(), matrix.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matrix, image);
        return image;
    }

    /**
     * Crop the matrix with the given rectangle
     * @param rectangle the part of the image you want to crop
     * @param mat the matrix you want to crop
     * @return a matrix that contains only the rectangle
     * @author Thomas Porro(g1), Oscar Garrido (g1)
     */
    public Mat crop(RotatedRect rectangle, Mat mat){

        // rect is the RotatedRect (I got it from a contour...)
        RotatedRect rect;

        // matrices we'll use
        Mat rotationMat = new Mat();
        Mat rotatedImg = new Mat();
        Mat croppedImg =new Mat();

        // get angle and size from the bounding box
        double angle = rectangle.angle;
        Size rect_size = rectangle.size;


        // thanks to http://felix.abecassis.me/2011/10/opencv-rotation-deskewing/
        if (rectangle.angle < -45.) {
            angle += 90.0;
            double width = rect_size.width;
            double height = rect_size.height;
            rect_size.width = height;
            rect_size.height =width;
        }

        // creates the rotation matrix
        rotationMat = getRotationMatrix2D(rectangle.center, angle, 1.0);

        // perform the affine transformation
        Imgproc.warpAffine(mat, rotatedImg, rotationMat, mat.size(), INTER_CUBIC);

        // crop the resulting image
        getRectSubPix(rotatedImg, rect_size, rectangle.center, croppedImg);

        return croppedImg;
    }

    /**
     * Converts a matrix into a Bitmap and saves it in a local directory
     * @param matrix the matrix to be converted
     * @param imageName the name of the file being saved
     * @author Thomas Porro (g1), Oscar Garrido (g1)
     */
    private void save(Mat matrix, String imageName){

        final String directory = Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_PICTURES+"/ImageProcessingTest/"+imageName;

        OutputStream outStream = null;

        File file = new File(directory);
        file.mkdirs();

        if (file.exists()) {
            file.delete();
            file = new File(directory);
        }
        try {
            // make a new bitmap from your file
            Bitmap image = conversion(matrix);

            outStream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            Log.i(TAG, "OutStream closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts the Bitmap into a matrix
     * @param imagePath the matrix you want to convert
     * @return the bitmap corresponding to the matrix
     * @author Oscar Garrido (g1)
     */
    public Mat conversion(String imagePath) throws FileNotFoundException {

        Log.d(TAG, "Image path = "+imagePath);
        Mat img = Imgcodecs.imread(imagePath);

        //Throw an Exception if "img" is empty
        if (!img.empty()){
            Log.e(TAG, "File not found");
            throw new FileNotFoundException();
        }
    }
}
