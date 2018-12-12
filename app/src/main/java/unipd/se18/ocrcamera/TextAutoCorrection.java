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
 * For the search of best matching words in the dictionary I used an
 * efficient implementation of the Levenshtein Automata, here for more information:
 * https://github.com/universal-automata/liblevenshtein-java
 *
 * @author Francesco Pham
 */
public class TextAutoCorrection {

    //Do not correct words with less than minChars characters
    private final int minChars = 3;

    //Threshold of minimum normalized distance below which we substitute the word with the term found in dictionary
    private final double distanceThreshold = 0.2;

    private final String TAG = "TextAutoCorrection";
    private LevenshteinStringDistance levenshtein;
    BkTreeSearcher<String> searcher;


    /**
     * Constructor
     * @param context app context
     * @author Francesco Pham
     */
    public TextAutoCorrection(Context context){
        levenshtein = new LevenshteinStringDistance();
        InputStream stream = context.getResources().openRawResource(R.raw.inciwordlist);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        MutableBkTree<String> bkTree = new MutableBkTree<>(levenshteinDistance);
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                bkTree.add(line);
            }
        }catch(IOException e){
            Log.e(TAG,"Error reading word list");
        }

        searcher = new BkTreeSearcher<>(bkTree);
    }

    private Metric<String> levenshteinDistance = new Metric<String>() {
        @Override
        public int distance(String x, String y) {
            return (int) levenshtein.distance(x,y);
        }
    };

    /**
     * Each word of the text is searched for a best match in the dictionary and
     * if there is a good match the word is corrected
     * @param text The text (probably taken from ocr) which has to be corrected
     * @return Corrected text
     */
    public String correctText(String text){

        text = text.toUpperCase();
        text = text.trim().replaceAll(" +", " ");

        int previousNonAlphanumIndex = -1;
        for(int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if(!Character.isLetter(c) && !Character.isDigit(c)){

                if(i > previousNonAlphanumIndex+minChars){
                    String word = text.substring(previousNonAlphanumIndex+1,i);

                    //find the word with minimum distance
                    int minDistance = Integer.MAX_VALUE;
                    String term = "";

                    Set<BkTreeSearcher.Match<? extends String>> matches =
                            searcher.search(word, (int) (word.length()*distanceThreshold));
                    for (BkTreeSearcher.Match<? extends String> match : matches){
                        if(match.getDistance() < minDistance) {
                            minDistance = match.getDistance();
                            term = match.getMatch();
                        }
                    }

                    if(!term.equals("") && !term.equals(word)){

                        Log.d(TAG,"word = "+word+" ; found word = "+term+" ; distance = "+minDistance);

                        //substitute with the word found
                        text = text.substring(0, previousNonAlphanumIndex+1) + term + text.substring(i);

                        //take into account the difference of length between the words
                        i += term.length()-word.length();
                    }
                }

                previousNonAlphanumIndex = i;
            }
        }

        return text;
    }
}
