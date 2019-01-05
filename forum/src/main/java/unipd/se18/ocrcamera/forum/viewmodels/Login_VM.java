package unipd.se18.ocrcamera.forum.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import unipd.se18.ocrcamera.forum.R;
import unipd.se18.ocrcamera.forum.RequestManager;
import unipd.se18.ocrcamera.forum.models.Post;

public class Login_VM extends ViewModel implements LoginMethods {

    /**
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */
    public MutableLiveData<String> liveLoginResponse = new MutableLiveData<>();
    public MutableLiveData<String> liveError = new MutableLiveData<>();

    /**
     * String used for logs to identify the viewmodel throwing it
     */
    private final String LOG_TAG = "@@Login_VM";

    /**
     * Key for requesting to perform a login
     * (used by the server that hosts the forum)
     */
    private final String KEY_LOGIN_REQUEST = "c";

    /**
     * Key for sending username to the server
     * (used by the server that hosts the forum)
     */
    private final String KEY_LOGIN_USERNAME = "user";

    /**
     * Key for sending password to the server
     * (used by the server that hosts the forum)
     */
    private final String KEY_LOGIN_PASSWORD = "pwd";

    @Override
    public void loginToForum(final Context context, String username, String password) {

        // Sets up the manager to perform login
        RequestManager loginManager = new RequestManager();

        //Definition of the network request parameters
        ArrayList<RequestManager.Parameter> loginManagerParameters = new ArrayList<>();

        // Sets up the login request parameter
        RequestManager.Parameter loginParameter =
                new RequestManager.Parameter(
                        KEY_LOGIN_REQUEST,
                        RequestManager.RequestType.LOGIN.value
                );

        //Sets up the username parameter
        RequestManager.Parameter usernameParameter =
                new RequestManager.Parameter(
                        KEY_LOGIN_USERNAME,
                        username
                );

        //Sets up the password parameter
        RequestManager.Parameter passwordParameter =
                new RequestManager.Parameter(
                        KEY_LOGIN_PASSWORD,
                        password
                );

        //Sets up the complete parameter to send to the server by adding previous parameters
        loginManagerParameters.add(loginParameter);
        loginManagerParameters.add(usernameParameter);
        loginManagerParameters.add(passwordParameter);

        //Implementation of the RequestManager listener
        loginManager.setOnRequestFinishedListener(new RequestManager.RequestManagerListener() {

            /**
             * If the user successfully logs in, the ShowPost is loaded
             * so that the user is allowed to access the forum content.
             * If not, the user is shown an error message for incorrect credentials.
             * @param response The network request's response
             */
            @Override
            public void onRequestFinished(String response) {
                //The live data is triggered so that the UI can be correctly updated
                liveLoginResponse.setValue(response);
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

        //Sends a netword request to check if the provided credentials are correct
        loginManager.sendRequest(context, loginManagerParameters);
    }
}
