package unipd.se18.ocrcamera;

import java.util.ArrayList;
import java.util.List;


public class PrecorrectionIngredientsExtractor implements IngredientsExtractor {

    //list of recognized ingredients where are stored informations about ingredients
    private List<Ingredient> listIngredients;

    private static final String TAG = "PrecorrectionIngredientsExtractor";

    private TextAutoCorrection corrector;

    public PrecorrectionIngredientsExtractor(List<Ingredient> listIngredients, TextAutoCorrection corrector) {
        this.listIngredients = listIngredients;
        this.corrector = corrector;
    }


    /**
     * This method extract ingredients from the ocr text and returns the list of ingredients.
     * @param text The entire OCR text
     * @return List of Ingredient objects where are stored ingredient's informations
     * @author Francesco Pham
     */
    @Override
    public ArrayList<Ingredient> findListIngredients(String text) {

        //initializing the list
        ArrayList<Ingredient> foundIngredients = new ArrayList<Ingredient>();

        text = corrector.correctText(text);
        for(Ingredient ingredient : listIngredients){
            if(text.contains(ingredient.getInciName())){
                foundIngredients.add(ingredient);
            }
        }

        return foundIngredients;
    }
}
