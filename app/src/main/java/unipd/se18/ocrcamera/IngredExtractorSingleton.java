package unipd.se18.ocrcamera;

import android.content.Context;
import java.io.InputStream;
import java.util.List;

import unipd.se18.ocrcamera.inci.Inci;
import unipd.se18.ocrcamera.inci.Ingredient;
import unipd.se18.ocrcamera.inci.IngredientsExtractor;
import unipd.se18.ocrcamera.inci.nameMatchIngredientsExtractor;
import unipd.se18.ocrcamera.inci.TextAutoCorrection;

/**
 * Using singleton design pattern for single time inci db loading and text extractor initialization.
 * @author Francesco Pham
 */
class IngredExtractorSingleton {
    private static volatile IngredExtractorSingleton ourInstance;

    private IngredientsExtractor ingredientsExtractor;
    private TextAutoCorrection textCorrector;

    static IngredExtractorSingleton getInstance(Context context) {
        if (ourInstance == null) {
            synchronized (IngredExtractorSingleton.class) {
                if (ourInstance == null) ourInstance = new IngredExtractorSingleton(context);
            }
        }
        return ourInstance;
    }

    private IngredExtractorSingleton(Context context) {
        if (ourInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        //Load list of ingredients from INCI DB and initialize ingredients extractor
        InputStream inciDbStream = context.getResources().openRawResource(R.raw.incidb);
        List<Ingredient> listInciIngredients = Inci.getListIngredients(inciDbStream);
        this.ingredientsExtractor = new nameMatchIngredientsExtractor(listInciIngredients);

        //Load wordlist and initialize text corrector
        InputStream wordListStream = context.getResources().openRawResource(R.raw.inciwordlist);
        this.textCorrector = new TextAutoCorrection(wordListStream);
    }

    IngredientsExtractor getIngredientsExtractor(){
        return this.ingredientsExtractor;
    }

    TextAutoCorrection getTextCorrector(){
        return this.textCorrector;
    }

}
