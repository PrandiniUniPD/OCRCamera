package unipd.se18.ocrcamera.forum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import unipd.se18.ocrcamera.forum.DatabaseManager;
import unipd.se18.ocrcamera.forum.R;
import unipd.se18.ocrcamera.forum.models.Post;

public class PostDetail_VM extends ViewModel implements PostDetailMethods
{
    /**
     * ********************
     * **   LISTENERS    **
     * ********************
     */

    public interface GetPostDetailListener
    {
        /**
         * Triggered when the db interrogation is successfully finished
         * @param comments The list of comments
         */
        void onGetDetailSuccess(ArrayList<Post> comments);

        /**
         * Triggered when an error occurred while getting post detail
         * @param message The error message
         */
        void onGetDetailFailure(String message);
    }

    /**
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */
    private GetPostDetailListener listener;
    private final String LOG_TAG = "@@PostDetail_VM";

    @Override
    public void getPostDetail(final Context context, final String post)
    {
        final DatabaseManager.Listeners listeners = new DatabaseManager.Listeners();
        listeners.completeListener = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    ArrayList<Post> comments = new ArrayList<>();

                    for (QueryDocumentSnapshot item : task.getResult())
                    {
                        Map<String, Object> itemData = item.getData();

                        String commentID = item.getId();
                        String message = itemData.get(context.getString(R.string.postMessageKey)).toString();
                        String author = itemData.get(context.getString(R.string.postAuthorKey)).toString();

                        SimpleDateFormat format = new SimpleDateFormat(Post.DATE_FORMAT);
                        String postDate = itemData.get(context.getString(R.string.postDateKey)).toString();

                        Post comment = new Post(commentID);
                        comment.setMessage(message);
                        comment.setAuthor(author);
                        try
                        {
                            comment.setDate(format.parse(postDate));
                            comments.add(comment);
                        }
                        catch (ParseException e)
                        {
                            Log.d(LOG_TAG, e.getMessage());
                            if (listener !=  null){ listener.onGetDetailFailure(context.getString(R.string.getPostDetailFailure)); }
                        }
                    }

                    if (listener != null) { listener.onGetDetailSuccess(comments); }

                }
            }
        };

        DatabaseManager.getPostDetail(context, post, listeners);
    }

    /**
     * Adds the specified listener to the view model
     * @param listener The specified listener that has to be added to the view model
     */
    public void setGetDetailListener(GetPostDetailListener listener) { this.listener = listener; }
}
