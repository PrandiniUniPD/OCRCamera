package com.example.mattia.fotocamera.OCRManager;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


class Tesseract extends Application implements TextRecognizer{

    //Tesseract API
    private TessBaseAPI mTess;
    //The datapath to the folder where the file trained data of tesseract is located
    String datapath;
    //language chosen to recognize the text
    String language;


    /**
     * @author Giovanni Fasan (g1)
     * Costrunctor
     */
    public Tesseract() {
        // TODO Auto-generated constructor stub
        mTess = new TessBaseAPI();
        datapath = Environment.getExternalStorageDirectory() + "/DemoOCR/";
        language = "ita";
        File dir = new File(datapath + "/tessdata/");
        //if the directory does not exist it creates it
        if (!dir.exists()) {
            dir.mkdirs();
        }
        loadAsset();
        mTess.init(datapath, language);
    }


    /**
     * @param bitmap
     * @return
     * @author Giovanni Fasan (g1)
     */
    @Override
    public String getTextFromImg(Bitmap bitmap) {
        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();
        return result;
    }

    /**
     * @author Giovanni Fasan (g1)
     * Load the trained data of tesseract
     */
    private void loadAsset(){
        AssetManager assetManager = getAssets();
        OutputStream out = null;
        try{
            //Trying to open the trained data
            InputStream in = assetManager.open("ita.traineddata");
            String tesspath = datapath;
            File tessFolder = new File(tesspath);
            //if the folder where the given date is found does not exist, it creates it
            if(!tessFolder.exists()) {
                tessFolder.mkdir();
            }
            String tessData = tesspath+"/tessdata/"+language+".traineddata";
            File tessFile = new File(tessData);
            if(!tessFile.exists()){
                //Create a new tessFile
                out = new FileOutputStream(tessData);
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);
                while (read != -1){
                    out.write(buffer, 0, read);
                    read = in.read(buffer);
                }
            }
        }catch(Exception e){
            Log.d("Tess", "couldn't copy with the following error :"+e.toString());
        }
        finally{
            try{
                if(out!=null){
                    out.close();
                }
            }catch(Exception exx){
                Log.d("Tess", "File not present"+exx.toString());
            }
        }
    }

}
