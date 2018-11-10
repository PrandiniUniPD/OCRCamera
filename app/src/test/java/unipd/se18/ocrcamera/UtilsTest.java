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
    }


    @Test
    public void getStringArrayFromJSON() {

        JSONObject json = null;
        try {
            json = new JSONObject("{\"ingredients\":\"aqua, camphor, cetearyl alcohol, glycerin, caprylic\\/capric triglyceride, olea europaea (olive) fruit oil, prunus amygdalus dulcis (sweet almond) oil, parfum (fragrance), glyceryl stearate SE, PEG-100 stearate, stearic acid, mentha piperita (peppermint) oil, eucalyptus globus leaf oil, phenoxyethanol, benzyl alcohol, sodium benzoate, potassium sorbate, limonene, linalool\",\"tags\":[\"non_inclinata\",\"non_angolata\",\"testo_presente\",\"luce_ottimale\",\"etichetta_piana\",\"caratteri_non_danneggiati\",\"nitida\",\"foto_non_mossa\",\"alta_risoluzione\"],\"notes\":\"\",\"original_name\":\"cremabarba_depot_emoliente_1_A.jpg\"}");
        } catch (JSONException e) {

        }
        String[] expected = {"non_inclinata","non_angolata","testo_presente","luce_ottimale","etichetta_piana","caratteri_non_danneggiati","nitida","foto_non_mossa","alta_risoluzione"};
        String[] actual = Utils.getStringArrayFromJSON(json, "tags");
        //assertEquals(expected, actual);


    }
}