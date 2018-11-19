package unipd.se18.ocrcamera;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Class used for testing Utils.java
 * @author Luca Moroldo (g3)
 */
public class UtilsTest {

    @Test
    public void getFileExtension() {
        String path = "testDir/testImage.jpeg";
        String expected = "jpeg";
        String actual = Utils.getFileExtension(path);
        assertEquals(expected, actual);
    }

    @Test
    public void getFilePrefix() {
        String path = "testDir/testImage.jpeg";
        String expected = "testImage";
        String actual = Utils.getFilePrefix(path);
        assertEquals(expected, actual);

        path = "home/luca/AndroidStudioProjects/OCRCamera/app/build/intermediates/sourceFolderJavaResources/test/debug/PhotoTesterTestDir/foto11.txt";
        expected = "foto11";
        actual = Utils.getFilePrefix(path);
        assertEquals(expected, actual);

        path = "foto.txt";
        expected = "foto";
        actual = Utils.getFilePrefix(path);
        assertEquals(expected, actual);
    }


    @Test
    public void getStringArrayFromJSON() {
        try {
            JSONObject jsonObject = new JSONObject("{\"tags\": [\"testTag1\", \"testTag2\", \"testTag3\"]}");

            String[] expected = {"testTag1", "testTag2", "testTag3"};
            String[] actual = Utils.getStringArrayFromJSON(jsonObject, "tags");

            assertArrayEquals(expected, actual);

        } catch (JSONException e) {

        }

    }

    @Test
    public void getTextFromFile() {

        String path = this.getClass().getClassLoader().getResource("getTextFromFileTest.txt").getPath();
        String expected = "this is a test";
        String actual = Utils.getTextFromFile(path);
        assertEquals(expected, actual);
    }
}