package unipd.se18.ocrcamera;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Class used to test TestElement class
 * @author Luca Moroldo (g3)
 */
public class TestElementTest {

    private static final String testString = "{\"ingredients\": \"testIng1, testIng2 ,TestIng3\",\"tags\": [\"testTag1\", \"testTag2\", \"testTag3\"],\"notes\": \"testNote\",\"original_name\": \"testOriginalName.jpg\"}\n";
    private TestElement testElement;
    @Before
    public void init() {
        JSONObject jsonElement = null;
        try {
            jsonElement = new JSONObject(testString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        testElement = new TestElement(null, jsonElement, "test");

        try {
            testElement.setConfidence(1);
            testElement.setRecognizedText("testRecogText");
        }catch (JSONException e) {

        }
    }

    @Test
    public void getIngredientsArray() {
        try {
            String[] expected = {"testIng1", "testIng2" ,"TestIng3"};
            String[] actual = testElement.getIngredientsArray();


            assertArrayEquals(expected, actual);

        } catch (JSONException e) {

        }
    }

    @Test
    public void getIngredients() {
        try {
            String expected = "testIng1, testIng2 ,TestIng3";
            String actual = testElement.getIngredients();

            assertEquals(expected, actual);

        } catch (JSONException e) {

        }


    }

    @Test
    public void getTags() {
        try {
            String[] expected = {"testTag1", "testTag2" ,"testTag3"};
            String[] actual = testElement.getTags();


            assertArrayEquals(expected, actual);

        } catch (JSONException e) {

        }
    }


    @Test
    public void getFileName() {

       String expected = "test";
       String actual = testElement.getFileName();

       assertEquals(expected, actual);

    }

    @Test
    public void getNotes() {
        try {
            String expected = "testNote";
            String actual = testElement.getNotes();

            assertEquals(expected, actual);

        } catch (JSONException e) {

        }

    }

    @Test
    public void getConfidence() {
        try {
            float expected = 1;
            float actual = testElement.getConfidence();

            assertEquals(expected, actual, 0);

        } catch (JSONException e) {

        }
    }

    @Test
    public void getRecognizedText() {
        try {
            String expected = "testRecogText";
            String actual = testElement.getRecognizedText();

            assertEquals(expected, actual);

        } catch (JSONException e) {

        }
    }

}