package unipd.se18.ocrcamera.forum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.MutableContextWrapper;

import java.util.ArrayList;

import unipd.se18.ocrcamera.forum.RequestManager;

public class Login_VM extends ViewModel implements LoginMethods {

    @Override
    public void loginToForum(Context context, String username, String password) {

        //Definition of the network request parameters
        ArrayList<RequestManager.Parameter> parameters = new ArrayList<>();
        parameters.add(new RequestManager.Parameter("c", RequestManager.RequestType.LOGIN.value));

        RequestManager manager = new RequestManager();

        //Implementation of the RequestManager listener
        manager.setOnRequestFinishedListener(new RequestManager.RequestManagerListener() {

            @Override
            public void onRequestFinished(String response) {

            }

            @Override
            public void onConnectionFailed(String message) {

            }

            @Override
            public void onParametersSendingFailed(String message) {

            }
        });

        //Sending of a netword request to check if the provided credentials are correct

        manager.sendRequest(context, parameters);
    }
}
