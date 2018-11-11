package unipd.se18.ocrcamera;

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
            assertNotNull(report);
            assertNotEquals("", report);



            JSONObject jsonReport = new JSONObject(report);
            JSONObject testElem = jsonReport.getJSONObject("foto11");
            String[] expected = new String[]{"non_inclinata","non_angolata","testo_presente","poca_luce","etichetta_piana","caratteri_non_danneggiati","nitida","foto_non_mossa","alta_risoluzione"};
            String[] actual = Utils.getStringArrayFromJSON(testElem, "tags");


            assertEquals(expected, actual);

        } catch (JSONException e) {

        }
    }

}