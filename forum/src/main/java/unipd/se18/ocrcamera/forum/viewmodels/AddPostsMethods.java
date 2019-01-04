package unipd.se18.ocrcamera.forum.viewmodels;

import android.content.Context;

/**
 * @author Leonardo Rosi g2
 */
public interface AddPostsMethods
{
    /**
     * Adds a new post to the forum
     * @param context The reference of the activity/fragment that calls this method
     * @param title The new post's title
     * @param message The new post's message
     */
    void addPostToForum(Context context, String title, String message);
}
