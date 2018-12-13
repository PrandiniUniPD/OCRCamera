package com.example.imageprocessing;

import org.opencv.core.RotatedRect;

import java.util.Iterator;
import java.util.List;

public interface TextRegions extends Iterator {

    /**
     * Used to get the ArrayList containing the regions
     * @return The arraylist that contains all the region with some text
     */
    List<RotatedRect> getRegions();

}
