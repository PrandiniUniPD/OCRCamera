package unipd.se18.ocrcamera.forum;

import unipd.se18.ocrcamera.forum.viewmodels.AddPost_VM;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * AddPost_VM unit test, which will execute on the development machine (host).
 * (See also the instrumented test about the AddPost_VM class)
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @author Pietro Prandini (g2)
 */
public class AddPost_VMTest {
    // Post keys for a JSON
    private final String IDJSONKey = "ID";
    private final String titleJSONKey = "title";
    private final String messageJSONKey = "message";
    private final String dateJSONKey = "date";
    private final String likesJSONKey = "likes";
    private final String commentsJSONKey = "comments";
    private final String authorJSONKey = "author";

    // Default value of a post
    private final int defaultID = 0;
    private final int defaultLikes = 0;
    private final int defaultComments = 0;

    // Valid Strings for a new post
    private final String validTitle = "Valid test title";
    private final String validMessage = "Valid test message";
    private final String validAuthor = "Valid test author";

    // Parameters of the add post request
    private ArrayList<RequestManager.Parameter> postManagerParameters;

    // String generated from getJSONPost method
    private String generatedJSONPostToString;

    /**
     * Sets up the environment for testing
     * @author Pietro Prandini (g2)
     */
    @Before
    public void setUpAddPostVMEnvironment() throws JSONException {
        // Generates the parameters
        postManagerParameters =
                AddPost_VM.getAddPostParameters(validTitle,validMessage,validAuthor);

        // Generates a JSON post
        generatedJSONPostToString = AddPost_VM.getJSONPost(validTitle,validMessage,validAuthor);
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

    /**
     * Checks if the title of the JSON is the same as the title that is in the JSON generated post
     * @throws JSONException Exception of the JSON package {@link JSONException}
     */
    @Test
    public void checkJSONTitle() throws JSONException {
        JSONObject JSONPost = new JSONObject(generatedJSONPostToString);
        assertEquals(validTitle, JSONPost.getString(AddPost_VM.JSONPostKey.TITLE.value));
    }
}
