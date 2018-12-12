package unipd.se18.ocrcamera;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import edu.gatech.gtri.bktree.BkTreeSearcher;
import edu.gatech.gtri.bktree.Metric;
import edu.gatech.gtri.bktree.MutableBkTree;

/**
 * This class corrects automatically the ocr text using a
 * dictionary of different words taken from INCI DB.
 *
 * To see how I generated the dictionary see :
 * https://github.com/frankplus/incidb/tree/master/src/main/java
 *
 * For the search of best matching words in the dictionary I used an implementation of bk-trees
 * For more information: https://github.com/gtri/bk-tree
 *
 * @author Francesco Pham
 */
public class TextAutoCorrection {

    //Do not correct words with less than minChars characters
    private final int minChars = 3;

    //Threshold of minimum normalized distance below which we substitute the word with the term found in dictionary
    private final double distanceThreshold = 0.2;

    private final String TAG = "TextAutoCorrection";

    //declaring metric used for string distance
    private LevenshteinStringDistance levenshtein;
    private Metric<String> levenshteinDistance = new Metric<String>() {
        @Override
        public int distance(String x, String y) {
            return (int) levenshtein.distance(x,y);
        }
    };


    BkTreeSearcher<String> searcher;


    /**
     * Constructor
     * @param context app context
     * @author Francesco Pham
     */
    public TextAutoCorrection(Context context){
        //open word list
        levenshtein = new LevenshteinStringDistance();
        InputStream stream = context.getResources().openRawResource(R.raw.inciwordlist);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        //inizialize bk-tree
        MutableBkTree<String> bkTree = new MutableBkTree<>(levenshteinDistance);

        //add each element to the tree
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                bkTree.add(line);
            }
        }catch(IOException e){
            Log.e(TAG,"Error reading word list");
        }

        //initialize searcher
        searcher = new BkTreeSearcher<>(bkTree);
    }

    /**
     * format the text in order to increase the probability to match ingredients in the INCI DB
     * @param text
     * @return Text formatted
     */
    private String formatText(String text){
        text = text.toUpperCase();
        text = text.trim().replaceAll(" +", " ");
        return text;
    }

    /**
     * Each word of the text is searched for a best match in the dictionary and
     * if there is a good match the word is corrected
     * @param text The text (probably taken from ocr) which has to be corrected
     * @return Corrected text
     */
    public String correctText(String text){

        text = formatText(text);

        //split the text into each word containing only letters or numbers with more than minChars characters
        int lastNonAlphanumIndex = -1;
        for(int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if(!Character.isLetter(c) && !Character.isDigit(c)){
                if(i > lastNonAlphanumIndex+minChars){

                    String word = text.substring(lastNonAlphanumIndex+1,i);

                    //Searches the tree for elements whose distance satisfy distanceThreshold
                    Set<BkTreeSearcher.Match<? extends String>> matches =
                            searcher.search(word, (int) (word.length()*distanceThreshold));

                    //find the word with minimum distance
                    int minDistance = Integer.MAX_VALUE;
                    String term = "";
                    for (BkTreeSearcher.Match<? extends String> match : matches){
                        if(match.getDistance() < minDistance) {
                            minDistance = match.getDistance();
                            term = match.getMatch();
                        }
                    }

                    if(!term.equals("") && !term.equals(word)){

                        Log.d(TAG,"word = "+word+" ; found word = "+term+" ; distance = "+minDistance);

                        //substitute with the word found
                        text = text.substring(0, lastNonAlphanumIndex+1) + term + text.substring(i);

                        //take into account the difference of length between the words
                        i += term.length()-word.length();
                    }
                }

                lastNonAlphanumIndex = i;
            }
        }

        return text;
    }
}
