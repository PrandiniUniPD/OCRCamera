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

    /**
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

    /**
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */
    private ForumLoginListener forumLoginListener;

    @Override
    public void loginToForum(final Context context, final String username, String password) {

        /**
         * Initialization of the listener useful to react to responses
         * from the database requests, as soon as they are available
         */
        final DatabaseManager.Listeners dbListeners = new DatabaseManager.Listeners();

        //Definition of the listener that will be triggered when the login process finishes
        dbListeners.completeListener = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    if (task.getResult().size() == 1)
                    {
                        forumLoginListener.onLoginSuccess(username);
                    }
                    else
                    {
                        forumLoginListener.onLoginFailure(context.getString(R.string.loginFailedMessage));
                    }
                }
            }
        };

        //Sends a netword request to check if the provided credentials are correct
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
