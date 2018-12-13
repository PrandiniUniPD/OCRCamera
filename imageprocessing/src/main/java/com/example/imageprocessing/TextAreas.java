package com.example.imageprocessing;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.RotatedRect;

/**
 * Class that contains all the regions with a text in an image
 * @autor Thomas Porro (g1)
 */
private package class TextAreas implements TextRegions{
    private List<RotatedRect> detectedText;

    /**
     * Constructor that initialize the arraylist
     */
    public TextAreas(){
        detectedText = new ArrayList<RotatedRect>();
    }

    /**
     * Add a text's region to the list
     * @param region The region we want to add
     */
    private void addRegion(RotatedRect region){
        detectedText.add(region);
    };

    @Override
    public Object next() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public List<RotatedRect> getRegions() {
        return null;
    }
}
