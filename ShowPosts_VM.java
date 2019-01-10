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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import unipd.se18.ocrcamera.forum.DatabaseManager;
import unipd.se18.ocrcamera.forum.R;
import unipd.se18.ocrcamera.forum.RequestManager;
import unipd.se18.ocrcamera.forum.models.Post;


/**
 * According to the MVVM (Model-View-ViewModel) architecture this class contains all fragment ShowPosts' logic.
 * This architecture ensures that the code is more testable and organised than the classic approch to put everything
 * in the activity or fragment class. 
 * In particular the methods that are implemented here are used to get posts from forum and to handle users' likes. The
 * forum's data are stored into a database handle thanks to FirebaseFirestore API
 * @author Leonardo Rossi g2
 */
public class ShowPosts_VM extends ViewModel implements ShowPostsMethods
{

    /**
     * ********************
     * **   LISTENERS    **
     * ********************
     */

    /**
    * This listener is triggered to update the UI with the result of a db interrogation to get posts inside the forum.
    * If the result is successfull, that is all the posts are correctly downloaded, the ShowPost fragment recevives the
    * posts list, otherwise it receives an error message that can be shown to the user
    */
    public interface GetPostListener
    {
        /**
         * Triggered when, after the db interrogation, all the posts are correctly parsed
         * @param posts The posts list
         */
        void onGetPostsSuccess(ArrayList<Post> posts);

        /**
         * Triggered when after the db interrogation an error occurs while parsing the posts
         * @param message The error message
         */
        void onGetPostFailure(String message);

    }

    /**
    * This listener is triggered to update the UI when a user put "like" to a specific post. This operation is
    * successfull if the like is correctly store to the db, failed otherwise.
    */
    public interface AddLikeListener
    {
        /**
         * Triggered when after the db interrogation the like has successfully been added
         * @param message A message to show to the user
         */
        void onAddLikeSuccess(String message);

        /**
         * Triggered when after the db interrogation an error occurred while adding a like
         * @param message The error message to show to the user
         */
        void onAddLikeFailure(String message);
    }

    /**
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */
    private GetPostListener getPostListener;
    private AddLikeListener addLikeListener;
    private final String LOG_TAG = "@@ShowPosts_VM";

    @Override
    public void getPosts(final Context context)
    {
        //Definition of the listener that will be triggered when the db interrogation is finished
        final DatabaseManager.Listeners listeners = new DatabaseManager.Listeners();
        listeners.completeListener = new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                //This array will contain the posts downloaded from the db
                ArrayList<Post> posts = new ArrayList<>();

                //Loopping through the db interrogation's result
                for (QueryDocumentSnapshot item: task.getResult())
                {
                    //Each item inside the db interrogation's result is a map
                    //where the key identifies the db field to which is associated the corresponding value
                    Map<String, Object> postData = item.getData();

                    //Post attributes reading
                    String postID = item.getId();
                    String postTitle = postData.get(context.getString(R.string.postTitleKey)).toString();
                    String postMessage = postData.get(context.getString(R.string.postMessageKey)).toString();
                    String postAuthor = postData.get(context.getString(R.string.postAuthorKey)).toString();
                    int comments = Integer.valueOf(postData.get(context.getString(R.string.postCommentsKey)).toString());
                    int likes = Integer.valueOf(postData.get(context.getString(R.string.postLikesKey)).toString());

                    //The date read from the db is converted into a specific format stored in the static variable
                    //DATE_FORMAT so that in this way it can be directly present in the UI
                    SimpleDateFormat format = new SimpleDateFormat(Post.DATE_FORMAT);
                    String postDate = postData.get(context.getString(R.string.postDateKey)).toString();

                    try
                    {
                        //At this point, if no exception are thrown, a post object can be built and stored into the
                        //specific array
                        posts.add(new Post(postID, postTitle, postMessage, format.parse(postDate), likes, comments, postAuthor));
                    }
                    catch (ParseException e)
                    {
                        //If an error occurs while converting the post's date an error message is logged to console and 
                        //the failure UI listener is triggered with a explanation message for the user
                        Log.d(LOG_TAG, e.getMessage());
                        if (getPostListener != null){ getPostListener.onGetPostFailure(context.getString(R.string.requestFailedMessage)); }
                    }
                }

                //If everithing goes well the success listener can be triggered with the posts list as parameter.
                //In this way it can be used in the fragment to populate the UI
                if (getPostListener != null){ getPostListener.onGetPostsSuccess(posts); }
            }
        };

        //Calling to the method to get posts from the db passing the previously defined listener as parameter
        DatabaseManager.getPosts(context, listeners);
    }

    /**
     * Adds a like to the specified post
     * @param context The reference to the activity/fragment that has invoked this method
     * @param post The ID of the post to which the like will be added
     * @param user The user that has added the like
     * @param prevLikes The number of likes before the last addition
     */
    public void addLikeToPost(final Context context, String post, String user, int prevLikes)
    {
        //Definition of the listeners that will be triggered when the db interrogation is finished.
        //In particular two listeners are defined: one for a successful db interrogation, the other in case of failure
        DatabaseManager.Listeners listeners = new DatabaseManager.Listeners();

        listeners.successListener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                //If the like has correctly been added to the db the success listener is triggerd
                //(In this case there's an empty string as parameter because there's no specific message to show to the user)
                if (addLikeListener != null) { addLikeListener.onAddLikeSuccess(""); }
            }
        };

        listeners.failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                //If an error occurrs while storing a like into the db an error message is logged to console and
                //and the failure UI listener is trigger with an explanation message for the user
                Log.d(LOG_TAG, "Error: " + e.getMessage());
                if (addLikeListener != null) { addLikeListener.onAddLikeFailure(context.getString(R.string.addLikeOnFailureMessage)); }
            }
        };

        //Calling to the method to store a like into the db passing as parameter:
        //The context which references to the fragment, the post ID, the user that has put "like" to the post,
        //the amount of likes before the last one is added and the listeners previously defined
        DatabaseManager.addLike(context, post, user, prevLikes, listeners);
    }

    /**
     * Adds the specified listener to the view model
     * @param listener The specified listener that has to be added to the view model
     */
    public void setGetPostsListener(GetPostListener listener) { this.getPostListener = listener; }

    /**
     * Adds the specified listener to the view model
     * @param listener The specified listener that has to be added to the view model
     */
    public void setAddLikeListener(AddLikeListener listener) { this.addLikeListener = listener; }
}
