package unipd.se18.ocrcamera.inci;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * IngredientsExtractor implementation that for each INCI ingredient check if it is contained
 * inside the text. Non alphanumeric characters are ignored.
 * @author Francesco Pham
 */
public class NameMatchIngredientsExtractor implements IngredientsExtractor {

    //list where all ingredients from inci db are stored
    private List<Ingredient> listIngredients;

    //Tag for logs
    private static final String TAG = "IngredientsExtractor";

    /**
     * Constructor initializes the ingredients extractor
     * @param listIngredients Total list of ingredients from the INCI DB
     */
    public NameMatchIngredientsExtractor(List<Ingredient> listIngredients) {
        //copying list so that sorting doesn't affect original list
        this.listIngredients = new ArrayList<>(listIngredients);

        //sort by name length so when we search for ingredients in text we match longer names first
        Collections.sort(this.listIngredients, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient o1, Ingredient o2) {
                return o2.getInciName().length() - o1.getInciName().length();
            }
        });
    }


    /**
     * This method extracts ingredients from the ocr text and returns the list of ingredients.
     * @param text The entire OCR text
     * @return List of extracted ingredients, empty list if no ingredients are found
     * @author Francesco Pham
     */
    @Override
    public List<Ingredient> findListIngredients(String text) {

        List<Ingredient> foundIngredients = new ArrayList<>();

        //remove non alphanumeric characters from text
        //in mapIndexes we store for each character in the stripped text, the original position
        int[] mapIndexes = new int[text.length()];
        StringBuilder strippedTextBuilder = new StringBuilder();
        for(int i=0; i<text.length(); i++) {
            char currentChar = text.charAt(i);
            if(Character.isLetter(currentChar) || Character.isDigit(currentChar)) {
                mapIndexes[strippedTextBuilder.length()] = i;
                strippedTextBuilder.append(currentChar);
            }
        }
        String strippedText = strippedTextBuilder.toString();

        //for each inci ingredient check if it is contained in the text
        for(Ingredient ingredient : listIngredients) {
            String strippedName = ingredient.getStrippedInciName();
            int indexOf = strippedText.indexOf(strippedName);
            if(indexOf >= 0){
                //found the ingredient
                ingredient.setStartPositionFound(mapIndexes[indexOf]);
                ingredient.setEndPositionFound(mapIndexes[indexOf+strippedName.length()-1]);
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
                return o1.getStartPositionFound() - o2.getStartPositionFound();
            }
        });

        return foundIngredients;
    }
}
