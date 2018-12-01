package com.example.mattia.fotocamera;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainApplication extends Application {

    public static MainApplication instance = null;

    public void onCreate(){
        super.onCreate();
        //start copy file here
        instance=this;
        copyTessDataForTextRecognizor();
    }

    /**
   	* @author Fasan Giovanni
    * @param none
   	* @return String of tess's data path
    */

    private String tessDataPath(){
        return MainApplication.instance.getExternalFilesDir(null)+"/tessdata/";
    }

    /**
    * @author Fasan Giovanni
    * @param none
    * @return String of tess's data parent directory
    */

    public String getTessDataParentDirectory(){
        return MainApplication.instance.getExternalFilesDir(null).getAbsolutePath();
    }

    /**
    * @author Fasan Giovanni
    * @param none
    * @return void
    * open and read tessdata file for recognizer the text
    */

    private void copyTessDataForTextRecognizor(){
        Runnable run = new Runnable() {
            @Override
            public void run() {
                AssetManager assetManager = MainApplication.instance.getAssets();
                OutputStream out = null;
                try{
                    Log.d("MainApplication", "CopyTessDataForTextRecognizor");
                    InputStream in = assetManager.open("ita.traineddata");
                    String tesspath = instance.tessDataPath();
                    File tessFolder = new File(tesspath);
                    if(!tessFolder.exists()) {
                        tessFolder.mkdir();
                    }
                    String tessData = tesspath+"/"+"ita.traineddata";
                    File tessFile = new File(tessData);
                    if(!tessFile.exists()){
                        out = new FileOutputStream(tessData);
                        byte[] buffer = new byte[1024];
                        int read = in.read(buffer);
                        while (read != -1){
                            out.write(buffer, 0, read);
                            read = in.read(buffer);
                        }
                        Log.d("MainApplication", " Did finish copy tess file ");
                    }
                    else{
                        Log.d("MainApplication", " tess file exist ");
                    }
                }catch(Exception e){
                    Log.d("MainApplication", "couldn't copy with the following error :"+e.toString());
                }
                finally{
                    try{
                        if(out!=null){
                            out.close();
                        }
                    }catch(Exception exx){

                    }
                }
            }
        };
        new Thread(run).start();
    }

}
