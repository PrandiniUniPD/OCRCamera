package unipd.se18.ocrcamera;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

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
        TextAutoCorrection corrector = new TextAutoCorrection(wordListStream);

        //single word correction
        assertEquals("CHOLESTEROL", corrector.correctText("CNOLSTEROL"));

        //more than maxDistance is not corrected
        assertEquals("ACYLLARES", corrector.correctText("ACYLLARES"));

        //multiple words correction
        assertEquals("COCOYL HYDROLYZED COLLAGEN", corrector.correctText("CQCOYL HYROLYZED COLLGEN"));

        //multiple words correction separated by symbols
        assertEquals("COCOYL$HYDROLYZED:COLLAGEN", corrector.correctText("CQCOYL$HYROLYZED:COLLGEN"));

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
        IngredientsExtractor extractor = new PrecorrectionIngredientsExtractor(totIngredients,corrector);

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

        //test of a difficult text
        text = "some more text...DiSsODLUM TEtraMETH-  \n  YLHEADECENVL  \nSUOCINOYL\nCYSTEINEblabla";
        extractedIngredients = extractor.findListIngredients(text);
        assertEquals("92137", extractedIngredients.get(0).getCosingRefNo());
    }
}
