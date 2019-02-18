package com.example.imageprocessing.enumClasses;

/**
 * Enum class that contains all the processing's result
 * @author Thomas Porro(g1)
 */
public enum ProcessingResult{
    //List of all the error with their messages
    CONVERSION_FAILED(0, "Conversion Failed"),
    PROCESSING_SUCCESSFUL(1, "Processing successful"),
    PROCESSING_FAILED(2, "Processing Failed"),
    BRIGHTNESS_MODIFIED(3, "Brightness modified successfully"),
    BRIGHTNESS_CONVERSION_ERROR(4, "Error while converting the image"),
    AUTOSKEW_FAILED(5, "Error while converting the image"),
    AUTOSKEW_SUCCESSFUL(6, "Image successfully rotated");

    //Variables of the class
    private int resultCode;
    private String resultMessage;

    /**
     * Constructor of the error object that assings a specific error's code and message
     * @param value The number assigned to the constant
     * @param message The result's message
     */
    ProcessingResult(int value, String message){
        resultCode = value;
        resultMessage = message;
    }

    /**
     * Returns the error's code
     * @return The error's code
     */
    public int getResultCode(){
        return this.resultCode;
    }


    /**
     * Returns the error's message
     * @return The error's message
     */
    public String getResultMessage(){
        return this.resultMessage;
    }
}
