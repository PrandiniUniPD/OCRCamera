package com.example.imageprocessing;

import com.example.imageprocessing.enumClasses.ProcessingResult;

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
    private ProcessingResult processingResult;


    /**
     * Constructor that initialize the object
     */
    TextAreas(){
        detectedText = new ArrayList<>();
        counter = 0;
        processingResult = ProcessingResult.PROCESSING_SUCCESSFUL;
    }


    /**
     * Constructor that initialize the object setting the value of processingResult to the
     * desired one
     */
    TextAreas(ProcessingResult value){
        detectedText = new ArrayList<>();
        counter = 0;
        processingResult = value;
    }


    /**
     * Add a text's region to the list
     * @param region The region we want to add
     */
    void addRegion(RotatedRect region){
        detectedText.add(region);
    }


    /**
     * Set the value of the enum class
     * @param value the new value of processingResult
     */
    void setProcessingResult(ProcessingResult value){
        processingResult = value;
    }


    /**
     * Get the value of the enum class
     * @return the value of the enum class
     */
    ProcessingResult getProcessingResult(){
        return processingResult;
    }

    @Override
    public Object next(){
        if(this.hasNext()) {
            return detectedText.get(counter++);
        } else {
            return null;
        }
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
