package unipd.se18.ocrcamera.forum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import unipd.se18.ocrcamera.forum.DatabaseManager;
import unipd.se18.ocrcamera.forum.R;
import unipd.se18.ocrcamera.forum.models.Post;

/**
 * View model that contains all the logic needed to retrieve a post's detail from the database
 * @author Leonardo Rossi g2
 */
public class PostDetail_VM extends ViewModel implements PostDetailMethods
{
    /**
     * ********************
     * **   LISTENERS    **
     * ********************
     */

    /**
     * This listener is triggered to update the UI with the result of a db interrogation to get a post detail.
     * If the result is successful, that is all the detail is correctly downloaded, the PostDetail fragment receives the
     * comments list, otherwise it receives an error message that can be shown to the user
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
     * This listener is triggered to update the UI when a user add a comment to a specific post. This operation is
     * successful if the comment is correctly store into the db, failed otherwise.
     */
    public interface AddCommentListener
    {
        /**
         * Triggered when a comment is successfully added
         * @param comment The comment that has just been added
         */
        void onAddCommentSuccess(Post comment);

        /**
         * Triggered when a comment addition fails
         * @param message The error message
         */
        void onAddCommentFailure(String message);
    }

    /**
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */
    private GetPostDetailListener getDetailListener;
    private AddCommentListener addCommentListener;
    private final String LOG_TAG = "@@PostDetail_VM";
    public String postID = "";

    @Override
    public void getPostDetail(final Context context, final String post)
    {
        //Definition of the listener that will be triggered when the db interrogation is finished
        final DatabaseManager.Listeners listeners = new DatabaseManager.Listeners();
        listeners.completeListener = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {

                if (task.isSuccessful())
                {
                    //This array contains all the comments related to the specified post
                    ArrayList<Post> comments = new ArrayList<>();

                    //Loop through the db interrogation's result
                    for (QueryDocumentSnapshot item : task.getResult())
                    {
                        //Each item inside the db interrogation's result is a map
                        //where the key identifies the db field to which is associated the corresponding value
                        Map<String, Object> itemData = item.getData();

                        //Comment attributes reading
                        String commentID = item.getId();
                        String message = itemData.get(context.getString(R.string.postMessageKey)).toString();
                        String author = itemData.get(context.getString(R.string.postAuthorKey)).toString();
                        String postDate = itemData.get(context.getString(R.string.postDateKey)).toString();

                        //At this point a comment is built with all its data
                        Post comment = new Post(commentID);
                        comment.setMessage(message);
                        comment.setAuthor(author);
                        try
                        {
                            comment.setDate(Post.FORMATTER.parse(postDate));
                            comments.add(comment);
                        }
                        catch (ParseException e)
                        {
                            //If an error occurs while converting the comment's date an error message is logged to console and
                            //the failure UI listener is triggered with a explanation message for the user
                            Log.d(LOG_TAG, e.getMessage());
                            if (getDetailListener !=  null){ getDetailListener.onGetDetailFailure(context.getString(R.string.getPostDetailFailure)); }
                        }
                    }

                    //If everything goes well the success listener is triggered
                    if (getDetailListener != null) { getDetailListener.onGetDetailSuccess(comments); }

                }
            }
        };

        //The method to retrieve a post detail is invoked
        DatabaseManager.getPostDetail(context, post, listeners);
    }

    /**
     * Adds the specified comment to the current post
     * @param context The reference to the activity/fragment that has invoked this method
     * @param comment The comment that has to be added to the current post
     * @param prevComments The amount of comments before the last one's addition
     */
    public void addComment(final Context context, final Post comment, int prevComments)
    {
        //Definition of the listener that will be triggered when the db interrogation is finished
        DatabaseManager.Listeners listeners = new DatabaseManager.Listeners();
        listeners.successListener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object o)
            {
                //If the comment has been successfully added into the database the corresponding
                //listener is triggered so that the UI can be updated with the last comment added
                if (addCommentListener != null) { addCommentListener.onAddCommentSuccess(comment); }
            }
        };

        listeners.failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                //If something goes wrong while adding a comment an error message is print to the console
                //and the failure listener is triggered with an explanation message for the user
                Log.d(LOG_TAG, e.getMessage());
                if (addCommentListener != null) { addCommentListener.onAddCommentFailure(context.getString(R.string.addCommentFailure)); }
            }
        };

        //The method to add a comment to the db is invoked
        DatabaseManager.addComment(context, comment, prevComments, postID, listeners);
    }

    /**
     * Adds the specified listener to the view model
     * @param listener The specified listener that has to be added to the view model
     */
    public void setGetDetailListener(GetPostDetailListener listener) { this.getDetailListener = listener; }

    /**
     * Adds the specified listener to the view model
     * @param listener The specified listener that has to be added to the view model
     */
    public void setAddCommentListener(AddCommentListener listener) { this.addCommentListener = listener; }
}
