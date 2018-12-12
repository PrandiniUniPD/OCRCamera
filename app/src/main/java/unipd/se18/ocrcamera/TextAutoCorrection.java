package unipd.se18.ocrcamera;

import android.util.Log;

import com.github.liblevenshtein.collection.dictionary.SortedDawg;
import com.github.liblevenshtein.serialization.ProtobufSerializer;
import com.github.liblevenshtein.serialization.Serializer;
import com.github.liblevenshtein.transducer.Algorithm;
import com.github.liblevenshtein.transducer.Candidate;
import com.github.liblevenshtein.transducer.ITransducer;
import com.github.liblevenshtein.transducer.factory.TransducerBuilder;

import java.io.InputStream;

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

    private final String TAG = "TextAutoCorrection";
    private SortedDawg dictionary = null;
    private ITransducer<Candidate> transducer;

    //Default maximum number of errors tolerated between each spelling candidate and the query term.
    final int defMaxDist = 5;

    //Do not correct words with less than minChars characters
    final int minChars = 3;

    //Threshold of minimum normalized distance below which we substitute the word with the term found in dictionary
    final double distanceThreshold = 0.2;

    /**
     * Constructor
     * @param dictionaryStream Input stream from serialized dictionary.
     * @author Francesco Pham
     */
    public TextAutoCorrection(InputStream dictionaryStream){
        final Serializer serializer = new ProtobufSerializer();

        SortedDawg dictionary = null;

        //load dictionary
        try {
            dictionary = serializer.deserialize(SortedDawg.class, dictionaryStream);
        } catch (Exception e) {
            Log.d(TAG,"couldn't deserialize dictionary");
            return;
        }

        //initializing transducer
        transducer = new TransducerBuilder()
                .dictionary(dictionary)
                .algorithm(Algorithm.MERGE_AND_SPLIT) //Using MERGE_AND_SPLIT because it's better for OCR
                .defaultMaxDistance(defMaxDist)
                .includeDistance(true)
                .build();
    }

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
                    double minDistance = Double.MAX_VALUE;
                    String term = "";
                    for (final Candidate candidate : transducer.transduce(word)) {
                        if(candidate.distance() < minDistance) {
                            minDistance = candidate.distance();
                            term = candidate.term();
                        }
                    }

                    double normalizedDistance = minDistance/word.length();
                    if(normalizedDistance < distanceThreshold && !term.equals("") && !term.equals(word)){

                        //substitute
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
