package com.example.imageprocessing.interfaces;

import android.graphics.Bitmap;
import com.example.imageprocessing.enumClasses.ProcessingResult;
import java.util.Iterator;
import java.util.List;

/**
 * Interface used to manage all the processing's operations
 * @author Thomas Porro (g1)
 */
public interface BitmapContainer extends Iterator {

    /**
     * Get the full list of bitmaps that contains some text or the image processed
     * @return the list of bitmaps
     */
    List<Bitmap> getTextBitmaps();

    /**
     * Get the first element of a bitmap's list that contains some text or the image processed
     * @return the Bitmap in the first place
     */
    Bitmap getFirstBitmap();

    /**
     * Get the processingResult value
     * @return the value of processingResult
     */
    ProcessingResult getProcessingResult();
}
