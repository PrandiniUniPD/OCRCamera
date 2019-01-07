package unipd.se18.ocrcamera.forum;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import unipd.se18.ocrcamera.forum.viewmodels.AddPost_VM;

import static org.junit.Assert.*;

/**
 * AddPost_VM instrumented test, which will execute on an Android device.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AddPost_VMInstrumentedTest {
    /*
    AddPost_VM Instrumented tests (ViewModel)
     */
    // Instance of AddPost ViewModel
    private AddPost_VM addPostViewModel;

    // Parameter for posting a new post
    private Context context;

    // Valid Strings for a new post
    private final String validTitle = "Valid test title";
    private final String validMessage = "Valid test message";
    private final String validAuthor = "Valid test author";

    /**
     * Sets up the environment for testing
     * @author Pietro Prandini (g2)
     */
    @Before
    public void setUpAddPostVMEnvironment() {
        // Initialization of the variables
        addPostViewModel = new AddPost_VM();
        context = InstrumentationRegistry.getTargetContext();
    }

    /*
    The next 26 tests use the
    addPostToForumWithInvalidParameters(String title, String message, String author) method
    of this class.
    It's considered 3 cases (valid, null, empty) for each parameter (title, message, author).
    So there is 27 (3*3*3) possible cases, 26 invalid cases and only 1 valid case.
     */

    /**
     * Test the method addPostToForum with invalid parameters
     * @param title The new post's title
     * @param message The new post's message
     * @param author The new post's author
     * @author Pietro Prandini (g2)
     */
    private void addPostToForumWithInvalidParameters(String title, String message, String author) {
        // Events listener of the AddPost ViewModel
        AddPost_VM.addPostListener addPostViewModelListener = new AddPost_VM.addPostListener() {
            @Override
            public void onPostAdded(String response) {
                // With invalid parameters the post shouldn't be added
                assertNotNull(response);
            }

            @Override
            public void onConnectionFailed(String error) {
                // With invalid parameters the connection shouldn't be established
                assertNull(error);
            }

            @Override
            public void onParametersSendingFailed(String error) {
                // With invalid parameters the parameters shouldn't be sent
                assertNull(error);
            }

            @Override
            public void onNotValidParameters(String error) {
                // With invalid parameters this method should be launched with an error String
                assertNotNull(error);
            }

            @Override
            public void onJSONPostCreationFailed(String error) {
                // With invalid parameters the JSON process shouldn't be started
                assertNull(error);
            }
        };

        // Sets the listener to the ViewModel
        addPostViewModel.setAddPostListener(addPostViewModelListener);

        // Tries to add an invalid post
        addPostViewModel.addPostToForum(context,title,message,author);
    }

    /**
     * Test the method addPostToForum with a null title String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullTitle() {
        addPostToForumWithInvalidParameters(null, validMessage, validAuthor);
    }

    /**
     * Test the method addPostToForum with a null message String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullMessage() {
        addPostToForumWithInvalidParameters(validTitle, null, validAuthor);
    }

    /**
     * Test the method addPostToForum with a null author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullAuthor() {
        addPostToForumWithInvalidParameters(validTitle, validMessage, null);
    }

    /**
     * Test the method addPostToForum with a null title String and a null message String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullTitleAndNullMessage() {
        addPostToForumWithInvalidParameters(null, null, validAuthor);
    }

    /**
     * Test the method addPostToForum with a null title String and a null author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullTitleAndNullAuthor() {
        addPostToForumWithInvalidParameters(null, validMessage, null);
    }

    /**
     * Test the method addPostToForum with a null message and a null author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullMessageAndNullAuthor() {
        addPostToForumWithInvalidParameters(validTitle, null, null);
    }

    /**
     * Test the method addPostToForum with a null title String and a null message
     * and a null author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullTitleAndNullMessageAndNullAuthor() {
        addPostToForumWithInvalidParameters(null, null, null);
    }

    /**
     * Test the method addPostToForum with an empty title String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyTitle() {
        addPostToForumWithInvalidParameters("", validMessage, validAuthor);
    }

    /**
     * Test the method addPostToForum with an empty message String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyMessage() {
        addPostToForumWithInvalidParameters(validTitle, "", validAuthor);
    }

    /**
     * Test the method addPostToForum with an empty author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyAuthor() {
        addPostToForumWithInvalidParameters(validTitle, validMessage, "");
    }

    /**
     * Test the method addPostToForum with an empty title String and an empty message String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyTitleAndEmptyMessage() {
        addPostToForumWithInvalidParameters("", "", validAuthor);
    }

    /**
     * Test the method addPostToForum with an empty title String and an empty author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyTitleAndEmptyAuthor() {
        addPostToForumWithInvalidParameters("", validMessage, "");
    }

    /**
     * Test the method addPostToForum with an empty message String and an empty author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumAndEmptyMessageAndEmptyAuthor() {
        addPostToForumWithInvalidParameters(validTitle, "", "");
    }

    /**
     * Test the method addPostToForum with an empty title String and an empty message String
     * and an empty author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyTitleAndEmptyMessageAndEmptyAuthor() {
        addPostToForumWithInvalidParameters("", "", "");
    }

    /**
     * Test the method addPostToForum with a null title String and an empty message String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullTitleAndEmptyMessage() {
        addPostToForumWithInvalidParameters(null, "", validAuthor);
    }

    /**
     * Test the method addPostToForum with a null title String and an empty author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullTitleAndEmptyAuthor() {
        addPostToForumWithInvalidParameters(null, validMessage, "");
    }

    /**
     * Test the method addPostToForum with an empty title String and a null message String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyTitleAndNullMessage() {
        addPostToForumWithInvalidParameters("", null, validAuthor);
    }

    /**
     * Test the method addPostToForum with a null message String and an empty author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullMessageAndEmptyAuthor() {
        addPostToForumWithInvalidParameters(validTitle, null, "");
    }

    /**
     * Test the method addPostToForum with an empty title String and a null author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyTitleAndNullAuthor() {
        addPostToForumWithInvalidParameters("", validMessage, null);
    }

    /**
     * Test the method addPostToForum with a null message String and a null author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyMessageAndNullAuthor() {
        addPostToForumWithInvalidParameters(validTitle, "", null);
    }

    /**
     * Test the method addPostToForum with a null title String and a null message String
     * and an empty author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullTitleAndNullMessageAndEmptyAuthor() {
        addPostToForumWithInvalidParameters(null, null, "");
    }

    /**
     * Test the method addPostToForum with an empty title String and a null message String
     * and a null author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyTitleAndNullMessageAndNullAuthor() {
        addPostToForumWithInvalidParameters("", null, null);
    }

    /**
     * Test the method addPostToForum with a null title String and an empty message String
     * and a null author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullTitleAndEmptyMessageAndNullAuthor() {
        addPostToForumWithInvalidParameters(null, "", null);
    }

    /**
     * Test the method addPostToForum with an empty title String and an empty message String
     * and a null author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyTitleAndEmptyMessageAndNullAuthor() {
        addPostToForumWithInvalidParameters("", "", null);
    }

    /**
     * Test the method addPostToForum with a null title String and an empty message String
     * and an empty author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullTitleAndEmptyMessageAndEmptyAuthor() {
        addPostToForumWithInvalidParameters(null, "", "");
    }

    /**
     * Test the method addPostToForum with an empty title String and a null message String
     * and an empty author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyTitleAndNullMessageAndEmptyAuthor() {
        addPostToForumWithInvalidParameters("", null, "");
    }
}
