package unipd.se18.ocrcamera;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import edu.gatech.gtri.bktree.BkTreeSearcher;
import edu.gatech.gtri.bktree.Metric;
import edu.gatech.gtri.bktree.MutableBkTree;

/**
 * IngredientsExtractor implementation that splits the ocr text and for each block of text search
 * inside INCI DB for the ingredient with minimum distance using Levenshtein.
 * The downside of this method is that ingredients that are not properly separated are not recognized.
 * @author Francesco Pham
 */
public class TextSplitIngredientsExtractor implements IngredientsExtractor {

    //list of recognized ingredients where are stored informations about ingredients
    private List<Ingredient> listIngredients;

    private static final String TAG = "IngredientsExtractor";

    private BkTreeSearcher<String> inciNameSearcher;

    /**
     * Constructor loads a tree of inci names for a much faster search
     * @param listIngredients Total list of ingredients from the INCI DB
     */
    public TextSplitIngredientsExtractor(List<Ingredient> listIngredients) {
        this.listIngredients = listIngredients;

        //listIngredients has to be sorted for the binary search to work
        Collections.sort(listIngredients, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient o1, Ingredient o2) {
                return o1.compareTo(o2.getInciName());
            }
        });


        //declaring metric used for string distance
        final LevenshteinStringDistance levenshtein = new LevenshteinStringDistance();
        final Metric<String> levenshteinDistance = new Metric<String>() {
            @Override
            public int distance(String x, String y) {
                return (int) levenshtein.distance(x,y);
            }
        };

        //inizialize bk-tree
        MutableBkTree<String> bkTree = new MutableBkTree<>(levenshteinDistance);

        //add each element to the tree
        for(Ingredient ingredient : listIngredients){
            bkTree.add(ingredient.getInciName());
        }

        //initialize searcher
        inciNameSearcher = new BkTreeSearcher<>(bkTree);
    }


    /**
     * This method extract ingredients from the ocr text and returns the list of ingredients.
     * @param text The entire OCR text
     * @return List of Ingredient objects where are stored ingredient's informations
     * @author Francesco Pham
     */
    @Override
    public ArrayList<Ingredient> findListIngredients(String text) {

        ArrayList<Ingredient> foundIngredients = new ArrayList<>();

        //maximum accepted distance between block of text and inci name
        final double maxDistance = 0.2;

        //split the text by each comma or dot
        String[] splittedText = text.trim().split("[,.]+");

        //for every splitted text inside the ocr text search for the most similar in the inci db
        for (String str : splittedText) {

            //Searches the tree for elements whose distance satisfy maxDistance
            Set<BkTreeSearcher.Match<? extends String>> matches =
                    inciNameSearcher.search(str, (int) (str.length()*maxDistance));

            //find the ingredient name with minimum distance
            int minDistance = Integer.MAX_VALUE;
            String ingredientName = null;
            boolean found = false;
            for (BkTreeSearcher.Match<? extends String> match : matches){
                if(match.getDistance() < minDistance) {
                    minDistance = match.getDistance();
                    ingredientName = match.getMatch();
                    found = true;
                }
            }

            //search ingredient by its name and add it to list
            if(found) {
                Log.d(TAG, "found "+ingredientName+" in "+str+". Distance="+minDistance);
                int indexBestIngredient = Collections.binarySearch(listIngredients, ingredientName);
                if(indexBestIngredient >= 0)
                    foundIngredients.add(listIngredients.get(indexBestIngredient));
                else
                    Log.e(TAG, "Couldn't find ingredient, this is strange.");
            }
        }

        return foundIngredients;
    }
}
