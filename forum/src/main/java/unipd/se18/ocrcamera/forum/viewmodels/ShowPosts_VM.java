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

public class ShowPosts_VM extends ViewModel implements ShowPostsMethods
{

    /**
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */
    public MutableLiveData<ArrayList<Post>> livePosts = new MutableLiveData<>();
    public MutableLiveData<String> liveError = new MutableLiveData<>();

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
                        SimpleDateFormat format = new SimpleDateFormat(Post.DATE_FORMAT);
                        Date date = format.parse(jPost.getString(context.getString(R.string.jPostDateField)));

                        //Add the converted post to the temporary array
                        posts.add(new Post(ID, title, message, date, likes, comments));
                    }

                    //The live data is triggered so that the UI can be correctly update
                    livePosts.setValue(posts);
                }
                catch (JSONException e)
                {
                    liveError.setValue(e.getMessage());
                }
                catch (ParseException e)
                {
                    liveError.setValue(e.getMessage());
                }
            }

            @Override
            public void onConnectionFailed(String message)
            {
                Log.d(LOG_TAG, message);
                liveError.setValue(context.getString(R.string.requestFailedMessage));
            }

            @Override
            public void onParametersSendingFailed(String message)
            {
                Log.d(LOG_TAG, message);
                liveError.setValue(context.getString(R.string.requestFailedMessage));
            }
        });

        //Sending of a network request to get the posts from the forum
        manager.sendRequest(context, parameters);

    }
}
