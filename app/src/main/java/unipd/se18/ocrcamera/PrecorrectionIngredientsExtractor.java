package unipd.se18.ocrcamera;

import java.util.ArrayList;
import java.util.List;


/**
 * IngredientsExtractor implementation which tries to correct the text before, then for each INCI
 * ingredient check if it is contained inside the corrected text
 * @author Francesco Pham
 */
public class PrecorrectionIngredientsExtractor implements IngredientsExtractor {

    //list of recognized ingredients where are stored informations about ingredients
    private List<Ingredient> listIngredients;

    //Tag for logs
    private static final String TAG = "IngredientsExtractor";

    private TextAutoCorrection corrector;

    /**
     * Constructor
     * @param listIngredients Total list of ingredients from the INCI DB
     * @param corrector Text Corrector
     */
    public PrecorrectionIngredientsExtractor(List<Ingredient> listIngredients, TextAutoCorrection corrector) {
        this.listIngredients = listIngredients;
        this.corrector = corrector;
    }


    /**
     * This method extracts ingredients from the ocr text and returns the list of ingredients.
     * @param text The entire OCR text
     * @return List of Ingredient objects where are stored ingredient's informations
     * @author Francesco Pham
     */
    @Override
    public ArrayList<Ingredient> findListIngredients(String text) {

        ArrayList<Ingredient> foundIngredients = new ArrayList<Ingredient>();

        //text correction
        text = corrector.correctText(text);

        //for each inci ingredient check if it is contained in the text
        for(Ingredient ingredient : listIngredients){
            if(text.contains(ingredient.getInciName())){
                foundIngredients.add(ingredient);
            }
        }

        return foundIngredients;
    }
}
