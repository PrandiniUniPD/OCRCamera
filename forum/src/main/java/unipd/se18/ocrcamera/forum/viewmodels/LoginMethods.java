package unipd.se18.ocrcamera.forum.viewmodels;

public interface LoginMethods
{
    /**
     * Log a user to the forum
     * @param username The user's nickname
     * @param password The user's password
     */
    void loginToForum(String username, String password);
}
