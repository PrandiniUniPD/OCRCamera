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
 * Forum instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ForumInstrumentedTest {
    /*
    AddPost_VM Instrumented tests (ViewModel)
     */
    // Instance of AddPost ViewModel
    private AddPost_VM addPostViewModel;

    // Parameter for posting a new post
    private Context context;

    // Useful for test - Array of Strings' indexes used for the parameters for addPostToForum method
    private final int titleIndex = 0;
    private final int messageIndex = 1;
    private final int authorIndex = 2;

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

    /**
     * Test the method addPostToForum with a null title String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullTitle() {
        // Preparing the title, the message and the author Strings
        String[] parameters = new String[3];
        parameters[titleIndex] = null;
        parameters[messageIndex] = "Valid test message";
        parameters[authorIndex] = "Valid author";

        // Test the functionality
        addPostToForumWithInvalidParameters(parameters);
    }

    /**
     * Test the method addPostToForum with an empty String for title
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyTitle() {
        // Preparing the title, the message and the author Strings
        String[] parameters = new String[3];
        parameters[titleIndex] = "";
        parameters[messageIndex] = "Valid test message";
        parameters[authorIndex] = "Valid author";

        // Test the functionality
        addPostToForumWithInvalidParameters(parameters);
    }

    /**
     * Test the method addPostToForum with a null message String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullMessage() {
        // Preparing the title, the message and the author Strings
        String[] parameters = new String[3];
        parameters[titleIndex] = "Valid title";
        parameters[messageIndex] = null;
        parameters[authorIndex] = "Valid author";

        // Test the functionality
        addPostToForumWithInvalidParameters(parameters);
    }

    /**
     * Test the method addPostToForum with an empty String for message
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyMessage() {
        // Preparing the title, the message and the author Strings
        String[] parameters = new String[3];
        parameters[titleIndex] = "Valid title";
        parameters[messageIndex] = "";
        parameters[authorIndex] = "Valid author";

        // Test the functionality
        addPostToForumWithInvalidParameters(parameters);
    }

    /**
     * Test the method addPostToForum with a null author String
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumNullAuthor() {
        // Preparing the title, the message and the author Strings
        String[] parameters = new String[3];
        parameters[titleIndex] = "Valid title";
        parameters[messageIndex] = "Valid test message";
        parameters[authorIndex] = null;

        // Test the functionality
        addPostToForumWithInvalidParameters(parameters);
    }

    /**
     * Test the method addPostToForum with an empty String for author
     * @author Pietro Prandini (g2)
     */
    @Test
    public void addPostToForumEmptyAuthor() {
        // Preparing the title, the message and the author Strings
        String[] parameters = new String[3];
        parameters[titleIndex] = "Valid title";
        parameters[messageIndex] = "Valid test message";
        parameters[authorIndex] = "";

        // Test the functionality
        addPostToForumWithInvalidParameters(parameters);
    }

    /**
     * Test the method addPostToForum with invalid parameters
     * @param parameters The array of Strings that contains the title, the message and the author
     * @author Pietro Prandini (g2)
     */
    private void addPostToForumWithInvalidParameters(String[] parameters) {
        // Parameters to send to the addPostToForum method
        String title = parameters[titleIndex];
        String message = parameters[messageIndex];
        String author = parameters[authorIndex];

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
}
