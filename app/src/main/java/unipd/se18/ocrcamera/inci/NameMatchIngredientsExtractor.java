package unipd.se18.ocrcamera.inci;

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
            if(isAlphaNumeric(currentChar)) {
                mapIndexes[strippedTextBuilder.length()] = i;
                strippedTextBuilder.append(currentChar);
            }
        }
        String strippedText = strippedTextBuilder.toString();

        //for each inci ingredient check if it is contained in the text
        for(Ingredient ingredient : listIngredients) {
            String strippedName = ingredient.getStrippedInciName();

            int foundAtIndex = strippedText.indexOf(strippedName);
            int foundEndIndex = foundAtIndex+strippedName.length()-1;


            if(foundAtIndex >= 0){
                int foundAtOriginalIndex = mapIndexes[foundAtIndex];
                int foundEndOriginalIndex = mapIndexes[foundEndIndex];

                boolean found = false;

                // for names with nCharThreshold characters or less, check if before and after the name there is
                // a non alphanumeric character (e.g. prevent match of EGG inside PROTEGGE)
                final int nCharThreshold = 4;
                if(strippedName.length() > nCharThreshold) {
                    found = true;
                }
                else if((foundAtOriginalIndex==0 || !isAlphaNumeric(text.charAt(foundAtOriginalIndex-1)))
                            && (foundEndOriginalIndex+1 >= text.length() || !isAlphaNumeric(text.charAt(foundEndOriginalIndex+1)))){
                    found = true;
                }

                if(found){
                    //found the ingredient
                    ingredient.setStartPositionFound(foundAtOriginalIndex);
                    ingredient.setEndPositionFound(foundEndOriginalIndex);
                    foundIngredients.add(ingredient);

                    //remove the ingredient from text replacing it with whitespaces
                    String replacement = StringUtils.repeat(' ', strippedName.length());
                    strippedText = strippedText.replace(strippedName, replacement);
                }
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

    private boolean isAlphaNumeric(char c){
        return Character.isLetter(c) || Character.isDigit(c);
    }
}
