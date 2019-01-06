package unipd.se18.ocrcamera.forum;

import unipd.se18.ocrcamera.forum.models.Post;
import unipd.se18.ocrcamera.forum.viewmodels.AddPost_VM;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * AddPost_VM unit test, which will execute on the development machine (host).
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @author Pietro Prandini (g2)
 */
public class ForumTest {
    /*
    Post tests (Model)
     */
    // Posts variables
    private int ID;
    private String title;
    private String message;
    private Date date;
    private int likes;
    private int comments;
    private String author;
    private Post post;

    /**
     * Sets up the environment for the model tests
     * @author Pietro Prandini (g2)
     */
    @Before
    public void setUpPost() {
        // Assigns values to the variable for creating a new post
        ID = 0;
        title = "Test";
        message = "Test message";
        date = new Date();
        likes = 0;
        comments = 0;
        author = "Developer";

        // Creates a new post
        post = new Post(ID,title,message,date,likes,comments,author);
    }

    /**
     * Post.java (model), getID method test
     * @author Pietro Prandini (g2)
     */
    @Test
    public void getIDTest() {
        assertEquals(ID,post.getID());
    }

    /**
     * Post.java (model), getTitle method test
     * @author Pietro Prandini (g2)
     */
    @Test
    public void getTitleTest() {
        assertEquals(title,post.getTitle());
    }

    /**
     * Post.java (model), getMessage method test
     * @author Pietro Prandini (g2)
     */
    @Test
    public void getMessageTest() {
        assertEquals(message,post.getMessage());
    }

    /**
     * Post.java (model), getDate method test
     * @author Pietro Prandini (g2)
     */
    @Test
    public void getDateTest() {
        assertEquals(date,post.getDate());
    }

    /**
     * Post.java (model), getComments method test
     * @author Pietro Prandini (g2)
     */
    @Test
    public void getCommentsTest() {
        assertEquals(comments,post.getComments());
    }

    /**
     * Post.java (model), getLikes method test
     * @author Pietro Prandini (g2)
     */
    @Test
    public void getLikesTest() {
        assertEquals(likes,post.getLikes());
    }

    /**
     * Post.java (model), getAuthor method test
     * @author Pietro Prandini (g2)
     */
    @Test
    public void getAuthorTest() {
        assertEquals(author,post.getAuthor());
    }

    /**
     * Post.java (model), setTitle method test
     * @author Pietro Prandini (g2)
     */
    @Test
    public void setTitleTest() {
        title = "New Test";
        post.setTitle(title);
        assertEquals(title,post.getTitle());
    }

    /**
     * Post.java (model), setMessage method test
     * @author Pietro Prandini (g2)
     */
    @Test
    public void setMessageTest() {
        message = "New test message";
        post.setMessage(message);
        assertEquals(message,post.getMessage());
    }

    /**
     * Post.java (model), setDate method test
     * @author Pietro Prandini (g2)
     */
    @Test
    public void setDateTest() {
        date = new Date();
        post.setDate(date);
        assertEquals(date,post.getDate());
    }

    /**
     * Post.java (model), setAuthor method test
     * @author Pietro Prandini (g2)
     */
    @Test
    public void setAuthorTest() {
        author = "Same developer";
        post.setAuthor(author);
        assertEquals(author,post.getAuthor());
    }

    /*
    AddPost_VM tests (ViewModel)
     */
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
    public void setUpJSONKeys() {
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
