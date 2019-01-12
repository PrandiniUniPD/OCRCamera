package unipd.se18.ocrcamera;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import unipd.se18.ocrcamera.inci.Inci;
import unipd.se18.ocrcamera.inci.Ingredient;
import unipd.se18.ocrcamera.inci.IngredientsExtractor;
import unipd.se18.ocrcamera.inci.LevenshteinStringDistance;
import unipd.se18.ocrcamera.inci.NameMatchIngredientsExtractor;
import unipd.se18.ocrcamera.inci.TextAutoCorrection;

import static org.junit.Assert.*;

/**
 * Junit tests of the text correction and ingredients extraction from alterated texts.
 * @author Francesco Pham
 */
public class IngredientsExtractionTest {
    @Test
    public void stringSimilarityTest(){
        LevenshteinStringDistance lsc = new LevenshteinStringDistance();

        double similarity = lsc.getNormalizedSimilarity("cavallo", "cavallo");
        assertTrue(0.99 < similarity && similarity < 1.01);

        similarity = lsc.getNormalizedDistance("terallo", "cavallo");
        assertTrue(0.42 < similarity && similarity < 0.43);

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
        TextAutoCorrection corrector = new TextAutoCorrection(wordListStream);

        //single word correction
        assertEquals("CHOLESTEROL", corrector.correctText("CNOLSTEROL"));

        //more than maxDistance is not corrected
        assertEquals("ACYLLARES", corrector.correctText("ACYLLARES")); //original word is "ACRYLATES"

        //multiple words correction
        assertEquals("COCOYL  HYDROLYZED   COLLAGEN", corrector.correctText("CQCOYL  HYROLYZED   COLLGEN"));

        //multiple words correction separated by symbols
        assertEquals("COCOYL$HYDROLYZED:COLLAGEN.", corrector.correctText("CQCOYL$HYROLYZED:COLLGEN."));

        //test substitution of hyphen + line break
        assertEquals("OLIGOSACCHARIDES", corrector.correctText("OLIGOSAC-\nCHARIDES"));
        assertEquals("OLIGOSACCHARIDES", corrector.correctText("OLIGOSAC-  \n   CHARIDES"));
    }

    @Test
    public void ingredientsExtractionTest(){
        //load word list for text corrector
        File wordListFile = new File("src/main/res/raw/inciwordlist.txt");
        InputStream wordListStream;
        try {
            wordListStream = new FileInputStream(wordListFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        TextAutoCorrection corrector = new TextAutoCorrection(wordListStream);

        //load inci db
        File inciFile = new File("src/main/res/raw/incidb.csv");
        InputStream inciStream;
        try {
            inciStream = new FileInputStream(inciFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        List<Ingredient> totIngredients = Inci.getListIngredients(inciStream);
        IngredientsExtractor extractor = new NameMatchIngredientsExtractor(totIngredients);

        //single word ingredients name
        String text = "CHOLETH-10";
        List<Ingredient> extractedIngredients = extractor.findListIngredients(text);
        assertEquals("75006", extractedIngredients.get(0).getCosingRefNo());

        //name composed by multiple words (check if "SODIUM ACRYLATES COPOLYMER" is matched first and not just "SODIUM")
        text = "SODIUM ACRYLATES COPOLYMER";
        extractedIngredients = extractor.findListIngredients(text);
        assertEquals("79031", extractedIngredients.get(0).getCosingRefNo());

        //multiple whitespaces inside text
        text = "ASTER         AGERATOIDES    EXTRACT";
        extractedIngredients = extractor.findListIngredients(text);
        assertEquals("83720", extractedIngredients.get(0).getCosingRefNo());

        //whitespaces before and after hyphen
        text = "CHOLETH  -   10";
        extractedIngredients = extractor.findListIngredients(text);
        assertEquals("75006", extractedIngredients.get(0).getCosingRefNo());

        //test of whitespaces before and after slash
        text = "ALPINIA SPECIOSA FLOWER   /  LEAF   /SEED/   STEM EXTRACT";
        extractedIngredients = extractor.findListIngredients(text);
        assertEquals("89745", extractedIngredients.get(0).getCosingRefNo());

        //test of an alterated text using text correction
        text = "some more text...DiSsODLUM TEtraMETH -  \n  YLHEADECENVL  \nSUOCINOYL\nCYSTEINEblabla";
        text = corrector.correctText(text);
        extractedIngredients = extractor.findListIngredients(text);
        assertEquals("92137", extractedIngredients.get(0).getCosingRefNo());

        //test of false ingredient inside word (verify "EGG" is not matched inside "PROTEGGERE")
        text = "PROTEGGERE";
        extractedIngredients = extractor.findListIngredients(text);
        assertEquals(0, extractedIngredients.size());

        //test of false ingredient inside word (verify "AQUA" is not matched)
        text = "CASA, QUALE";
        extractedIngredients = extractor.findListIngredients(text);
        assertEquals(0, extractedIngredients.size());
    }
}
