package unipd.se18.ocrcamera;

import android.content.Context;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *author Leonardo Pratesi
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

        //check null ingredient
    @Test
    public void findIngredientsTest() throws Exception {
        Inci provainci = new Inci();
        InputStream database = this.getClass().getClassLoader().getResourceAsStream("database.csv");
        provainci.loadDB(database);
        String ingredientstest = null;
        assertNotNull(provainci.findIngredients(ingredientstest));

    }
    //check return empty list if no ingredients are found
    @Test
    public void findIngredientsListTest() throws Exception {
        Inci provainci = new Inci();
        String test = "/^^";
        List inci = new ArrayList();
        provainci.findIngredientsList(test);
        assertNotNull(provainci);


    }
}
