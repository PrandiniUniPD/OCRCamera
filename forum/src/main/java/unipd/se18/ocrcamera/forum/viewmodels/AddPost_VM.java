package unipd.se18.ocrcamera.forum.viewmodels;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import java.util.ArrayList;
import java.util.Date;

import unipd.se18.ocrcamera.forum.DatabaseManager;
import unipd.se18.ocrcamera.forum.R;
import unipd.se18.ocrcamera.forum.RequestManager;
import unipd.se18.ocrcamera.forum.models.Post;

/**
 * ViewModel class for adding a post to the forum
 * (architecture used: Model - View - ViewModel)
 * @see <a href="https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel">
 *     Model–view–viewmodel</a>
 * @author Pietro Prandini (g2)
 */
public class AddPost_VM implements AddPostsMethods {
    /**
     * String used for logs
     */
    private String TAG = "AddPost_VM -> ";

    /**
     * Listener useful for communicating with the View
     * @see <a href="https://docs.oracle.com/javase/tutorial/uiswing/events/index.html">
     *     Writing Event Listeners</a>
     * @author Pietro Prandini (g2)
     */
    public interface addPostListener {
        /**
         * Notifies when a post is correctly added
         * @param response The response of the server
         */
        void onPostAdded(String response);

        /**
         * Notifies a problem when trying to add a new post to the database
         * @param error The error parsed
         */
        void onAddingPostFailure(String error);

        /**
         * Notifies the VM has received some not valid parameters
         * (title, message and author null or empty String)
         * @param error The String that describes the error
         */
        void onNotValidParameters(String error);
    }

    /**
     * Sets The listener useful for communicating with the View
     * @param addPostVMListener The instance of the listener useful for communicating with the view
     */
    public void setAddPostListener(AddPost_VM.addPostListener addPostVMListener) {
        this.notifier = addPostVMListener;
    }

    /**
     * The instance of the listener useful for communicating with the view
     */
    private addPostListener notifier;

    /**
     * Adds a post to the forum
     * More details at: {@link DatabaseManager}, {@link DatabaseManager.Listeners},
     * {@link DatabaseManager#addPost(Context, Post, DatabaseManager.Listeners)}.
     * @param context The reference of the activity/fragment that calls this method
     * @param title The new post's title
     * @param message The new post's message
     * @param author The new post's author
     * @author Pietro Prandini (g2)
     */
    @Override
    public void addPostToForum(final Context context, String title, String message, String author) {
        Log.i(TAG,"addPostToForum");

        // Checks the validity of the parameters
        if(!areParametersValid(title, message, author)) {
            Log.d(TAG,"addPostToForum -> The parameters are not valid");

            // The parameters are not valid
            notifier.onNotValidParameters(context.getString(R.string.not_valid_parameters));

            // Ends the method
            return;
        }

        // Sets up the listeners for interaction with the database
        DatabaseManager.Listeners databaseManagerListener = new DatabaseManager.Listeners();

        // On added post listener
        databaseManagerListener.successListener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "addPostToForum -> Post added successfully");
                notifier.onPostAdded(o.toString());
            }
        };

        // On failure to add post listener
        databaseManagerListener.failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "addPostToForum -> There was a failure when trying to add a post");
                notifier.onAddingPostFailure(e.toString());
            }
        };

        // Sets up the new post
        Date today = new Date();
        Post newPost = new Post(title,message,today,author);

        // Sends the new post to the database
        DatabaseManager.addPost(context,newPost,databaseManagerListener);
    }

    /**
     * Checks the validity of the parameters received (not null and not empty String)
     * @param title The new post's title
     * @param message The new post's message
     * @param author The new post's author
     * @return TRUE if the parameters are valid (not null and not empty String), FALSE otherwise
     * @author Pietro Prandini (g2)
     */
    private Boolean areParametersValid(String title, String message, String author) {
        // Checks if the Strings are not null and not empty ones
        return (title != null && !title.equals(""))
                && (message != null && !message.equals(""))
                && (author != null && !author.equals(""));
    }
}
