package unipd.se18.ocrcamera;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


public class DeskewTextService extends IntentService {

    private final String TAG="DeskewTextService";

    public DeskewTextService(){
        super("DeskewTextService");
        System.loadLibrary("opencv_java3");
        Log.i(TAG, "Started the service");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        //Get the image's path from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String pathImage = prefs.getString("imagePath", null);

        //TODO precess the image while the main thread is waiting

    }



}
