package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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
 * Class used to process the image
 */
public class ImageProcessing {
    final String  TAG = "openCV";


    private void save(Mat matrix, String imageName){

        String directory = Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_PICTURES+"/ImageProcessingTest/"+imageName;

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
            System.out.println("closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor of the class which initialize the openCV library
     * @author Thomas Porro (g1)
    */
    public ImageProcessing(){
        //Load the openCV library
        System.loadLibrary("opencv_java3");
        Log.i(TAG, "Loaded the library");
    }

    public Bitmap findText(String imagePath) throws FileNotFoundException{
        Mat img = Imgcodecs.imread( imagePath);
        RotatedRect area = detectMinTextArea(imagePath);
        img = crop(area, img);
        Bitmap image = conversion(img);
        return image;
    }

    /**
     * Calculate the angle between the text and the horizontal
     * @param imagePath path of the image you want to analyze
     * @throws FileNotFoundException if inFile is not valid
     * @return the angle between the text and the horizontal
     * @author Thomas Porro (g1)
     */
    public double computeSkew(String imagePath) throws FileNotFoundException {

        //Load this image in grayscale
        Log.d(TAG, "Image path = "+imagePath);
        Mat img = Imgcodecs.imread( imagePath, Imgcodecs.IMREAD_GRAYSCALE );

        //Throw an Exception if the matrix img is empty
        if (!img.empty()){
            Log.e(TAG, "File not found");
            throw new FileNotFoundException();
        }

        //Invert the colors (because objects are represented as white pixels, and the background is represented by black pixels)
        Core.bitwise_not( img, img );

        //Detect the edges in the image
        Imgproc.Canny(img, img, 50, 200, 3, false);

        //Create a 4 dimensions vector using matrix
        MatOfInt4 lines = new MatOfInt4();

        //Process the image with the Probabilistic Hough Transform
        Imgproc.HoughLinesP(img, lines,  1, Math.PI/180, 50, 50, 10);

        double meanAngle=0;
        Log.d(TAG, "rows = "+lines.cols()+"\ncols = "+lines.cols());

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
        Log.i(TAG, "Mean angle="+degreesAngle);
        return degreesAngle;
    }

    /**
     * Detect in which region of the picture there is some text
     * @param inFile the path of the image you want to analyze
     * @throws FileNotFoundException if inFile is not valid
     * @return the rectangle which contains the text
     * @author Thomas Porro (g1)
     */
    public RotatedRect detectMinTextArea(String inFile) throws FileNotFoundException{

        //Load this image in grayscale
        Log.d(TAG, "Image path = "+inFile);
        Mat img = Imgcodecs.imread( inFile, Imgcodecs.IMREAD_GRAYSCALE );

        //Throw an Exception if the matrix img is empty
        if (img.empty()){
            Log.e(TAG, "File not found");
            throw new FileNotFoundException();
        }

        Mat Laplacian = new Mat();
        Core.BORDER_TRANSPARENT;
        save(Laplacian, "Laplacian.jpep");

        //Detect the edges in the image
        Mat Canny = new Mat();
        Imgproc.Canny(Laplacian, Canny, 50, 200, 3, false);
        save(Canny, "Canny.jpeg");

        Mat blurredMat = new Mat();
        Imgproc.medianBlur(Canny, blurredMat, 3);
        save(blurredMat, "GaussianBlur.jpeg");

        Mat blur = new Mat();
        Imgproc.blur(blurredMat, blur, new Size(5,5));
        save(blur, "Blur.jpeg");

        Mat Dilatated = new Mat();

        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(30, 30));
        //Imgproc.morphologyEx(imgThreshold, imgThreshold, Imgproc.MORPH_CLOSE, element);
        Imgproc.dilate(blur, Dilatated, element);

        save(Dilatated, "Dilatated.jpeg");


        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(Dilatated, contours, new Mat(), 0,1);

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

        RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(max_contour.toArray()));
        /*double epsilon = 0.1*Imgproc.arcLength(new MatOfPoint2f(max_contour.toArray()),true);
        MatOfPoint2f approx = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(max_contour.toArray()),approx,epsilon,true);

        return new Mat();*/
        return rect;
    }

    /**
     * Converts the matrix into a Bitmap
     * @param matrix the matrix you want to convert
     * @return the bitmap corrisponding to the matrix
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
     * @mat the matrix you want to crop
     * @return a matrix that contains only the rectangle
     * @author Thomas Porro(g1)
     */
    public Mat crop(RotatedRect rectangle, Mat mat){
        // rect is the RotatedRect (I got it from a contour...)
        RotatedRect rect;
        // matrices we'll use
        Mat M = new Mat();
        Mat rotated = new Mat();
        Mat cropped =new Mat();
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
        // get the rotation matrix
        M = getRotationMatrix2D(rectangle.center, angle, 1.0);
        // perform the affine transformation
        Imgproc.warpAffine(mat, rotated, M, mat.size(), INTER_CUBIC);
        // crop the resulting image
        getRectSubPix(rotated, rect_size, rectangle.center, cropped);
        return cropped;
    }
}
