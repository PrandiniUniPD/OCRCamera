package unipd.se18.ocrcamera;

import org.junit.Test;
import static org.junit.Assert.*;

public class LevenshteinTest {
    @Test
    public void stringSimilarityTest(){
        LevenshteinStringComparator lsc = new LevenshteinStringComparator();

        double similarity = lsc.getNormalizedSimilarity("cavallo", "cavallo");
        assertTrue(0.99 < similarity && similarity < 1.01);

        similarity = lsc.getNormalizedSimilarity("abcd", "efgh");
        assertTrue(-0.01 < similarity && similarity < 0.01);
    }
}
