package com.example.mattia.fotocamera;

import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class OcrManager {
    private static final String TAG = "AndroidCameraApi";
    TessBaseAPI baseAPI = null;

    public void  initAPI(){
        baseAPI = new TessBaseAPI();
        String dataPath = MainApplication.instance.getTessDataParentDirectory();
        baseAPI.init(dataPath,"ita");
        //first param is datapath which is part to the your trainned data, second is language code
        //now, your trained data stored in asset folder, we need to copy it to another exernal storage folder
        //It is better do this work when application start firt time
    }

    /**
    * @author Fasan Giovanni
    * @param Bitmap image we want to recognize
    * @return String of the text found in the image
    */
    public String getTextFromImg(Bitmap image){
        if(baseAPI==null){
            initAPI();
        }
        baseAPI.setImage(image);
        return baseAPI.getUTF8Text();
    }

}
