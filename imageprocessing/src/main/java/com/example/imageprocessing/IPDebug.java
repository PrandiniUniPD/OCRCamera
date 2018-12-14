package com.example.imageprocessing;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Class used for debugging
 */
class IPDebug {
    private final static String directory = Environment.getExternalStorageDirectory()+
            "/"+Environment.DIRECTORY_PICTURES+"/ImageProcessingTest/";
    private final static String TAG = "IPDebug";

    /**
     * Class used to save a list of bitmap in a directory
     * @param bitmapList the list we want to save
     */
    static void saveBitmapList(List<Bitmap> bitmapList){
        int imageNumber = 0;
        String imageName;
        for(Bitmap currentImage : bitmapList){
            imageName = "imageNumber_n"+imageNumber+".jpg";
            saveImage(currentImage, directory+imageName);
            imageNumber++;
        }
    }

    /**
     * Method used to save an image in a predefined directory
     * @param image the image we want to save
     * @param path the path where we want to save the image
     */
    private static void saveImage(Bitmap image, String path){
        File fileToSave = new File(path);
        OutputStream outStream;
        try{
            if (fileToSave.exists()) {
                fileToSave.delete();
                fileToSave = new File(path);
            }
            try {
                  outStream = new FileOutputStream(fileToSave);
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
}
