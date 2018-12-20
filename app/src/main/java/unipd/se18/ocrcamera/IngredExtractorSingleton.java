package unipd.se18.ocrcamera;

import android.content.Context;
import java.io.InputStream;
import java.util.List;

import unipd.se18.ocrcamera.inci.Inci;
import unipd.se18.ocrcamera.inci.Ingredient;
import unipd.se18.ocrcamera.inci.IngredientsExtractor;
import unipd.se18.ocrcamera.inci.PrecorrectionIngredientsExtractor;
import unipd.se18.ocrcamera.inci.TextAutoCorrection;

/**
 * Using singleton design pattern for single time inci db loading and text extractor initialization.
 * @author Francesco Pham
 */
class IngredExtractorSingleton {

    private static volatile IngredientsExtractor ingredientsExtractor;

    static IngredientsExtractor getInstance(Context context) {
        if (ingredientsExtractor == null) {
            synchronized (IngredExtractorSingleton.class) {
                if (ingredientsExtractor == null) ingredientsExtractor = load(context);
            }
        }
        return ingredientsExtractor;
    }

    private IngredExtractorSingleton() {
        if (ingredientsExtractor != null){
            throw new RuntimeException("Use getInstance() method to get ingredientExtractor.");
        }
    }

    /**
     * Load list of ingredients from INCI DB and initialize ingredients extractor.
     * @param context
     */
    private static IngredientsExtractor load(Context context){
        //load inci db and initialize ingredient extractor
        InputStream inciDbStream = context.getResources().openRawResource(R.raw.incidb);
        List<Ingredient> listInciIngredients = Inci.getListIngredients(inciDbStream);

        InputStream wordListStream = context.getResources().openRawResource(R.raw.inciwordlist);
        TextAutoCorrection textCorrector = new TextAutoCorrection(wordListStream);

        return new PrecorrectionIngredientsExtractor(listInciIngredients, textCorrector);
    }
}
