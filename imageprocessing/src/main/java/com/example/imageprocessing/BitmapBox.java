package com.example.imageprocessing;

import android.graphics.Bitmap;

import com.example.imageprocessing.enumClasses.ProcessingResult;
import com.example.imageprocessing.interfaces.BitmapContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to manage the list of bitmaps and the errors
 * @author Thomas Porro (g1)
 */
class BitmapBox implements BitmapContainer {
    private List<Bitmap> container;
    private int counter;
    private ProcessingResult processingResult;

    /**
     * Constructor of the class that initialize the bitmaps's list and processingResult
     * to a default value
     */
    BitmapBox(){
        container = new ArrayList<>();
        counter = 0;
        processingResult = ProcessingResult.PROCESSING_SUCCESSFUL;
    }


    /**
     * Constructor of the class that initialize the bitmaps's list with a single image,
     * and initialize processingResult with the desired value
     */
    BitmapBox(Bitmap image, ProcessingResult value){
        container = new ArrayList<>();
        container.add(image);
        counter = 0;
        processingResult = value;
    }


    /**
     * Constructor of the class that initialize the bitmaps's list and processingResult
     * to the desired value
     */
    BitmapBox(ProcessingResult value){
        container = new ArrayList<>();
        counter = 0;
        processingResult = value;
    }


    /**
     * Add a bitmap to the list
     * @param croppedImage the image we want to add to the list
     */
    void addBitmap(Bitmap croppedImage){
        container.add(croppedImage);
    }

    /**
     * Set processingResult to the desired value
     * @param value the new value of processingResult
     */
    void setProcessingResult(ProcessingResult value){
        processingResult = value;
    }

    @Override
    public ProcessingResult getProcessingResult(){
        return processingResult;
    }

    @Override
    public List<Bitmap> getTextBitmaps() {
        return container;
    }

    @Override
    public Bitmap getFirstBitmap(){
        if(this.hasNext()) {
            return container.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Object next(){
        if(this.hasNext()) {
            return container.get(counter++);
        } else {
            return null;
        }
    }

    @Override
    public boolean hasNext(){
        return counter<container.size();
    }

}
