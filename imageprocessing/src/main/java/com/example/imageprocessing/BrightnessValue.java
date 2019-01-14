package com.example.imageprocessing;

/**
 * Enum class used to identify the image's brightness
 * @author Thomas Porro (g1)
 */
public enum BrightnessValue {
    IMAGE_IS_OK(0),
    IMAGE_TOO_BRIGHT(1),
    IMAGE_TOO_DARK(2);

    private int value;

    /**
     * Constructor
     * @param value The number assigned to the constant
     */
    BrightnessValue (int value){
        this.value = value;
    }

    /**
     * Return the value
     * @return the value of the constant
     */
    int getValue(){
        return this.value;
    }
}
