package unipd.se18.ocrcamera.forum;

import unipd.se18.ocrcamera.forum.viewmodels.AddPost_VM;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * AddPost_VM unit test, which will execute on the development machine (host).
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @author Pietro Prandini (g2)
 */
public class AddPost_VMTest {
    // Post keys for a JSON
    private String IDJSONKey;
    private String titleJSONKey;
    private String messageJSONKey;
    private String dateJSONKey;
    private String likesJSONKey;
    private String commentsJSONKey;
    private String authorJSONKey;

    /**
     * Sets up the environment for testing
     * @author Pietro Prandini (g2)
     */
    @Before
    public void setUpAddPostVMEnvironment() {
        // Initialization of the variables
        IDJSONKey = "ID";
        titleJSONKey = "title";
        messageJSONKey = "message";
        dateJSONKey = "date";
        likesJSONKey = "likes";
        commentsJSONKey = "comments";
        authorJSONKey = "author";
    }

    /**
     * Check enum value, ID
     * @author Pietro Prandini (g2)
     */
    @Test
    public void enumIDCheck() {
        assertEquals(IDJSONKey, AddPost_VM.JSONPostKey.ID.value);
    }

    /**
     * Check enum value, Title
     * @author Pietro Prandini (g2)
     */
    @Test
    public void enumTitleCheck() {
        assertEquals(titleJSONKey, AddPost_VM.JSONPostKey.TITLE.value);
    }

    /**
     * Check enum value, Message
     * @author Pietro Prandini (g2)
     */
    @Test
    public void enumMessageCheck() {
        assertEquals(messageJSONKey, AddPost_VM.JSONPostKey.MESSAGE.value);
    }

    /**
     * Check enum value, Date
     * @author Pietro Prandini (g2)
     */
    @Test
    public void enumDateCheck() {
        assertEquals(dateJSONKey, AddPost_VM.JSONPostKey.DATE.value);
    }

    /**
     * Check enum value, Likes
     * @author Pietro Prandini (g2)
     */
    @Test
    public void enumLikesCheck() {
        assertEquals(likesJSONKey, AddPost_VM.JSONPostKey.LIKES.value);
    }

    /**
     * Check enum value, Comments
     * @author Pietro Prandini (g2)
     */
    @Test
    public void enumCommentsCheck() {
        assertEquals(commentsJSONKey, AddPost_VM.JSONPostKey.COMMENTS.value);
    }

    /**
     * Check enum value, Author
     * @author Pietro Prandini (g2)
     */
    @Test
    public void enumAuthorCheck() {
        assertEquals(authorJSONKey, AddPost_VM.JSONPostKey.AUTHOR.value);
    }
}
