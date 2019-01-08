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
     * Checks enum value, ID
     * @author Pietro Prandini (g2)
     */
    @Test
    public void enumIDCheck() {
        assertEquals(IDJSONKey, AddPost_VM.JSONPostKey.ID.value);
    }

    /**
     * Checks enum value, Title
     * @author Pietro Prandini (g2)
     */
    @Test
    public void enumTitleCheck() {
        assertEquals(titleJSONKey, AddPost_VM.JSONPostKey.TITLE.value);
    }

    /**
     * Checks enum value, Message
     * @author Pietro Prandini (g2)
     */
    @Test
    public void enumMessageCheck() {
        assertEquals(messageJSONKey, AddPost_VM.JSONPostKey.MESSAGE.value);
    }

    /**
     * Checks enum value, Date
     * @author Pietro Prandini (g2)
     */
    @Test
    public void enumDateCheck() {
        assertEquals(dateJSONKey, AddPost_VM.JSONPostKey.DATE.value);
    }

    /**
     * Checks enum value, Likes
     * @author Pietro Prandini (g2)
     */
    @Test
    public void enumLikesCheck() {
        assertEquals(likesJSONKey, AddPost_VM.JSONPostKey.LIKES.value);
    }

    /**
     * Checks enum value, Comments
     * @author Pietro Prandini (g2)
     */
    @Test
    public void enumCommentsCheck() {
        assertEquals(commentsJSONKey, AddPost_VM.JSONPostKey.COMMENTS.value);
    }

    /**
     * Checks enum value, Author
     * @author Pietro Prandini (g2)
     */
    @Test
    public void enumAuthorCheck() {
        assertEquals(authorJSONKey, AddPost_VM.JSONPostKey.AUTHOR.value);
    }

    /**
     * Checks the key of the add Post request
     */
    @Test
    public void checkRequestAddPostKey() {
        RequestManager.Parameter addPostRequest = postManagerParameters.get(0);
        assertEquals(AddPost_VM.KEY_ADD_POST_REQUEST,addPostRequest.key);
    }

    /**
     * Checks the value of the add Post request
     */
    @Test
    public void checkRequestAddPostValue() {
        RequestManager.Parameter addPostValue = postManagerParameters.get(0);
        assertEquals(RequestManager.RequestType.ADD_POST.value,addPostValue.value);
    }

    /**
     * Checks the key of the JSON post parameter
     */
    @Test
    public void checkRequestJSONPostKey() {
        RequestManager.Parameter JSONPostParameter = postManagerParameters.get(1);
        assertEquals(AddPost_VM.KEY_JSON_POST_CONTENT,JSONPostParameter.key);
    }

    /**
     * Checks the title value of the JSON post parameter
     * @throws JSONException Exception of the JSON package {@link JSONException}
     */
    @Test
    public void checkRequestJSONPostValueTitle() throws JSONException {
        RequestManager.Parameter JSONPostParameter = postManagerParameters.get(1);
        JSONObject JSONPost = new JSONObject(JSONPostParameter.value);
        assertEquals(validTitle, JSONPost.getString(AddPost_VM.JSONPostKey.TITLE.value));
    }

    /**
     * Checks the message value of the JSON post parameter
     * @throws JSONException Exception of the JSON package {@link JSONException}
     */
    @Test
    public void checkRequestJSONPostValueMessage() throws JSONException {
        RequestManager.Parameter JSONPostParameter = postManagerParameters.get(1);
        JSONObject JSONPost = new JSONObject(JSONPostParameter.value);
        assertEquals(validMessage, JSONPost.getString(AddPost_VM.JSONPostKey.MESSAGE.value));
    }

    /**
     * Checks the author value of the JSON post parameter
     * @throws JSONException Exception of the JSON package {@link JSONException}
     */
    @Test
    public void checkRequestJSONPostValueAuthor() throws JSONException {
        RequestManager.Parameter JSONPostParameter = postManagerParameters.get(1);
        JSONObject JSONPost = new JSONObject(JSONPostParameter.value);
        assertEquals(validAuthor, JSONPost.getString(AddPost_VM.JSONPostKey.AUTHOR.value));
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

    /**
     * Checks if the message of the JSON is the same as the message
     * that is in the JSON generated post
     * @throws JSONException Exception of the JSON package {@link JSONException}
     */
    @Test
    public void checkJSONMessage() throws JSONException {
        JSONObject JSONPost = new JSONObject(generatedJSONPostToString);
        assertEquals(validMessage, JSONPost.getString(AddPost_VM.JSONPostKey.MESSAGE.value));
    }

    /**
     * Checks if the author of the JSON is the same as the author that is in the JSON generated post
     * @throws JSONException Exception of the JSON package {@link JSONException}
     */
    @Test
    public void checkJSONAuthor() throws JSONException {
        JSONObject JSONPost = new JSONObject(generatedJSONPostToString);
        assertEquals(validAuthor, JSONPost.getString(AddPost_VM.JSONPostKey.AUTHOR.value));
    }
}
