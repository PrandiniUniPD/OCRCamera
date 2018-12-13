package com.example.imageprocessing;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.RotatedRect;

/**
 * Class that contains all the regions with a text in an image
 * @author Thomas Porro (g1)
 */
class TextAreas implements TextRegions{
    private List<RotatedRect> detectedText;
    private int counter;


    /**
     * Constructor that initialize the arraylist
     */
    public TextAreas(){
        detectedText = new ArrayList<>();
        counter = 0;
    }


    /**
     * Add a text's region to the list
     * @param region The region we want to add
     */
    public void addRegion(RotatedRect region){
        detectedText.add(region);
    }

    @Override
    public Object next(){
        return detectedText.get(counter++);
    }

    @Override
    public boolean hasNext(){
        return counter<detectedText.size();
    }

    @Override
    public List<RotatedRect> getRegions() {
        return detectedText;
    }
}
