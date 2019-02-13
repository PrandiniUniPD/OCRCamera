package com.example.imageprocessing.enumClasses;

/**
 * Enum class that contains all the error's definitions
 * @author Thomas Porro(g1)
 */
public enum ErrorCodes{
    //List of all the error with their messages
    MATRIX_EMPTY(0, "The matrix is empty, conversion failed"),
    CONVERSION_FAILED(1, "Conversion failed"),
    FAILED_TO_SAVE(2, "Failed to save the image");

    //Variables of the class
    private int errorCode;
    private String errorMessage;


    /**
     * Constructor of the error object that assings a specific error's code and message
     * @param id The error's code
     * @param message The errors' message
     */
    ErrorCodes(int id, String message){
        this.errorCode = id;
        this.errorMessage = message;
    }


    /**
     * Returns the error's code
     * @return The error's code
     */
    public int getErrorCode(){
        return this.errorCode;
    }


    /**
     * Returns the error's message
     * @return The error's message
     */
    public String getErrorMessage(){
        return this.errorMessage;
    }
}
