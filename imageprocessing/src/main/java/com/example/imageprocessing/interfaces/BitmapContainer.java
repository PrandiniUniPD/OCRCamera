package com.example.imageprocessing.interfaces;

import android.graphics.Bitmap;

import java.util.Iterator;
import java.util.List;

/**
 * Interface used to manage all the processing's operations
 * @author Thomas Porro (g1)
 */
public interface BitmapContainer extends Iterator {

    /**
     * Get the full list of bitmaps
     * @return the list of bitmaps
     */
    List<Bitmap> getTextBitmaps();

    /**
     * Get the first element of the bitmap's list
     * @return the Bitmap in the first place
     */
    Bitmap getFirstBitmap();
}
