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
 * View model that contains all the logic needed to retrieve posts from the database
 * @author Leonardo Rossi g2
 */
public class ShowPosts_VM extends ViewModel implements ShowPostsMethods
{

    /**
     * ********************
     * **   LISTENERS    **
     * ********************
     */

    public interface GetPostListener
    {
        /**
         * Triggered when after the db interrogation all the posts are correctly parsed
         * @param posts The list of posts
         */
        void onGetPostsSuccess(ArrayList<Post> posts);

        /**
         * Triggered when after the db interrogation an error occurs while parsing the posts
         * @param message The error message
         */
        void onGetPostFailure(String message);

    }

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
        final DatabaseManager.Listeners listeners = new DatabaseManager.Listeners();
        listeners.completeListener = new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                ArrayList<Post> posts = new ArrayList<>();

                for (QueryDocumentSnapshot item: task.getResult())
                {
                    Map<String, Object> postData = item.getData();

                    String postID = item.getId();
                    String postTitle = postData.get(context.getString(R.string.postTitleKey)).toString();
                    String postMessage = postData.get(context.getString(R.string.postMessageKey)).toString();
                    String postAuthor = postData.get(context.getString(R.string.postAuthorKey)).toString();
                    int comments = Integer.valueOf(postData.get(context.getString(R.string.postCommentsKey)).toString());
                    int likes = Integer.valueOf(postData.get(context.getString(R.string.postLikesKey)).toString());

                    SimpleDateFormat format = new SimpleDateFormat(Post.DATE_FORMAT);
                    String postDate = postData.get(context.getString(R.string.postDateKey)).toString();

                    try
                    {
                        posts.add(new Post(postID, postTitle, postMessage, format.parse(postDate), likes, comments, postAuthor));
                    }
                    catch (ParseException e)
                    {
                        Log.d(LOG_TAG, e.getMessage());
                        if (getPostListener != null){ getPostListener.onGetPostFailure(context.getString(R.string.requestFailedMessage)); }
                    }
                }

                if (getPostListener != null){ getPostListener.onGetPostsSuccess(posts); }
            }
        };

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
        DatabaseManager.Listeners listeners = new DatabaseManager.Listeners();

        listeners.successListener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object o)
            {
                if (addLikeListener != null) { addLikeListener.onAddLikeSuccess(""); }
            }
        };

        listeners.failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d(LOG_TAG, "Error: " + e.getMessage());
                if (addLikeListener != null) { addLikeListener.onAddLikeFailure(context.getString(R.string.addLikeOnFailureMessage)); }
            }
        };

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
