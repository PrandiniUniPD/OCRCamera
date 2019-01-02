package unipd.se18.ocrcamera.forum.viewmodels;

import android.content.Context;

/**
 * @author Leonardo Rossi g2
 */
public interface ShowPostsMethods
{
    /**
     * Retrieves the posts in the forum
     * @param context The reference of the activity/fragment that calls this method
     */
    void getPosts(Context context);
}
