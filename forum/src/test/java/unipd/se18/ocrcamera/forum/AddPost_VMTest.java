package unipd.se18.ocrcamera.forum;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import unipd.se18.ocrcamera.forum.viewmodels.AddPost_VM;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * AddPost_VM unit test, which will execute on the development machine (host).
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @author Pietro Prandini (g2)
 */
@RunWith(MockitoJUnitRunner.class)
public class AddPost_VMTest {
    /**
     * Trivial test of the enum JSON post keys
     */
    @Test
    public void enumValuesCheck() {
        // First parameter is the expected value, the second is the real value
        assertEquals("ID", AddPost_VM.JSONPostKey.ID.value);
        assertEquals("title", AddPost_VM.JSONPostKey.TITLE.value);
        assertEquals("message", AddPost_VM.JSONPostKey.MESSAGE.value);
        assertEquals("date", AddPost_VM.JSONPostKey.DATE.value);
        assertEquals("likes", AddPost_VM.JSONPostKey.LIKES.value);
        assertEquals("comments", AddPost_VM.JSONPostKey.COMMENTS.value);
        assertEquals("author", AddPost_VM.JSONPostKey.AUTHOR.value);
    }
}
