package unipd.se18.ocrcamera;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PhotoTesterTest {
    private PhotoTester photoTester;

    @Before
    public void setUp() {
        String dirPath = this.getClass().getClassLoader().getResource("PhotoTesterTestDir").getPath();
        photoTester = new PhotoTester(dirPath);
    }

    @Test
    public void testAndReport() {
        try {
            String report = photoTester.testAndReport();
            JSONObject jsonReport = new JSONObject(report);

            JSONObject testElem = jsonReport.getJSONObject("foto11");

            assertEquals(testElem.get("ingredients"), "ing1,ing2,ing 3");

            assertEquals(Utils.getStringArrayFromJSON(testElem, "tags"), new String[]{"non_inclinata","non_angolata","testo_presente","poca_luce","etichetta_piana","caratteri_non_danneggiati","nitida","foto_non_mossa","alta_risoluzione"});
        } catch (JSONException e) {

        }

    }

}