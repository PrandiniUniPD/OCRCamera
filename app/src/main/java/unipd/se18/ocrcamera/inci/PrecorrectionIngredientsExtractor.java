package unipd.se18.ocrcamera.inci;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    PrecorrectionIngredientsExtractor(List<Ingredient> listIngredients, TextAutoCorrection corrector) {
        //copying list so that sorting doesn't affect original list
        this.listIngredients = new ArrayList<>(listIngredients);

        //sort by name length so when we search for ingredients in text we match longer names first
        Collections.sort(this.listIngredients, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient o1, Ingredient o2) {
                return o2.getInciName().length() - o1.getInciName().length();
            }
        });
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

        ArrayList<Ingredient> foundIngredients = new ArrayList<>();

        //text correction
        text = corrector.correctText(text);

        //ignore non alphanumeric characters from text
        text = text.replaceAll("[^A-Za-z0-9]", "");

        //for each inci ingredient check if it is contained in the text
        for(Ingredient ingredient : listIngredients){
            if(text.contains(ingredient.getStrippedInciName())){
                foundIngredients.add(ingredient);
                text = text.replace(ingredient.getStrippedInciName(), "");  //remove the ingredient from text
            }
        }

        return foundIngredients;
    }
}
