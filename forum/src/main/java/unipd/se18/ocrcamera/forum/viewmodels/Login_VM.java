package unipd.se18.ocrcamera.forum.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import unipd.se18.ocrcamera.forum.DatabaseManager;
import unipd.se18.ocrcamera.forum.R;

/**
 * View model that contains all the logic needed to perform a login by querying the database
 * @author Leonardo Rossi (g2), Alberto Valente (g2), Taulant Bullaku (g2)
 */
public class Login_VM extends ViewModel implements LoginMethods {

    /*
     * ********************
     * **   LISTENERS    **
     * ********************
     */

    /**
     * Listener useful for communicating with the View
     * @see <a href="https://docs.oracle.com/javase/tutorial/uiswing/events/index.html">
     *     Writing Event Listeners</a>
     * @author Taulant Bullaku (g2)
     */
    public interface ForumLoginListener {

        /**
         * Triggered when the login request ends up successfully
         * @param username The username of the user that successfully logged in
         */
        void onLoginSuccess(String username);

        /**
         * Triggered when the login request fails
         * @param message The error message about what was wrong with the login request
         */
        void onLoginFailure(String message);

    }

    /*
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */
    private ForumLoginListener forumLoginListener;

    //String used for logs to identify the fragment throwing it
    private final String LOG_TAG = String.valueOf(R.string.logTagLogin_VM);

    /**
     * Performs a login request providing user credentials
     *
     * @param context The reference of the activity/fragment that calls this method
     * @param username The user's nickname
     * @param password The user's password
     * @author Alberto Valente (g2)
     */
    @Override
    public void loginToForum(final Context context, final String username, String password) {

        /*
        Initialization of the listener useful to react to any kind of response
        to the database requests, as soon as they are available
         */
        final DatabaseManager.Listeners dbListeners = new DatabaseManager.Listeners();

        //Definition of the listener that will be triggered when the login process finishes
        dbListeners.completeListener = new OnCompleteListener<QuerySnapshot>() {

            /**
             * Defines what to do when the loginUser request has finished
             *
             * @param task The async task which has just completed
             */
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                //The task is considered as successful when the db query ends
                if (task.isSuccessful()) {

                    //Manages the case of a NULL task result
                    if (task.getResult() != null) {

                        /*
                        @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/QuerySnapshot">
                            Useful Firestore documentation </a>

                        If the database finds anything different from a single result checking
                        the username correspondences, a login error must have occurred
                         */
                        if (task.getResult().size() == 1) {

                            //The ForumLogin fragment is asked to access the forum
                            forumLoginListener.onLoginSuccess(username);
                        }
                        else {

                            //The ForumLogin fragment is asked to show an error
                            forumLoginListener.onLoginFailure(
                                    context.getString(
                                            R.string.loginFailedMessage
                                    )
                            );
                        }
                    }
                    else {

                        //If a task result is null an error log is printed
                        Log.e(LOG_TAG, "The result of a task is NULL!");

                        //An error occurred retrieving task information, so it's shown to the user
                        forumLoginListener.onLoginFailure(
                                context.getString(
                                        R.string.loginUserErrorMessage
                                )
                        );
                    }
                }
                else {

                    //Catches possible task exceptions
                    Log.e(LOG_TAG, "Error getting Firestore documents: ", task.getException());

                    //An error occurred completing the task, so it's shown to the user
                    forumLoginListener.onLoginFailure(
                            context.getString(
                                    R.string.loginUserErrorMessage
                            )
                    );
                }
            }
        };

        //Sends a network request to check if the provided credentials are correct
        DatabaseManager.loginUser(context, username, password, dbListeners);
    }

    /**
     * Sets The listener useful for communicating with the View
     * @param listener The instance of the listener useful for communicating with the view
     * @author Taulant Bullaku (g2)
     */
    public void setForumLoginListener(ForumLoginListener listener) {

        this.forumLoginListener = listener;
    }

}
