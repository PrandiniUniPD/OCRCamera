package com.example.imageprocessing;

import org.opencv.core.RotatedRect;

import java.util.Iterator;
import java.util.List;

/**
 * Interface used to contains RotatedRect objects
 * @author Thomas Porro (g1)
 */
public interface TextRegions extends Iterator {

    /**
     * Used to get the ArrayList containing the regions
     * @return The arraylist that contains all the region with some text
     */
    List<RotatedRect> getRegions();

}
