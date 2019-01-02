package unipd.se18.ocrcamera.forum.viewmodels;

/**
 * @author Leonardo Rosi g2
 */
public interface AddPostsMethods
{
    /**
     * Adds a new post to the forum
     * @param title The new post's title
     * @param message The new post's message
     */
    void addPostToForum(String title, String message);
}
