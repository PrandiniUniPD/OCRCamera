package unipd.se18.ocrcamera.inci;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * For the search of best matching words in the dictionary I used an implementation of bk-trees,
 * this way the search complexity is logarithmic instead of quadratic.
 * For more information: https://github.com/gtri/bk-tree
 *
 * @author Francesco Pham
 */
public class TextAutoCorrection {

    private final String TAG = "TextAutoCorrection";

    //searcher for words in the word list with minimum distance from a given query
    private BkTreeSearcher<String> searcher;

    /**
     * Constructor which loads the word list into a bk-tree and initialize the searcher
     * @param wordList InputStream from the word list
     * @author Francesco Pham
     */
    public TextAutoCorrection(InputStream wordList){

        //open word list
        BufferedReader reader = new BufferedReader(new InputStreamReader(wordList));

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
     * Each word of the text is searched for a best match in the dictionary and
     * if there is a good match the word is corrected
     * @param text The text (probably taken from ocr) which has to be corrected
     * @return Corrected text
     */
    public String correctText(String text){

        //not correcting words with less than minChars characters
        final int minChars = 3;

        text = formatText(text);

        //search all words composed by alphanumeric or hyphen characters
        Pattern pattern = Pattern.compile("[a-zA-Z0-9-]+");
        Matcher matcher = pattern.matcher(text);

        //generate list of words to correct
        ArrayList<String> wordsToCorrect = new ArrayList<>();
        ArrayList<Integer> wordsStartPos = new ArrayList<>();
        while (matcher.find()) {
            String word = matcher.group();
            if(word.length()>=minChars) {
                wordsToCorrect.add(word);
                wordsStartPos.add(matcher.start());
            }
        }


        if(wordsToCorrect.size() == 0)
            return text; //no words to correct

        //correct words
        List<String> correctedWords = correctMultipleWords(wordsToCorrect);

        //in this array we store the mapping between indexes of original text and the corrected text.
        int[] mapIndexes = new int[text.length()];
        for(int i=0; i<text.length(); i++) mapIndexes[i] = i;
        String correctedText = text;

        //construct corrected text by substituting the corrected words
        for(int i=0; i<wordsToCorrect.size(); i++){
            String oldWord = wordsToCorrect.get(i);
            String correctedWord = correctedWords.get(i);
            if (!correctedWord.equals(oldWord)) {

                Log.d(TAG, "word " + oldWord + " corrected with " + correctedWord);

                //get start-end positions of the word to replace
                int startPos = wordsStartPos.get(i);
                int endPos = startPos+oldWord.length();

                //substitute with the corrected word
                String newText = "";
                if(startPos > 0) newText = correctedText.substring(0, mapIndexes[startPos]);
                newText = newText + correctedWord;
                if (endPos < text.length()) newText = newText + correctedText.substring(mapIndexes[endPos]);

                correctedText = newText;

                //shift map indexes by the difference of length between the old word and corrected word
                int shift = correctedWord.length() - oldWord.length();
                int from = startPos + Math.min(correctedWord.length(), oldWord.length());
                for (int j = from; j < text.length(); j++)
                    mapIndexes[j] += shift;
            }
        }

        return correctedText;
    }


    /**
     * format the text in order to increase the probability to match ingredients in the INCI DB
     * @param text Text to be formatted
     * @return Text formatted
     */
    private String formatText(String text){
        //merge the word before and after hyphen + new line (e.g. "ceta- \n ril" into "cetaryl")
        text = text.replaceAll(" *- *[\\n\\r]+ *", "");

        //ignoring case by converting all into upper case
        text = text.toUpperCase();
        return text;
    }

    /**
     * Each word in the list given is corrected
     * @param words Words to be corrected in a list
     * @return List of corrected words in the same position of the original word in the given list.
     */
    private List<String> correctMultipleWords(List<String> words){

        //minimum number of words per task (if total number of words is less than this value all words are corrected in one task)
        final int minWordsPerTask = 10;

        //calculate number of concurrent tasks and number of words per task
        int concurrentTasks;
        if(words.size() < minWordsPerTask)
            concurrentTasks = 1;
        else {
            concurrentTasks = Runtime.getRuntime().availableProcessors();
            if (concurrentTasks > 1) {
                concurrentTasks--; //leave a processor for the OS
            }
            if (words.size() / concurrentTasks < minWordsPerTask)
                concurrentTasks = words.size() / minWordsPerTask;
        }
        int wordsPerTask = words.size()/concurrentTasks;
        Log.d(TAG, concurrentTasks+" tasks. "+wordsPerTask+" words per task.");

        //generate threads
        CountDownLatch latch = new CountDownLatch(concurrentTasks);
        WordsCorrectionThread[] threads = new WordsCorrectionThread[concurrentTasks];
        for(int i=0; i<concurrentTasks; i++){
            //split the word list to be corrected
            int from = i*wordsPerTask;
            int to = i==concurrentTasks-1 ? words.size() : (i+1)*wordsPerTask;
            List<String> wordList = words.subList(from, to);

            //start thread
            threads[i] = new WordsCorrectionThread(wordList, latch);
            threads[i].start();
        }

        //wait until all threads are finished
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return words;
        }

        //join the result lists
        ArrayList<String> correctedWords = new ArrayList<>(words.size());
        for(int i=0; i<concurrentTasks; i++){
            correctedWords.addAll(threads[i].getCorrectedWords());
        }
        return correctedWords;
    }

    /**
     * Thread for words correction
     */
    private class WordsCorrectionThread extends Thread {
        private List<String> wordsToCorrect;
        private List<String> correctedWords;
        private CountDownLatch doneSignal;

        /**
         * Constructor
         * @param words Words to be corrected
         * @param doneSignal CountDownLatch for signalling when the thread has ended
         */
        WordsCorrectionThread(List<String> words, CountDownLatch doneSignal){
            wordsToCorrect = words;
            this.doneSignal = doneSignal;
        }

        public void run(){
            //correct each word
            correctedWords = new ArrayList<>(wordsToCorrect.size());
            for(String word : wordsToCorrect){
                correctedWords.add(correctWord(word));
            }
            doneSignal.countDown();
        }

        List<String> getCorrectedWords(){
            return correctedWords;
        }
    }

    /**
     * Correct a single word by searching for the most similar in word list
     * @param word The word to be corrected
     * @return Best candidate word from word list. If no words within maxDistance is found, the same word is returned.
     */
    private String correctWord(String word){

        //percentage distance below which we substitute the word with the term found in dictionary
        // (during testing i found out that above 30% the confidence does not improve by much and
        // also increases chance of correcting words not related to ingredients)
        final double maxNormalizedDistance = 0.30;

        //Searches the tree for elements whose distance satisfy max distance
        // for the demostration of the distance upper bound see:
        // https://github.com/frankplus/incidb/blob/master/maxNormalizedDistanceFormulaDim.jpg
        int distanceUpperBound = (int) (word.length()*maxNormalizedDistance/(1-maxNormalizedDistance));
        Set<BkTreeSearcher.Match<? extends String>> matches =
                searcher.search(word, distanceUpperBound);

        //find the word with minimum distance
        double minDistance = Double.MAX_VALUE;
        String closest = "";
        for (BkTreeSearcher.Match<? extends String> match : matches){

            //if same word is found, no need to continue
            if(match.getDistance() == 0) {
                closest = word;
                break;
            }

            //calculate normalized distance
            int wordLength = word.length();
            int matchLength = match.getMatch().length();
            double normalizedDistance = (double) match.getDistance()/Math.max(wordLength, matchLength);

            //if normalized distance satisfy max distance put it into closest match
            if(normalizedDistance <= maxNormalizedDistance && normalizedDistance < minDistance) {
                minDistance = normalizedDistance;
                closest = match.getMatch();
            }
        }

        //If no words within maxNormalizedDistance is found, the same word is returned.
        return closest.equals("") ? word : closest;
    }
}
