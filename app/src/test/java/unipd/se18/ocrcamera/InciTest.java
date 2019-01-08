package unipd.se18.ocrcamera;

import android.content.Context;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Leonardo Pratesi
 *
 */
public class InciTest {

    //check inputstream
    @Test
    public void loadDBTest() throws Exception {
        Inci provainci = new Inci();
        InputStream database = this.getClass().getClassLoader().getResourceAsStream("database.csv");
        provainci.loadDB(database);
        assertNotNull(database);

    }

}
