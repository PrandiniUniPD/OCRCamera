package com.example.imageprocessing.enumClasses;

/**
 * Enum class used to identify the blurriness
 * @author Thomas Porro (g1)
 */
public enum BlurValue {
    IMAGE_NOT_BLURRED(0),
    IMAGE_BLURRED(1),
    IMAGE_NOT_ANALYZED(2);

    private int value;

    /**
     * Constructor
     * @param value The number assigned to the constant
     */
    BlurValue(int value){
        this.value = value;
    }


    /**
     * Return the mode
     * @return the mode of the constant
     */
    int getValue(){
        return this.value;
    }
}
