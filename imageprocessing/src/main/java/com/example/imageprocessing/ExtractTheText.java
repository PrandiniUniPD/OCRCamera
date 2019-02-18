package com.example.imageprocessing;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.imageprocessing.enumClasses.DetectTheTextMethods;
import com.example.imageprocessing.enumClasses.ProcessingResult;
import com.example.imageprocessing.exceptions.ConversionFailedException;
import com.example.imageprocessing.interfaces.BitmapContainer;
import com.example.imageprocessing.interfaces.DetectTheText;
import com.example.imageprocessing.interfaces.TextRegions;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
import static org.opencv.imgproc.Imgproc.getRectSubPix;
import static org.opencv.imgproc.Imgproc.getRotationMatrix2D;


/**
 * Class used to analyze the image
 * @author Thomas Porro (g1), Oscar Garrido (g1)
 */
public class ExtractTheText extends PreProcessing implements DetectTheText {

    /*
        Documentation of the Imgproc class available at:
        https://docs.opencv.org/java/2.4.2/org/opencv/imgproc/Imgproc.html

        We referenced a previous instance of the documentation since
        the newer one is still incomplete
     */

    //Tag used to identify the log
    final private String TAG = "DetectingTheText";


    /**
     * Applies filters to an image do make it easier to detect rectangle areas
     * @param imageMat The matrix you want to analyze
     * @return a matrix of the filtered image
     * @author Thomas Porro (g1), Oscar Garrido (g1)
     */
    private Mat applyFilters(Mat imageMat){
        Mat grayscale = new Mat();
        Imgproc.cvtColor(imageMat, grayscale, Imgproc.COLOR_BGR2GRAY);

        //Transforms a grayscale image to a binary image using the gaussian algorithm
        Mat threshold = IPBuilder.doAdaptiveThreshold(
                new IPBuilder.AdaptiveThresholdBuilder(grayscale)
                .withMaxThreshold(200)
                .withBlockSize(21)
                .withConstant(8));
        /*
            Method used for debug
            IPDebug.saveMatrix(threshold, "threshold.jpg");
        */

        //Detect the edges in the image
        Mat canny = IPBuilder.doCanny(new IPBuilder.CannyBuilder(threshold)
                .withMinThreshold(100)
                .withMaxThreshold(200)
                .withApertureSize(3)
                .withL2gradient(false));
        //IPDebug.saveMatrix(canny, "canny.jpg");

        /*
            kernelSize is the dimension of "element" matrix
            element is the matrix used for "morphologyEx" and "dilate" transformations
         */
        Size kernelSize = new Size(20, 20);
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, kernelSize);


        //Fill the close edges created by "canny"
        Mat morphology = new Mat();
        Imgproc.morphologyEx(canny, morphology, Imgproc.MORPH_CLOSE, element);
        //IPDebug.saveMatrix(morphology, "morphology.jpg");


        //Smoothes the image using the median filter.
        Mat blurredMat = new Mat();
        int ksize = 15;
        Imgproc.medianBlur(morphology, blurredMat, ksize);
        //IPDebug.saveMatrix(blurredMat, "gaussianBlur.jpg");


        //Dilates the image
        Mat dilatated = new Mat();
        Imgproc.dilate(blurredMat, dilatated, element);
        //IPDebug.saveMatrix(dilatated, "dilate.jpg");

        return dilatated;
    }


    /**
     * Detect all the areas of the image where is supposed to be some text
     * @param filteredMat the matrix of the image you want to find the rectangles of text
     * @return A list of rectangles which is supposed to contain all the areas with some
     *         text(they could be rotated)
     * @author Thomas Porro (g1), Oscar Garrido (g1)
     */
    private List<RotatedRect> detectTextAreas(Mat filteredMat){
        List<RotatedRect> rectanglesList = new ArrayList<>();

        //Saves all the contours in a list of MatOfPoint (multidimensional vector)
        List<MatOfPoint> contours = IPBuilder.doFindContours(
                new IPBuilder.FindContoursBuilder(filteredMat)
                        .withMode(Imgproc.RETR_EXTERNAL)
                        .withMethod(Imgproc.CHAIN_APPROX_SIMPLE));


        //Fills rectanglesList with all the found rectangles of the image
        MatOfPoint maxContour;
        for (MatOfPoint contour : contours) {
            maxContour = contour;
            //Creates a rotated rectangle based on "max_contour
            RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(maxContour.toArray()));
            rectanglesList.add(rect);
        }

        return rectanglesList;
    }


    /**
     * Searches the rectangles in the matrix of an image to find
     * the largest one, even if it's rotated
     * @param filteredMat the path of the image you want to analyze
     * @return A list of rectangles which contains the rectangle of text with the
     *         maximum area (it could be rotated)
     * @author Thomas Porro (g1), Oscar Garrido (g1)
     */
    private List<RotatedRect> detectMaxTextArea(Mat filteredMat){
        List<RotatedRect> rectanglesList = new ArrayList<>();

        //Saves the contours in a list of MatOfPoint (multidimensional vector)
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(filteredMat, contours, new Mat(), Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE);
        //The third parameter contains additional information that is unused

        /*
            Finds the text rectangle with the largest area
            and saves it in "max_contour"
         */
        double maxArea = 0;
        MatOfPoint maxContour = new MatOfPoint();
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                maxContour = contour;
            }
        }
        //Creates a rotated rectangle based on "max_contour
        RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(maxContour.toArray()));
        rectanglesList.add(rect);

        //Creates and return a rotated rectangle based on "max_contour"
        return rectanglesList;
    }


    /**
     * Crop the matrix with the given rectangle
     * @param rectangle the part of the image you want to crop
     * @param mat the matrix you want to crop
     * @return a matrix that contains only the rectangle
     * @author Thomas Porro(g1), Oscar Garrido (g1)
     */
    private Mat crop(RotatedRect rectangle, Mat mat) {

        //Matrices we'll use
        Mat croppedImg = new Mat();
        Mat rotatedImg = new Mat();
        Mat rotationMat;

        //Get angle and size from the bounding box
        double angle = rectangle.angle;
        Size rectSize = rectangle.size;

        //Thanks to http://felix.abecassis.me/2011/10/opencv-rotation-deskewing/
        if (rectangle.angle < -45.) {
            angle += 90.0;
            double width = rectSize.width;
            double height = rectSize.height;
            rectSize.width = height;
            rectSize.height = width;
        }

        //Creates the rotation matrix
        rotationMat = getRotationMatrix2D(rectangle.center, angle, 1.0);

        //Perform the affine transformation (rotation)                                   *
        Imgproc.warpAffine(mat, rotatedImg, rotationMat, mat.size(), INTER_CUBIC);

        Log.d(TAG, "Channels = "+rotatedImg.channels());
        //Crop the resulting image
        getRectSubPix(rotatedImg, rectSize, rectangle.center, croppedImg);
        return croppedImg;
    }

    /**
     * See DetectTheTextMethods.java.
     * @author Thomas Porro(g1)
     */
    @Override
    public TextRegions detectTextRegions(Bitmap image, DetectTheTextMethods method) {
        TextAreas textContainer = new TextAreas();
        //Put the image into a matrix, if the conversion fails it return a textContainer
        //with the full image
        Mat img;
        try {
            img = IPUtils.conversionBitmapToMat(image);
        } catch (ConversionFailedException e){
            Log.e(TAG, e.getErrorMessage());
            //Creates a Point object that head to the center of the full image
            int centerHeight = image.getHeight()/2;
            int centerWidth = image.getWidth()/2;
            Point center = new Point(centerWidth, centerHeight);

            //Creates a Size object with the same size of the full image
            Size imageDimensions = new Size(image.getHeight(), image.getWidth());

            //Creates a RotatedRect containing the full image
            double inclinationAngle=0;
            RotatedRect fullImage = new RotatedRect(center, imageDimensions, inclinationAngle);
            textContainer.addRegion(fullImage);
            textContainer.setProcessingResult(ProcessingResult.CONVERSION_FAILED);
            return textContainer;
        }
        //Do the image Processing
        Mat filteredMat = applyFilters(img);
        //Add each element to the TextAreas's object
        List<RotatedRect> rectanglesList = new ArrayList<>();
        switch(method){
            case DETECT_MAX_TEXT_AREA: rectanglesList = detectMaxTextArea(filteredMat);
                    break;
            case DETECT_ALL_TEXT_AREAS: rectanglesList = detectTextAreas(filteredMat);
                    break;
        }
        for(RotatedRect rectangle : rectanglesList){
            textContainer.addRegion(rectangle);
        }
        return textContainer;
    }

    /**
     * See DetectTheTextMethods.java.
     * @author Thomas Porro(g1)
     */
    @Override
    public BitmapContainer extractTextFromBitmap(Bitmap image, TextRegions textContainer) {
        BitmapBox imgTextContainer = new BitmapBox();
        Mat img;

        //Converts the image into a matrix
        try {
            img = IPUtils.conversionBitmapToMat(image);
        } catch (ConversionFailedException e){
            Log.e(TAG, e.getErrorMessage());
            imgTextContainer.addBitmap(image);
            imgTextContainer.setProcessingResult(ProcessingResult.CONVERSION_FAILED);
            return imgTextContainer;
        }

        /*Modifies the number of channel of the image so the  matrix is compatible  with
          Imgproc.getRectSubPix method*/
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGRA2BGR);

        /*For each rectangle contained in textContainer extract the rectangle and saves it
          into a bitmap*/
        RotatedRect rectangle;
        try {
            while (textContainer.hasNext()) {
                rectangle = (RotatedRect) textContainer.next();
                Mat croppedMat = crop(rectangle, img);
                //If the conversion failed return a List with only the original image
                Bitmap croppedBitmap = IPUtils.conversionMatToBitmap(croppedMat);
                imgTextContainer.addBitmap(croppedBitmap);
            }
        } catch (ConversionFailedException e) {
            Log.e(TAG, e.getErrorMessage());
            BitmapBox imgTextContainerFailure = new BitmapBox();
            imgTextContainerFailure.addBitmap(image);
            imgTextContainerFailure.setProcessingResult(ProcessingResult.CONVERSION_FAILED);
            return imgTextContainerFailure;
        }
        //Debug method
        //IPDebug.saveBitmapList(imgTextContainer);
        return imgTextContainer;
    }
}
