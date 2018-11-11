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
            JSONObject json = new JSONObject(this.getClass().getClassLoader().getResource("txtJsonTest.txt").getFile());
            String[] expected = {"non_inclinata","non_angolata","testo_presente","luce_ottimale","etichetta_piana","caratteri_non_danneggiati","nitida","foto_non_mossa","alta_risoluzione"};
            String[] actual = Utils.getStringArrayFromJSON(json, "tags");
            assertEquals(expected, actual);


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