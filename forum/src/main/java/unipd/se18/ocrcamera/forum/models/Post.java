package unipd.se18.ocrcamera.forum.models;

import java.util.Date;
import java.util.HashMap;

/**
 * @author Leonardo Rossi g2
 */
public class Post
{
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    int ID;
    String title;
    String message;
    Date date;
    int likes;
    int comments;

    /**
     * Constructs an object of type Post
     * @param ID The post's ID
     * @param title The post's title
     * @param message The post's message
     * @param date The post's date
     * @param likes The post's number of likes
     * @param comments The post's number of comments
     */
    public Post(int ID, String title, String message, Date date, int likes, int comments)
    {
        this.ID = ID;
        this.title = title;
        this.message = message;
        this.date = date;
        this.likes = likes;
        this.comments = comments;
    }

    /**
     * *************************
     * ** GETTER AND SETTERS  **
     * *************************
     */

    /**
     * Returns the post's ID
     * @return The post's ID
     */
    public int getID() { return ID; }

    /**
     * Returns the post's title
     * @return The post's title
     */
    public String getTitle() { return title; }

    /**
     * Returns the post's message
     * @return The post's message
     */
    public String getMessage() { return message; }

    /**
     * Returns the post's date
     * @return The post's date
     */
    public Date getDate() { return date; }

    /**
     * Returns the post's number of comments
     * @return The post's number of comments
     */
    public int getComments() { return comments; }

    /**
     * Returns the post's number of likes
     * @return The post's number of likes
     */
    public int getLikes() { return likes; }

    /**
     * Sets the post's title to the specified value
     * @param title The specified value
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Sets the post's message to the specified value
     * @param message The specified value
     */
    public void setMessage(String message) { this.message = message; }

    /**
     * Sets the post's date to the specified value
     * @param date The specified value
     */
    public void setDate(Date date) { this.date = date; }

}
