package unipd.se18.ocrcamera;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void getFileExtension() {
        String path = "testDir/testImage.jpeg";
        String expected = "jpeg";
        String actual = Utils.getFileExtension(path);
    }
}