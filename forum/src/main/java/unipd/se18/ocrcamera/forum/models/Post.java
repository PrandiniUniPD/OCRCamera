package unipd.se18.ocrcamera.forum.models;

import java.util.Date;

public class Post
{
    int ID;
    String title;
    String message;
    Date date;

    /**
     * Constructs an object of type Post
     * @param ID The post's ID
     * @param title The post's title
     * @param message The post's message
     * @param date The post's date
     */
    public Post(int ID, String title, String message, Date date)
    {
        this.ID = ID;
        this.title = title;
        this.message = message;
        this.date = date;
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
