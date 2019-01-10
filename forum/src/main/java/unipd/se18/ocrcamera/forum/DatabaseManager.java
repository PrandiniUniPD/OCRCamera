package unipd.se18.ocrcamera.forum;

import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import unipd.se18.ocrcamera.forum.models.Post;

public class DatabaseManager
{
    /**
     * Listeners that can be called when the communication with the db has ended
     */
    public static class Listeners
    {
        public OnSuccessListener successListener;
        public OnFailureListener failureListener;
        public OnCompleteListener<QuerySnapshot> completeListener;
    }

    /**
     * Determines if the specified user can login into the forum
     * @param context The reference to the activity/fragment that has invoked this method
     * @param username The specified user's username
     * @param password The specified user's password
     * @param listeners The listeners that have to be executed when the communication with the database has ended
     */
    public static void loginUser(Context context, String username, String password, DatabaseManager.Listeners listeners)
    {
        //Get from the db the user with the specified username and password
        //When the query has ended the completeListener is called
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(context.getString(R.string.userCollectionName))
                .whereEqualTo(context.getString(R.string.usernameKey), username)
                .whereEqualTo(context.getString(R.string.passwordKey), password)
                .get()
                .addOnCompleteListener(listeners.completeListener);
    }

    /**
     * Adds the specified post into the database
     * @param context The reference to the activity/fragment that has invoked this method
     * @param post The post that has to be added into the database
     * @param listeners The listeners that have to be executed when the communication with the database has ended
     */
    public static void addPost(Context context, Post post, DatabaseManager.Listeners listeners)
    {
        //The post that has to be added is converted into a map
        Map<String, Object> toAdd = new HashMap<>();
        toAdd.put(context.getString(R.string.postTitleKey), post.getTitle());
        toAdd.put(context.getString(R.string.postMessageKey), post.getMessage());
        toAdd.put(context.getString(R.string.postDateKey), post.getDate());
        toAdd.put(context.getString(R.string.postAuthorKey), post.getAuthor());

        //Addition of the new post to the database
        //When the query has ended if the addition is successful then the successListener is called
        //Otherwise the failureListener is triggered
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(context.getString(R.string.postCollectionName))
                .add(toAdd)
                .addOnSuccessListener(listeners.successListener)
                .addOnFailureListener(listeners.failureListener);
    }

    /**
     * Gets the posts into the database
     * @param context The reference to the activity/fragment that has invoked this method
     * @param listeners The listeners that have to be executed when the communication with the database has ended
     */
    public static void getPosts(Context context, DatabaseManager.Listeners listeners)
    {
        //Get from the db the posts inserted by users
        //When the query has finished the completeListener is called
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(context.getString(R.string.postCollectionName))
                .whereEqualTo(context.getString(R.string.postCommentKey), null)
                .get()
                .addOnCompleteListener(listeners.completeListener);
    }

    /**
     * Adds a like to the specified post into the db
     * @param context The reference to the activity/fragment that has invoked this method
     * @param post The ID of the post to which the like will be added
     * @param user The user that has added the like
     * @param prevLikes The number of likes before the last addition
     * @param listeners The listeners that have to be executed when the communication with the database has ended
     */
    public static void addLike(Context context, String post, String user, int prevLikes, Listeners listeners)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Addition of the like to the db
        Map<String, Object> toAdd = new HashMap<>();
        toAdd.put("post", post);
        toAdd.put("user", user);

        db.collection(context.getString(R.string.likesCollectionName))
                .add(toAdd)
                .addOnSuccessListener(listeners.successListener)
                .addOnFailureListener(listeners.failureListener);

        //Incrementation of likes count to the specified post
        db.collection(context.getString(R.string.postCollectionName))
                .document(post)
                .update(context.getString(R.string.postLikesKey), prevLikes+1);
    }

    /**
     * Gets the detail of the specified post
     * @param context The reference to the activity/fragment that has invoked this method
     * @param post The ID of the specified post
     * @param listeners The listeners that have to be executed when the communication with the database has ended
     */
    public static void getPostDetail(Context context, String post, Listeners listeners)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Get the post detail from the db
        db.collection(context.getString(R.string.postCollectionName))
                .whereEqualTo(context.getString(R.string.postCommentKey), post)
                .get()
                .addOnCompleteListener(listeners.completeListener);
    }

    /**
     * Adds the specified comment to db
     * @param context The reference to the activity/fragment that has invoked this method
     * @param comment The comment that has to be added
     * @param prevComments The amount of comments before the last one's addition
     * @param post The ID of the post to which the comment is added
     * @param listeners The listeners that have to be executed when the communication with the database has ended
     */
    public static void addComment(Context context, Post comment, int prevComments, String post, Listeners listeners)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Add the comment to the db
        Map<String, Object> toAdd = new HashMap<>();
        toAdd.put(context.getString(R.string.postAuthorKey), comment.getAuthor());
        toAdd.put(context.getString(R.string.postMessageKey), comment.getMessage());
        toAdd.put(context.getString(R.string.postCommentKey), post);

        SimpleDateFormat format = new SimpleDateFormat(Post.DATE_FORMAT);
        toAdd.put(context.getString(R.string.postDateKey), format.format(comment.getDate()));

        db.collection(context.getString(R.string.postCollectionName))
                .add(toAdd)
                .addOnSuccessListener(listeners.successListener)
                .addOnFailureListener(listeners.failureListener);

        //Update comments amount
        db.collection(context.getString(R.string.postCollectionName))
                .document(post)
                .update(context.getString(R.string.postCommentsKey), prevComments+1);
    }

}
