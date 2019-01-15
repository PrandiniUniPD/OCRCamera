package unipd.se18.ocrcamera.forum;

import unipd.se18.ocrcamera.forum.models.Post;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;
import static unipd.se18.ocrcamera.forum.models.Post.DATE_FORMAT;

/**
 * Post unit test, which will execute on the development machine (host).
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @author Pietro Prandini (g2)
 */
public class PostTest {
    // Posts variables
    private String ID;
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
        ID = "";
        title = "Test";
        message = "Test message";
        date = convertDate(new Date());
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
        date = convertDate(new Date());
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

    /**
     * Converts the specified date to the specified format
     * @param date The date that has to be converted
     * @return The date in the new format
     */
    private Date convertDate(Date date)
    {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        String sDate = format.format(date);

        try
        {
            return format.parse(sDate);
        }
        catch (ParseException e)
        {
            return null;
        }
    }
}
