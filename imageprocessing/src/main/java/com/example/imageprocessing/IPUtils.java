package com.example.imageprocessing;

import android.graphics.Bitmap;
import android.util.Log;
import com.example.imageprocessing.exceptions.ConversionFailedException;
import com.example.imageprocessing.enumClasses.ErrorCodes;
import com.example.imageprocessing.exceptions.FailedToSave;
import com.example.imageprocessing.exceptions.MatrixEmptyException;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Utility class used in ExtractTheText
 * @author Thomas Porro (g1), Oscar Garrido (g1)
 */
class IPUtils {
    //Tag used to identify the log
    private final static String TAG = "IPUtils";

    /**
     * Converts a matrix into a Bitmap and saves it in the default temp-file dir
     * Used in case of debug.
     * @param matrix the matrix to be converted
     * @param tmpPrefix the name of the file being saved
     * @param tmpSuffix the extension of the file being saved
     * @author Thomas Porro(g1), Oscar Garrido(g1)
     */
    static void save(Mat matrix, String tmpPrefix, String tmpSuffix) throws FailedToSave{

        Bitmap image;

        try {
            image = conversionMatToBitmap(matrix);
        } catch (ConversionFailedException e){
           throw new FailedToSave(ErrorCodes.FAILED_TO_SAVE);
        }
        OutputStream outStream;

        try {

            //if not specified, the system-dependent default temporary-file directory will be used
            File tmpFile = File.createTempFile(tmpPrefix, tmpSuffix);

            if (tmpFile.exists()) {
                tmpFile.delete();
                tmpFile = File.createTempFile(tmpPrefix, tmpSuffix);
            }

            try {
                outStream = new FileOutputStream(tmpFile);
                image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
                Log.i(TAG, "outStream closed");
            } catch (FileNotFoundException fileNotFound) {
                fileNotFound.printStackTrace();
            }

        } catch (IOException fileNotCreated) {
            fileNotCreated.printStackTrace();
        }
    }


    /**
     * Converts the matrix into a Bitmap
     * @param matrix the matrix you want to convert
     * @return the Bitmap corresponding to the matrix
     * @throws ConversionFailedException if the conversion failed (see documentation of openCV's
     *         method "matToBitmap")
     * @author Thomas Porro (g1)
     */
    static Bitmap conversionMatToBitmap(Mat matrix) throws ConversionFailedException{
        //Create a Bitmap with a specific height, width and configuration
        Bitmap image = Bitmap.createBitmap(matrix.width(),
                matrix.height(), Bitmap.Config.ARGB_8888);
        try {
            Utils.matToBitmap(matrix, image);
        } catch (Exception e){
            throw new ConversionFailedException(ErrorCodes.CONVERSION_FAILED);
        }
        return image;
    }


    /**
     * Converts the Bitmap into a matrix
     * @param image the Bitmap you want to convert
     * @return the matrix corresponding to the Bitmap
     * @throws ConversionFailedException if the conversion failed (see documentation of openCV's
     *         method "bitmapToMat")
     * @throws MatrixEmptyException if the final matrix is empty
     * @author Oscar Garrido (g1)
     */
    static Mat conversionBitmapToMat(Bitmap image)
            throws MatrixEmptyException, ConversionFailedException{

        //Loads the grayscale image in a matrix
        Mat img = new Mat();
        try{
            Utils.bitmapToMat(image, img, true);
        } catch (Exception e) {
            throw new ConversionFailedException(ErrorCodes.CONVERSION_FAILED);
        }

        //Throw an Exception if "img" is empty
        if (img.empty()) {
            Log.e(TAG, "File not found");
            throw new MatrixEmptyException(ErrorCodes.MATRIX_EMPTY);
        }
        return img;
    }
}