package unipd.se18.ocrcamera.forum.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * A model that describes a post with all its data
 * @author Leonardo Rossi g2
 */
public class Post implements Parcelable
{
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    String ID;
    String title;
    String message;
    String author;
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
     * @param author The person who has written the post
     */
    public Post(String ID, String title, String message, Date date, int likes, int comments, String author)
    {
        this.ID = ID;
        this.title = title;
        this.message = message;
        this.date = date;
        this.likes = likes;
        this.comments = comments;
        this.author = author;
    }

    public Post(String title, String message, Date date, String author)
    {
        this("", title, message, date, 0, 0, author);
    }

    public Post(String ID)
    {
        this(ID, "", "", new Date(), 0, 0, "");
    }

    public Post() { this("", "", "", new Date(), 0, 0, ""); }

    /**
     * *************************
     * ** GETTER AND SETTERS  **
     * *************************
     */

    protected Post(Parcel in) {
        ID = in.readString();
        title = in.readString();
        message = in.readString();
        author = in.readString();
        likes = in.readInt();
        comments = in.readInt();
    }

    /**
     * Returns the post's ID
     * @return The post's ID
     */
    public String getID() { return ID; }

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
     * Returns the date in string format
     * @return The post's date converted to string
     */
    public String getDateInString() { return FORMATTER.format(date); }

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
     * Returns the post's author
     * @return The post's author
     */
    public String getAuthor() { return author; }

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

    /**
     * Sets the post's author to the specified value
     * @param author The specified author
     */
    public void setAuthor(String author) { this.author = author; }

    /**
     * ***********************
     * **   PUBLIC METHODS  **
     * ***********************
     */
    public void addLike() { likes++; }
    public void addComment() { comments++; }

    /**
     * *****************+***********
     * **   PARCELABLE METHODS    **
     * *****************************
     */


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(ID);
        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(author);
        dest.writeInt(likes);
        dest.writeInt(comments);
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
