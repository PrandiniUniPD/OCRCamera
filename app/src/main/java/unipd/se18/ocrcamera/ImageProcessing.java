package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
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

import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
import static org.opencv.imgproc.Imgproc.getRectSubPix;
import static org.opencv.imgproc.Imgproc.getRotationMatrix2D;


/**
 * Class used to analyze the image
 */
public class ImageProcessing {

    /*
        Documentation of the Imgproc class available at:
        https://docs.opencv.org/java/2.4.2/org/opencv/imgproc/Imgproc.html
        
        We referenced a previous instance of the documentation since
        the newer one is still incomplete

        We are trying to implement all the Imgproc methods,
        which require a large number of
        parameters to be called, with a builder statement,
        to simplify reading and eventually changing them
        (at the moment there are a little too many "magic numbers")
     */

    //Tag used to identify the log
    final String TAG = "openCV";


    /**
     * Constructor of the class which initialize the openCV library
     * @author Thomas Porro (g1)
     */
    public ImageProcessing() {
        //TODO verify if the library is correctly loaded
        //Load the openCV library
        System.loadLibrary("opencv_java3");
        Log.i(TAG, "Loaded the library");
    }

    /**
     * Find, crop and rotate the text in an image
     * @param imagePath the path of the image you want to analyze
     * @return the cropped image
     * @throws FileNotFoundException if imagePath doesn't exist
     * @author Thomas Porro (g1)
     */
    public Bitmap findText(String imagePath) throws FileNotFoundException {
        //Converts the image into a matrix
        Mat img = Imgcodecs.imread(imagePath);

        //Call of internal methods
        RotatedRect area = detectMaxTextArea(imagePath);
        img = crop(area, img);
        Bitmap image = conversion(img);
        return image;
    }


    /**
     * Calculate the angle between the text and the horizontal
     * @param imagePath path of the image you want to analyze
     * @return the angle between the text and the horizontal
     * @throws FileNotFoundException if imagePath doesn't exist
     * @author Thomas Porro (g1)
     */
    public double computeSkew(String imagePath) throws FileNotFoundException {

        //Turns the image in grayscale and put it in a matrix
        Log.d(TAG, "Image path = " + imagePath);
        Mat img = conversion(imagePath);
        //save(img, "grayScale.jpg");

        //Invert the colors of "img" onto itself
        Core.bitwise_not(img, img);

        //Detect the edges in the image
        double threshold1 = 50;
        double threshold2 = 200;
        int apertureSize = 3;
        boolean l2gradient = false;
        Imgproc.Canny(img, img, threshold1, threshold2, apertureSize, l2gradient);

        //Create a 4 dimensions vector using matrix
        MatOfInt4 lines = new MatOfInt4();

        //Process the image with the Probabilistic Hough Transform
        double rho = 1;
        double theta = Math.PI / 180;
        int threshold = 50;
        double minLineLenght = 50;
        double maxLineGap = 10;
        Imgproc.HoughLinesP(img, lines, rho, theta, threshold, minLineLenght, maxLineGap);

        double meanAngle = 0;
        Log.d(TAG, "rows = " + lines.cols() + "\ncols = " + lines.cols());

        //Analizes the text line per line
        for (int i = 0; i < lines.rows(); i++) {
            //Get points from the beginning and the ending of the line of text
            double[] vec = lines.get(i, 0);

            //First point
            double x1 = vec[0];
            double y1 = vec[1];

            //Second point
            double x2 = vec[2];
            double y2 = vec[3];

            //Sum all the angle of the lines of text
            meanAngle += Math.atan2(y2 - y1, x2 - x1);
        }

        //Calculate the meanAngle by dividing it with the number of rows
        meanAngle /= lines.rows();

        //Transform the angle in degrees
        double degreesAngle = Math.toDegrees(meanAngle);
        Log.i(TAG, "Mean angle=" + degreesAngle);
        return degreesAngle;
    }


    /**
     * Detect in which region of the picture there is some text
     * @param imagePath the path of the image you want to analyze
     * @return the rectangle which contains the text
     * @throws FileNotFoundException if imagePath doesn't exist
     * @author Thomas Porro (g1), Oscar Garrido (g1)
     */
    public RotatedRect detectMaxTextArea(String imagePath) throws FileNotFoundException {

        //Turns the image in grayscale and put it in a matrix
        Log.d(TAG, "Image path = " + imagePath);
        Mat img = conversion(imagePath);
        //save(img, "grayScale.jpg");

        //Transforms a grayscale image to a binary image using the gaussian algorithm
        Mat threshold = new Mat();
        double maxValue = 200;
        int blockSize = 21;
        double constant = 8;
        Imgproc.adaptiveThreshold(img, threshold, maxValue, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, blockSize, constant);
        /*
            Method used for debug
            save(threshold, "threshold.jpg");
        */


        //Detect the edges in the image
        Mat canny = new Mat();
        double threshold1 = 100;
        double threshold2 = 200;
        int apertureSize = 3;
        boolean l2gradient = false;
        Imgproc.Canny(threshold, canny, threshold1, threshold2, apertureSize, l2gradient);
        //save(canny, "canny.jpg");


        /*
            kernelSize is the dimension of "element" matrix
            element is the matrix used for "morphologyEx" and "dilate" transformations
         */
        Size kernelSize = new Size(20, 20);
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, kernelSize);


        //Fill the close edges created by "canny"
        Mat morphology = new Mat();
        Imgproc.morphologyEx(canny, morphology, Imgproc.MORPH_CLOSE, element);
        //save(morphology, "morphology.jpg");


        //Smoothes the image using the median filter.
        Mat blurredMat = new Mat();
        int ksize = 15;
        Imgproc.medianBlur(morphology, blurredMat, ksize);
        //save(blurredMat, "gaussianBlur.jpg");


        //Dilates the image
        Mat dilatated = new Mat();
        Imgproc.dilate(blurredMat, dilatated, element);
        //save(dilatated, "dilate.jpg");


        //Saves the contours in a list of MatOfPoint (multidimensional vector)
        List<MatOfPoint> contours = new ArrayList<>();
        int mode = 0;
        int method = 1;
        Imgproc.findContours(dilatated, contours, new Mat(), mode, method);
        //The third parameter contains additional information that is unused


        /*
            EXPERIMENTAL:
            finds the text rectangle with the largest area
            and saves it in "max_contour"
         */
        double maxArea = 0;
        MatOfPoint max_contour = new MatOfPoint();
        Iterator<MatOfPoint> iterator = contours.iterator();
        while (iterator.hasNext()) {
            MatOfPoint contour = iterator.next();
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                max_contour = contour;
            }
        }

        //Creates a rotated rectangle based on "max_contour"
        RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(max_contour.toArray()));
        return rect;
    }


    /**
     * Converts the matrix into a Bitmap
     * @param matrix the matrix you want to convert
     * @return the bitmap corresponding to the matrix
     * @author Thomas Porro (g1)
     */
    public Bitmap conversion(Mat matrix) {
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
    public Mat crop(RotatedRect rectangle, Mat mat) {

        RotatedRect rect;

        //Matrices we'll use
        Mat rotationMat = new Mat();
        Mat rotatedImg = new Mat();
        Mat croppedImg = new Mat();

        //Get angle and size from the bounding box
        double angle = rectangle.angle;
        Size rect_size = rectangle.size;

        //Thanks to http://felix.abecassis.me/2011/10/opencv-rotation-deskewing/
        if (rectangle.angle < -45.) {
            angle += 90.0;
            double width = rect_size.width;
            double height = rect_size.height;
            rect_size.width = height;
            rect_size.height = width;
        }

        //Creates the rotation matrix
        rotationMat = getRotationMatrix2D(rectangle.center, angle, 1.0);

        //Perform the affine transformation
        Imgproc.warpAffine(mat, rotatedImg, rotationMat, mat.size(), INTER_CUBIC);

        //Crop the resulting image
        getRectSubPix(rotatedImg, rect_size, rectangle.center, croppedImg);
        return croppedImg;

    }


    /**
     * Converts a matrix into a Bitmap and saves it in a local directory
     * @param matrix the matrix to be converted
     * @param imageName the name of the file being saved
     * @author Thomas Porro(g1), Oscar Garrido(g1)
     */
    private void save(Mat matrix, String imageName) {
        final String directory = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_PICTURES + "/ImageProcessingTest/" + imageName;
        Bitmap image = conversion(matrix);

        OutputStream outStream = null;

        File file = new File(directory);
        file.mkdirs();

        if (file.exists()) {
            file.delete();
            file = new File(directory);
        }
        try {
            outStream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            Log.i(TAG, "outStream closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Converts the Bitmap into a grayscale matrix
     * @param imagePath the matrix you want to convert
     * @return the bitmap corresponding to the matrix
     * @throws FileNotFoundException if the imagePath doesn't exist
     * @author Oscar Garrido (g1)
     */
    public Mat conversion(String imagePath) throws FileNotFoundException {

        //Loads the grayscale image in a matrix
        Mat img = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);

        //Throw an Exception if "img" is empty
        if (img.empty()) {
            Log.e(TAG, "File not found");
            throw new FileNotFoundException();
        }

        return img;
    }
}