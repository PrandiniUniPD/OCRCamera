package com.example.imageprocessing.exceptions;

/**
 * Enum class that contains all the error's definitions
 * @author Thomas Porro(g1)
 */
public enum ErrorCodes{
    //List of all the error with their messages
    INVALID_METHOD_USED(0, "Invalid method used, the image will be not modified");

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
