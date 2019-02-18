package com.example.imageprocessing.interfaces;

import android.graphics.Bitmap;

import java.util.Iterator;
import java.util.List;

/**
 * Interface used to get the list of the cropped images with some text in them
 * @author Thomas Porro (g1)
 */
public interface BitmapContainer extends Iterator {

    /**
     * Get a list of bitmaps that hopefully contains some text in them
     * @return the list of bitmaps
     */
    List<Bitmap> getTextBitmaps();
}
