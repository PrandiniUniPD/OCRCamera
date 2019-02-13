package com.example.imageprocessing;

import android.util.Log;

/**
 * Singletone used to avoid more than one library load
 * @author Thomas Porro (g1)
 */
class LibraryLoaderSingleton {

    private final static String TAG = "Singleton openCV";
    private static LibraryLoaderSingleton myLibrary;

    /**
     * Private constructor that load the library
     */
    private LibraryLoaderSingleton(){
        System.loadLibrary("opencv_java3");
        Log.i(TAG, "Loaded the library");
    }

    /**
     * Method called externally of the class to load the library
     */
    static void loadLibrary(){
        if(myLibrary == null){
            myLibrary = new LibraryLoaderSingleton();
        } else {
            Log.e(TAG, "Library already loaded");
        }
    }
}
