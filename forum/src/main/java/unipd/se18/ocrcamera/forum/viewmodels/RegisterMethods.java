package unipd.se18.ocrcamera.forum.viewmodels;

import android.content.Context;

/**
 * @author Alberto Valente (g2)
 */
public interface RegisterMethods
{
    /**
     * Registers a user to the forum by creating an account
     * @param context The reference of the activity/fragment that calls this method
     * @param username The user's nickname, which is the key field and has to be unique
     * @param password The user's password
     * @param name The user's real name
     * @param surname The user's real surname
     */
    void registerUserToForum(Context context, String username, String password, String name,
                             String surname);
}
