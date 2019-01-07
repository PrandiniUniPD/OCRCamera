package unipd.se18.ocrcamera.forum.viewmodels;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

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
     * Key for requesting to add a new post
     * (used by the server that hosts the forum)
     */
    private final String KEY_ADD_POST_REQUEST = "c";

    /**
     * Key for indicating the content of the post
     * (used by the server that hosts the forum)
     */
    private final String KEY_JSON_POST_CONTENT = "jPost";

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
         * Notifies a connection problem to the network
         * @param error The error parsed
         */
        void onConnectionFailed(String error);

        /**
         * Notifies a failure of a sending parameters process addressed to the server
         * @param error The error parsed
         */
        void onParametersSendingFailed(String error);

        /**
         * Notifies the VM has received some not valid parameters
         * (title, message and author null or empty String)
         * @param error The String that describes the error
         */
        void onNotValidParameters(String error);

        /**
         * Notifies a failure in the creation of a JSONPost
         * @param  error The error String
         */
        void onJSONPostCreationFailed(String error);
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
     * Keys of the JSON strings value for a post
     * @see <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html">Enum Types</a>
     */
    public enum JSONPostKey {
        ID("ID"),
        TITLE("title"),
        MESSAGE("message"),
        DATE("date"),
        LIKES("likes"),
        COMMENTS("comments"),
        AUTHOR("author");

        public String value;

        /**
         * Defines an object of type JSONPostKey
         * @param value The value of the key for the JSON format of the post
         */
        JSONPostKey(String value){ this.value = value; }
    }

    /**
     * Listener useful to receive notification from the requests manager
     * More details at: {@link RequestManager.RequestManagerListener}
     */
    private RequestManager.RequestManagerListener requestManagerListener =
            new RequestManager.RequestManagerListener() {
        /**
         * Notifies that a post was added correctly
         * @param response The network request's response
         */
        @Override
        public void onRequestFinished(String response) {
            // Post added
            Log.d(TAG,"onRequestFinished -> response: " + response);
            notifier.onPostAdded(response);
        }

        /**
         * Notifies a connection problem to the network
         * @param message The error message
         */
        @Override
        public void onConnectionFailed(String message) {
            // Connection problem
            Log.d(TAG,"onConnectionFailed -> message: " + message);
            notifier.onConnectionFailed(message);
        }

        /**
         * Notifies a failure of a sending parameters process addressed to the server
         * @param message The error message
         */
        @Override
        public void onParametersSendingFailed(String message) {
            // Parameters not sent correctly
            Log.d(TAG,"onParametersSendingFailed -> message: " + message);
            notifier.onParametersSendingFailed(message);
        }
    };

    /**
     * Adds a post to the forum
     * More details at: {@link RequestManager},
     * {@link RequestManager#sendRequest(Context, ArrayList)}.
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
        if(!checkParametersValidity(title, message, author)) {
            Log.d(TAG,"addPostToForum -> The parameters are not valid");

            // The parameters are not valid
            notifier.onNotValidParameters(context.getString(R.string.not_valid_parameters));

            // Ends the method
            return;
        }

        // Sets up the manager useful for adding posts
        RequestManager postManager = new RequestManager();

        // Sets up the manager worker listener
        postManager.setOnRequestFinishedListener(requestManagerListener);

        /*
        The next try catch handles a possible failure on the creation
        of the JSON object that represents a new post.
         */

        try {
            // Sets up the parameters for the adding post request
            ArrayList<RequestManager.Parameter> postManagerParameters =
                    getAddPostParameters(title, message, author);

            // Sends the request
            Log.i(TAG,"addPostToForum -> Sending the request");
            postManager.sendRequest(context, postManagerParameters);
        } catch (JSONException e) {
            // JSON Post creation failed
            e.printStackTrace();
            // Notifies the error
            Log.d(TAG, "addPostToForum -> JSON creation problem");
            notifier.onJSONPostCreationFailed(e.toString());
        }
    }

    /**
     * Checks the validity of the parameters received
     * @param title The new post's title
     * @param message The new post's message
     * @param author The new post's author
     * @return TRUE if the parameters are valid, FALSE otherwise
     * @author Pietro Prandini (g2)
     */
    private Boolean checkParametersValidity(String title, String message, String author) {
        // Checks if the Strings are not null and not empty ones
        return (title != null && !title.equals(""))
                && (message != null && !message.equals(""))
                && (author != null && !author.equals(""));
    }

    /**
     * Sets up the parameters for sending the post adding request
     * More details at: {@link RequestManager.Parameter},
     * {@link RequestManager.RequestManagerListener},
     * {@link RequestManager#setOnRequestFinishedListener(RequestManager.RequestManagerListener)}.
     * @param title The new post's title
     * @param message The new post's message
     * @param author The new post's author
     * @return The ArrayList of the parameters required,
     * null if the JSON has not been created successfully.
     * @throws JSONException If the JSON post is not correctly built
     * @author Pietro Prandini (g2)
     */
    private ArrayList<RequestManager.Parameter> getAddPostParameters
    (String title, String message, String author) throws JSONException {
        // Sets up the add post request parameter
        RequestManager.Parameter addPostParameter =
                new RequestManager.Parameter(
                        KEY_ADD_POST_REQUEST,
                        RequestManager.RequestType.ADD_POST.value
                );

        // Formats the post in JSON format
        String JSONPostContent = getJSONPost(title, message, author);

        // Sets up the post content parameter
        RequestManager.Parameter postContentParameter =
                new RequestManager.Parameter(
                        KEY_JSON_POST_CONTENT,
                        JSONPostContent
                );

        // Adds the parameters to an ArrayList
        ArrayList<RequestManager.Parameter> postManagerParameters = new ArrayList<>();
        postManagerParameters.add(addPostParameter);
        postManagerParameters.add(postContentParameter);

        // Returns the ArrayList of the parameters required
        return postManagerParameters;
    }

    /**
     * Get a JSON string that represents a new post
     * More details at: {@link Post}, {@link JSONObject}, {@link JSONObject#put(String, Object)}.
     * @param title The new post's title
     * @param message The new post's message
     * @param author The new post's author
     * @return The JSON string that represents the forum posts,
     * null if the JSON has not been created successfully.
     * @throws JSONException If the JSON post is not correctly built
     * @author Pietro Prandini (g2)
     */
    private String getJSONPost(String title, String message, String author) throws JSONException {
        // Prepares the post's data
        Date today = new Date();

        // Creates a post
        Post newPost = new Post(title, message, today, author);

        // Puts the values of the post in a JSON object that represents the post
        JSONObject JSONPost = new JSONObject();

        JSONPost.put(JSONPostKey.ID.value, newPost.getID());
        JSONPost.put(JSONPostKey.TITLE.value, newPost.getTitle());
        JSONPost.put(JSONPostKey.MESSAGE.value, newPost.getMessage());
        JSONPost.put(JSONPostKey.DATE.value, newPost.getDate());
        JSONPost.put(JSONPostKey.LIKES.value, newPost.getLikes());
        JSONPost.put(JSONPostKey.COMMENTS.value, newPost.getComments());
        JSONPost.put(JSONPostKey.AUTHOR.value, newPost.getAuthor());

        // JSONPost is correctly built
        Log.i(TAG, "getJSONPost -> JSONPost is correctly built\n"
                + "-----      NEW JSON POST       -----"
                + "\n" +   JSONPost.toString()    + "\n"
                + "----- END OF THE NEW JSON POST -----"
        );
        return JSONPost.toString();
    }
}
