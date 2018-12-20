package unipd.se18.ocrcamera.inci;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

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
     * Constructor initializes the ingredients extractor
     * @param listIngredients Total list of ingredients from the INCI DB
     * @param corrector Text Corrector which has to be correctly pre initialized
     */
    public PrecorrectionIngredientsExtractor(List<Ingredient> listIngredients, TextAutoCorrection corrector) {
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
     * @return List of Ingredient objects where ingredient's informations are stored, empty list if no ingredients are found.
     * @author Francesco Pham
     */
    @Override
    public List<Ingredient> findListIngredients(String text) {

        List<Ingredient> foundIngredients = new ArrayList<>();

        //text correction
        text = corrector.correctText(text);

        //ignore non alphanumeric characters from text
        String strippedText = text.replaceAll("[^A-Za-z0-9]", "");

        //for each inci ingredient check if it is contained in the text
        for(Ingredient ingredient : listIngredients){
            String strippedName = ingredient.getStrippedInciName();
            int indexOf = strippedText.indexOf(strippedName);
            if(indexOf >= 0){
                //found the ingredient
                ingredient.setPositionFound(indexOf);
                foundIngredients.add(ingredient);

                Log.d(TAG, "found "+ingredient.getInciName()+" at pos "+indexOf);

                //remove the ingredient from text replacing it with whitespaces
                String replacement = StringUtils.repeat(' ', strippedName.length());
                strippedText = strippedText.replace(strippedName, replacement);
            }
        }

        //sort by index where the ingredients are found (reconstruct original order)
        Collections.sort(foundIngredients, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient o1, Ingredient o2) {
                return o1.getPositionFound() - o2.getPositionFound();
            }
        });

        return foundIngredients;
    }
}
