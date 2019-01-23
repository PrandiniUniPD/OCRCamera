package unipd.se18.ocrcamera.performancetester;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import unipd.se18.ocrcamera.performancetester.TestElement;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Class used to test TestElement class
 * @author Luca Moroldo (g3)
 */
public class TestElementTest {

    //fake JSON data used to test TestElement class
    private static final String testString = "{\n" +
            "\t\"ingredients\": \"testIng1, testIng2 ,TestIng3\",\n" +
            "\t\"tags\": [\"testTag1\", \"testTag2\", \"testTag3\"],\n" +
            "\t\"alterations\": {\n" +
            "\t\t\"alteration10.jpeg\": {\n" +
            "\t\t\t\"tags\": [\"tagliata\"],\n" +
            "\t\t\t\"notes\": \"alterationTestNotes\"\n" +
            "\t\t},\n" +
            "\t\t\"alteration11.jpeg\": {\n" +
            "\t\t\t\"tags\": [\"angolata\"],\n" +
            "\t\t\t\"notes\": \"alterationTestNotes\"\n" +
            "\t\t}\n" +
            "\t},\n" +
            "\t\"notes\": \"testNote\",\n" +
            "\t\"original_name\": \"testOriginalName.jpg\"\n" +
            "}";

    private TestElement testElement;

    @Before
    public void init()  throws JSONException {

        JSONObject jsonElement = new JSONObject(testString);

        testElement = new TestElement(null, jsonElement, "test");

        //init test with fake data
            testElement.setConfidence(1);
            testElement.setRecognizedText("testRecogText");

            for(String alterationName : testElement.getAlterationsNames()) {
                testElement.setAlterationRecognizedText(alterationName, "RecognizedText");
                testElement.setAlterationConfidence(alterationName, 1);
            }

    }

    @Test
    public void getAlterationRecognizedText() {
        String expected = "RecognizedText";
        String actual = testElement.getAlterationRecognizedText("alteration10.jpeg");
        assertEquals(expected, actual);

        actual = testElement.getAlterationRecognizedText("alteration11.jpeg");
        assertEquals(expected, actual);

        testElement.setAlterationRecognizedText("alteration10.jpeg", "");
        actual = testElement.getAlterationRecognizedText("alteration10.jpeg");
        assertEquals("", actual);

        actual = testElement.getAlterationRecognizedText("non-existing");
        assertNull(actual);

    }

    @Test
    public void getAlterationConfidence() {
        float expected = 1;
        float actual = testElement.getAlterationConfidence("alteration10.jpeg");

        assertEquals(expected, actual, 0);

        actual= testElement.getAlterationConfidence("alteration11.jpeg");

        assertEquals(expected, actual, 0);

        //non existing must return -1
        actual = testElement.getAlterationConfidence("non-existing");
        assertEquals(-1, actual, 0);
    }

    @Test
    public void getAlterationNotes() {
        String expected = "alterationTestNotes";
        String actual = testElement.getAlterationNotes("alteration10.jpeg");

        assertEquals(expected,actual);

        actual = testElement.getAlterationNotes("non-existing");
        assertNull(actual);
    }

    @Test
    public void getAlterationTags() {
        String[] expected = {"tagliata"};
        String[] actual = testElement.getAlterationTags("alteration10.jpeg");

        assertArrayEquals(expected, actual);

        actual = testElement.getAlterationTags("non-existing");
        assertNull(actual);
    }

    @Test
    public void getConfidence() {
        float expected = 1;
        float actual = testElement.getConfidence();

        assertEquals(expected, actual, 0);

        //setting negative confidence must produce no change
        testElement.setConfidence(-10);
        actual = testElement.getConfidence();

        assertEquals(1, actual, 0);
    }

    @Test
    public void getRecognizedText() {
        String expected = "testRecogText";
        String actual = testElement.getRecognizedText();

        assertEquals(expected, actual);

        testElement.setRecognizedText("");
        actual = testElement.getRecognizedText();
        assertEquals("", actual);

        testElement.setRecognizedText(null);
        actual = testElement.getRecognizedText();
        assertNull(actual);
    }

    @Test
    public void getIngredientsArray() {
        String[] expected = {"testIng1", "testIng2" ,"TestIng3"};
        String[] actual = testElement.getIngredientsArray();
        assertArrayEquals(expected, actual);

    }

    @Test
    public void getIngredients() {
        String expected = "testIng1, testIng2 ,TestIng3";
        String actual = testElement.getIngredients();

        assertEquals(expected, actual);
    }

    @Test
    public void getTags() {

        String[] expected = {"testTag1", "testTag2" ,"testTag3"};
        String[] actual = testElement.getTags();

        assertArrayEquals(expected, actual);
    }

    @Test
    public void getFileName() {

       String expected = "test";
       String actual = testElement.getFileName();

       assertEquals(expected, actual);

    }

    @Test
    public void getNotes() {
        String expected = "testNote";
        String actual = testElement.getNotes();

        assertEquals(expected, actual);

    }

    @Test
    public void getAlterationsNames() {
        String[] expected = {"alteration10.jpeg", "alteration11.jpeg"};
        String[] actual = testElement.getAlterationsNames();

        assertArrayEquals(expected, actual);
    }


}