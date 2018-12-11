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
    final int defMaxDist = 8;

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
            Log.e(TAG, "couldn't deserialize dictionary");
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
     * if there is a good match the word it is corrected
     * @param text The text (probably taken from ocr) which has to be corrected
     * @return Corrected text
     */
    public String correctText(String text){

        return text;
    }
}
