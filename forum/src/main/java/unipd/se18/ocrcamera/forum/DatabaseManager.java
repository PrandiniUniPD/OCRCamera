package unipd.se18.ocrcamera.forum;

import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import unipd.se18.ocrcamera.forum.models.Post;

public class DatabaseManager
{
    /**
     * Listeners that can be called when the communication with the db has ended
     */
    public static class Listners
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
    public static void loginUser(Context context, String username, String password, DatabaseManager.Listners listeners)
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
    public static void addPost(Context context, Post post, DatabaseManager.Listners listeners)
    {
        //The post that has to be added is converted into a map
        Map<String, Object> toAdd = new HashMap<>();
        toAdd.put(context.getString(R.string.titleKey), post.getTitle());
        toAdd.put(context.getString(R.string.messageKey), post.getMessage());
        toAdd.put(context.getString(R.string.dateKey), post.getDate());
        toAdd.put(context.getString(R.string.authorKey), post.getAuthor());

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
    public static void getPosts(Context context, DatabaseManager.Listners listeners)
    {
        //Get from the db the posts inserted by users
        //When the query has finished the completeListener is called
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(context.getString(R.string.postCollectionName))
                .get()
                .addOnCompleteListener(listeners.completeListener);
    }

}
