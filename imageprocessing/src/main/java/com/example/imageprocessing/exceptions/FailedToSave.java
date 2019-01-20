package com.example.imageprocessing.exceptions;

/**
 * Exception threw in case that detectTextAreas have an invalid method as parameter
 * @author Thomas Porro (g1)
 */
public class FailedToSave extends Exception {

    private int errorCode;
    private String errorMessage;


    /**
     * Constructor of the exceptions
     * @param code The code of the error
     */
    public FailedToSave(ErrorCodes code){
        super();
        this.errorCode = code.getErrorCode();
        this.errorMessage = code.getErrorMessage();
    }


    /**
     * Returns the error's code
     * @return the error's code
     */
    public int getErrorCode(){
        return this.errorCode;
    }


    /**
     * Returns the error's message
     * @return the error's message
     */
    public String getErrorMessage(){
        return this.errorMessage;
    }

}