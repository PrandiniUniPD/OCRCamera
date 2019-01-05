package unipd.se18.ocrcamera.forum.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.MutableContextWrapper;
import android.util.Log;

import java.util.ArrayList;

import unipd.se18.ocrcamera.forum.R;
import unipd.se18.ocrcamera.forum.RequestManager;

public class Login_VM extends ViewModel implements LoginMethods {

    public MutableLiveData<String> liveError = new MutableLiveData<>();

    private final String LOG_TAG = "@@Login_VM";

    @Override
    public void loginToForum(final Context context, String username, String password) {

        //Definition of the network request parameters
        ArrayList<RequestManager.Parameter> parameters = new ArrayList<>();
        parameters.add(new RequestManager.Parameter("c", RequestManager.RequestType.LOGIN.value));

        RequestManager manager = new RequestManager();

        //Implementation of the RequestManager listener
        manager.setOnRequestFinishedListener(new RequestManager.RequestManagerListener() {

            @Override
            public void onRequestFinished(String response)
            {

            }

            @Override
            public void onConnectionFailed(String message) {
                Log.d(LOG_TAG, message);
                liveError.setValue(context.getString(R.string.loginFailedMessage));
            }

            @Override
            public void onParametersSendingFailed(String message) {
                Log.d(LOG_TAG, message);
                liveError.setValue(context.getString(R.string.loginFailedMessage));
            }
        });

        //Sending a netword request to check if the provided credentials are correct
        manager.sendRequest(context, parameters);
    }
}
