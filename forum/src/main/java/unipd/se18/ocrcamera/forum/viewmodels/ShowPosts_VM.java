package unipd.se18.ocrcamera.forum.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        //Definition of the network request parameters
        ArrayList<RequestManager.Parameter> parameters = new ArrayList<>();
        parameters.add(new RequestManager.Parameter("c", RequestManager.RequestType.GET_POSTS.value));

        RequestManager manager = new RequestManager();

        //Implementation of the RequestManager listener
        manager.setOnRequestFinishedListener(new RequestManager.RequestManagerListener()
        {
            @Override
            public void onRequestFinished(String response)
            {
                try
                {
                    //Definition of a temporary array which will contain the posts retrieved
                    //from the network request that has just ended
                    ArrayList<Post> posts = new ArrayList<>();

                    //The network request sends back a response in JSON format
                    //So now it has to be read and convert into usable data
                    JSONArray jPosts = new JSONArray(response);

                    for (int i = 0; i < jPosts.length(); i++)
                    {
                        JSONObject jPost = jPosts.getJSONObject(i);

                        //Get post's attributes
                        int ID = jPost.getInt(context.getString(R.string.jPostIDField));
                        String title = jPost.getString(context.getString(R.string.jPostTitleField));
                        String message = jPost.getString(context.getString(R.string.jPostMessageField));
                        int likes = jPost.getInt(context.getString(R.string.jPostLikesField));
                        int comments = jPost.getInt(context.getString(R.string.jPostCommentsField));
                        String author = jPost.getString(context.getString(R.string.jPostAuthorField));
                        SimpleDateFormat format = new SimpleDateFormat(Post.DATE_FORMAT);
                        Date date = format.parse(jPost.getString(context.getString(R.string.jPostDateField)));

                        //Add the converted post to the temporary array
                        posts.add(new Post(ID, title, message, date, likes, comments, author));
                    }

                    //The live data is triggered so that the UI can be correctly update
                    if (listener != null) { listener.onGetPostsSuccess(posts); }
                }
                catch (JSONException e)
                {
                    if (listener != null) { listener.onGetPostFailure(e.getMessage()); }
                }
                catch (ParseException e)
                {
                    if (listener != null) { listener.onGetPostFailure(e.getMessage()); }
                }
            }

            @Override
            public void onConnectionFailed(String message)
            {
                Log.d(LOG_TAG, message);
                if (listener != null) { listener.onGetPostFailure(context.getString(R.string.requestFailedMessage)); }
            }

            @Override
            public void onParametersSendingFailed(String message)
            {
                Log.d(LOG_TAG, message);
                if (listener != null) { listener.onGetPostFailure(context.getString(R.string.requestFailedMessage)); }
            }
        });

        //Sending of a network request to get the posts from the forum
        manager.sendRequest(context, parameters);

    }

    /**
     * Adds the specified listener to the view model
     * @param listener The specified listener that has to be added to the view model
     */
    public void setUIComunicatorListener(UIComunicator listener) { this.listener = listener; }
}
