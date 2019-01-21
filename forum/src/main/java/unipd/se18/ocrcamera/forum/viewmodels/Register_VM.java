package unipd.se18.ocrcamera.forum.viewmodels;

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
 * View model that contains all (and only) the logic to create an account
 * by querying the database with an appropriate request
 *
 * @author Alberto Valente (g2)
 */
public class Register_VM extends ViewModel implements RegisterMethods {

    /**
     * ********************
     * **   LISTENERS    **
     * ********************
     */

    /*
    Listener useful for communicating with the corresponding view ForumRegister
    according to the Model-View-View model architecture

    Real user's name and surname are not made accessible through the listener
    since they are not meant to be used inside the forum
     */
    public interface ForumRegisterListener {

        /**
         * Triggered when the register request ends up successfully
         * @param username The new username of the user that successfully signed up
         */
        void onRegisterSuccess(String username);

        /**
         * Triggered when the register request fails
         * @param message The error message about what was wrong with the register request
         */
        void onRegisterFailure(String message);

    }

    /**
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */
    private ForumRegisterListener forumRegisterListener;

    //String used for logs to identify the fragment throwing it
    private final String LOG_TAG = String.valueOf(R.string.logTagRegister_VM);

    @Override
    public void registerUserToForum(final Context context, final String username, final String password, final String name, final String surname) {

        /*
        Initialization of the listener useful to react to responses
        from the database requests, as soon as they are available
         */
        final DatabaseManager.Listeners dbListeners = new DatabaseManager.Listeners();

        /*
        Definition of the listener that will be triggered as soon as there
        is a response to the checkUsername request from the database
         */
        dbListeners.completeListener = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) throws NullPointerException {

                //The task is considered as successful when the db query ends
                if (task.isSuccessful()) {

                    /*
                    Since the task result can be potentially null, in order to avoid exceptions
                    further on a not-null condition supplied with error logs is added
                     */
                    if (task.getResult() != null) {

                        /*
                        @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/QuerySnapshot">
                            Useful Firestore documentation </a>

                        Checks if there are correspondences of the chosen username in the database
                        If not, the size results zero, so the username is unique and can be used
                         */
                        if (task.getResult().size() == 0) {
                            /*
                             * If the username is allowed, a request is sent to the database to
                             * finalize the account creation by adding missing information
                             */
                            DatabaseManager.registerUser(context, name, surname, username, password,
                                    dbListeners);
                        }
                        else {

                            //If not, the view is required to show an error
                            forumRegisterListener.onRegisterFailure(
                                    context.getString(
                                            R.string.registerUsernameAlreadyUsedMessage
                                    )
                            );
                        }
                    }
                    else {

                        //If a task result is null an error log is printed
                        Log.e(LOG_TAG, "The result of a task is NULL!");

                        //An error occurred retrieving task information, so it's shown to the user
                        forumRegisterListener.onRegisterFailure(
                                context.getString(
                                        R.string.checkUsernameErrorMessage
                                )
                        );
                    }
                }
                else {

                    //If a task exception is thrown, here it's caught
                    Log.e(LOG_TAG, "Error getting Firestore documents: ", task.getException());

                    //An error occurred completing the task, so it's shown to the user
                    forumRegisterListener.onRegisterFailure(
                            context.getString(
                                    R.string.checkUsernameErrorMessage
                            )
                    );
                }
            }
        };

        /*
        Definition of the listeners that will be triggered as soon as there
        is a response to the registerUser request from the database
         */
        dbListeners.successListener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                //If the registration process succeeds, the view is told to react properly
                forumRegisterListener.onRegisterSuccess(username);
            }
        };

        dbListeners.failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                //If the registration process fails, the view is required to show an error
                forumRegisterListener.onRegisterFailure(
                        context.getString(
                                R.string.registerFailedMessage
                        )
                );
            }
        };

        //Asks the database if the chosen username is unique since it will be the db key
        DatabaseManager.checkUsername(context, username, dbListeners);
    }

    /**
     * Sets The listener useful for communicating with the corresponding view ForumRegister
     *
     * @param listener The instance of the listener useful for communicating with the view
     */
    public void setForumRegisterListener(ForumRegisterListener listener) {

        this.forumRegisterListener = listener;
    }

}
