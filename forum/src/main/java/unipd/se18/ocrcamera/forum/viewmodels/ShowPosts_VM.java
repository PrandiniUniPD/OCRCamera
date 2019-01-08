package unipd.se18.ocrcamera.forum.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
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
     * *******************
     * **   LISTENER    **
     * *******************
     */
    public interface UIComunicator
    {
        /**
         * Triggered when after the network response all the posts are correctly parsed
         * @param posts The list of posts
         */
        void onGetPostsSuccess(ArrayList<Post> posts);

        /**
         * Triggered when after the network response an error occurs while parsing the posts
         * @param message The error message
         */
        void onGetPostFailure(String message);
    }

    /**
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */
    private UIComunicator listener;
    private final String LOG_TAG = "@@ShowPosts_VM";

    @Override
    public void getPosts(final Context context)
    {
        final DatabaseManager.Listners listeners = new DatabaseManager.Listners();
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
                    String postTitle = postData.get("title").toString();
                    String postMessage = postData.get("message").toString();
                    String postAuthor = postData.get("author").toString();
                    int comments = Integer.valueOf(postData.get("comments").toString());
                    int likes = Integer.valueOf(postData.get("likes").toString());

                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    String postDate = postData.get("date").toString();

                    try
                    {
                        posts.add(new Post(postID, postTitle, postMessage, format.parse(postDate), likes, comments, postAuthor));
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }

                if (listener != null){ listener.onGetPostsSuccess(posts); }
            }
        };

        DatabaseManager.getPosts(listeners);
    }

    /**
     * Adds the specified listener to the view model
     * @param listener The specified listener that has to be added to the view model
     */
    public void setUIComunicatorListener(UIComunicator listener) { this.listener = listener; }
}
