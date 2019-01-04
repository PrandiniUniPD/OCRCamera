package unipd.se18.ocrcamera.forum.viewmodels;

import android.content.Context;

/**
 * @author Leonardo Rossi g2
 */
public interface LoginMethods
{
    /**
     * Log a user to the forum
     * @param context The reference of the activity/fragment that calls this method
     * @param username The user's nickname
     * @param password The user's password
     */
    void loginToForum(Context context, String username, String password);
}
