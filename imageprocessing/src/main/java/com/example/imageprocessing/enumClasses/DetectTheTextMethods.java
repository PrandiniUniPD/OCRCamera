package com.example.imageprocessing.enumClasses;

/**
 * Enum class used to identify the image's brightness
 * @author Thomas Porro (g1)
 */
public enum DetectTheTextMethods {
    DETECT_MAX_TEXT_AREA(0),
    DETECT_ALL_TEXT_AREAS(1);

    private int mode;

    /**
     * Constructor
     * @param mode The number assigned to the constant
     */
    DetectTheTextMethods(int mode){
        this.mode = mode;
    }


    /**
     * Return the mode
     * @return the mode of the constant
     */
    int getMode(){
        return this.mode;
    }
}
