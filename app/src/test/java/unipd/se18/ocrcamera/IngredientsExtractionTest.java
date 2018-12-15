package unipd.se18.ocrcamera;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class IngredientsExtractionTest {
    @Test
    public void stringSimilarityTest(){
        LevenshteinStringDistance lsc = new LevenshteinStringDistance();

        double similarity = lsc.getNormalizedSimilarity("cavallo", "cavallo");
        assertTrue(0.99 < similarity && similarity < 1.01);

        similarity = lsc.getNormalizedSimilarity("abcd", "efgh");
        assertTrue(-0.01 < similarity && similarity < 0.01);
    }

    @Test
    public void textCorrectionTest(){
        File file = new File("src/main/res/raw/inciwordlist.txt");
        InputStream wordListStream;
        try {
            wordListStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        final double maxDistance = 0.2;
        TextAutoCorrection corrector = new TextAutoCorrection(wordListStream, maxDistance);

        //single word correction
        assertEquals("CHOLESTEROL", corrector.correctText("CNOLSTEROL"));

        //more than maxDistance is not corrected
        assertEquals("ACYLLARES", corrector.correctText("ACYLLARES"));

        //multiple words correction
        assertEquals("COCOYL HYDROLYZED COLLAGEN", corrector.correctText("CQCOYL HYROLYZED COLLGEN"));

        //multiple words correction separated by symbols
        assertEquals("COCOYL$HYDROLYZED:COLLAGEN", corrector.correctText("CQCOYL$HYROLYZED:COLLGEN"));
    }
}
