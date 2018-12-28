package com.example.imageprocessing;

public enum DetectTheTextMethods {
    DETECT_MAX_TEXT_AREA(0),
    DETECT_ALL_TEXT_AREAS(1);

    private int mode;
    DetectTheTextMethods(int mode){
        this.mode = mode;
    }

    int getMode(){
        return this.mode;
    }
}
