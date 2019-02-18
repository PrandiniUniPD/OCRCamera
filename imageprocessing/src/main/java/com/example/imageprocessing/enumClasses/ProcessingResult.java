package com.example.imageprocessing.enumClasses;

/**
 * Enum class that contains all the processing's result
 * @author Thomas Porro(g1)
 */
public enum ProcessingResult{
    //List of all the error with their messages
    CONVERSION_FAILED(0, "Conversion Failed"),
    PROCESSING_SUCCESSFUL(1, "Processing successful");

    //Variables of the class
    private int resultCode;
    private String resultMessage;

    /**
     * Constructor of the error object that assings a specific error's code and message
     * @param id The result's code
     * @param message The result's message
     */
    ProcessingResult(int id, String message){
        resultCode = id;
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
