package unipd.se18.ocrcamera.performancetester.testers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import unipd.se18.ocrcamera.InciSingleton;
import unipd.se18.ocrcamera.R;
import unipd.se18.ocrcamera.Utils;
import unipd.se18.ocrcamera.inci.Ingredient;
import unipd.se18.ocrcamera.inci.IngredientsExtractor;
import unipd.se18.ocrcamera.inci.LevenshteinStringDistance;
import unipd.se18.ocrcamera.inci.TextAutoCorrection;
import unipd.se18.ocrcamera.inci.TextSplitIngredientsExtractor;
import unipd.se18.ocrcamera.performancetester.TestElement;
import unipd.se18.textrecognizer.OCR;
import unipd.se18.textrecognizer.OCRListener;
import unipd.se18.textrecognizer.TextRecognizer;

/**
 * Class built to test the application's OCR comparing the goal text with the recognized text and
 * providing a JSON report containing stats and results.
 * @author Luca Moroldo (g3) - Francesco Pham (g3)
 */
class PhotoTester extends AbstractPerformanceTester {

    private static final String TAG = "PhotoTester";

    //ingredients extractors (Francesco Pham)
    private IngredientsExtractor ocrIngredientsExtractor;
    private IngredientsExtractor correctIngredientsExtractor;
    private TextAutoCorrection textCorrector;

    /**
     * The String of a JSON report
     */
    private String report;

    /**
     * Load test elements (images + description)
     * @param context The context of the app
     * @param dirPath The path where the photos and descriptions are.
     */
    PhotoTester(Context context, String dirPath) throws TestDirectoryException{
        super(context, dirPath);
    }


    /**
     * Compare the list of ingredients extracted by OCR and the correct list of ingredients
     *
     * @param correct       Correct list of ingredients loaded from file
     * @param extracted     List of ingredients extracted by the OCR
     * @return Confidence percentage based on number of matched words, their similarity and order
     * @author Francesco Pham credit to Stefano Romanello for Levenshtein library suggestion
     */
    private float ingredientsTextComparison(String correct, String extracted){

        extracted = extracted.toLowerCase(); //ignoring case

        //split words
        String[] extractedWords = extracted.trim().split("[ ,-:./\\n\\r]+");
        String[] correctWords = correct.trim().split("[ ,-:./\\n\\r]+");

        Log.i(TAG, "ingredientsTextComparison -> Start of comparing");
        Log.i(TAG, "ingredientsTextComparison -> correctWords.length == " + correctWords.length + ", extractedWords.length == " + extractedWords.length);

        final double similarityThreshold = 0.8; //threshold above which the word we are looking at is considered found
        final int checkMinWordLength = 3; //words with less than checkMinWordLength are ignored
        final int checkNotSeparatedMinLength = 6;

        float points = 0; //points are added each time a word is found
        int maxPoints = 0; //maximum points which is the number of characters of all words with more than 3 characters
        int posLastWordFound = 0; //index of the last word found
        int consecutiveNotFound = 0; //consecutive words not found since last word found

        LevenshteinStringDistance stringComparator = new LevenshteinStringDistance();


        //for each correct word
        for (String word : correctWords) {
            boolean found = false;
            int index = posLastWordFound;
            word = word.toLowerCase(); //ignoring case

            if (word.length() >= checkMinWordLength) { //ignoring short words
                maxPoints += word.length();

                for (int i = 0; i < extractedWords.length && !found; i++) {
                    //for each extracted words starting from posLastWordFound
                    index = (posLastWordFound + i) % extractedWords.length;


                    double similarity = stringComparator.getNormalizedSimilarity(word, extractedWords[index]);

                    //if similarity grater than similarityThreshold the word is found
                    if (similarity > similarityThreshold) {
                        if (points == 0 || i < consecutiveNotFound + 10) {
                            points += word.length()*similarity; //assign points based on number of characters
                        } else {
                            //if word found is distant from posLastWordFound the ocr text isn't ordered, less points
                            points += (float) word.length()*similarity/2;
                        }
                        Log.d(TAG, "ingredientsTextComparison -> \"" + word + "\" ==  \"" + extractedWords[index] + "\" similarity="+similarity);
                        extractedWords[index] = ""; //remove found word
                        found = true;
                    }
                }
            }

            //taking into consideration words that are not properly separated (e.g. "cetarylalcohol")
            if(!found && word.length() >= checkNotSeparatedMinLength){
                for(int i=0; i<extractedWords.length && !found; i++) {
                    index = (posLastWordFound+i)%extractedWords.length;
                    if (extractedWords[index].contains(word)) { //the correct word is contained in the extracted word
                        if(points==0 || i<consecutiveNotFound+10) {
                            points += word.length(); //assign points based on number of characters
                        } else {
                            points += (float)word.length()/2;
                        }
                        Log.d(TAG, "ingredientsTextComparison -> \"" + word + "\" contained in  \"" + extractedWords[index] + "\"");
                        extractedWords[index] = extractedWords[index].replace(word, ""); //remove found word
                        found = true;
                    }
                }
            }

            if(found){
                consecutiveNotFound = 0;
                posLastWordFound = index;
            } else {
                consecutiveNotFound++;
            }
        }
        float confidence = (points / maxPoints)*100;
        Log.i(TAG, "ingredientsTextComparison -> confidence == " + confidence + " (%)");

        if(Float.isNaN(confidence)) confidence = 0;

        return confidence;

    }


    /**
     * Class used to evaluate a single test
     * @author Luca Moroldo (g3)
     */
    class RunnableTest implements Runnable {

        /**
         * The test that will be evaluated
         */
        private TestElement test;

        /**
         * The JSONObject where will be added this test evaluation
         */
        private JSONObject jsonReport;

        /**
         * The countdownlatch that will be used to signal the end of the test
         */
        private CountDownLatch countDownLatch;


        /**
         * @param jsonReport JSONObject containing tests data that will be updated with current
         *                   'test'
         * @param test element of a test - must contain an image's path and ingredients field
         * @param countDownLatch used to signal the task completion
         */
        RunnableTest(JSONObject jsonReport, TestElement test, CountDownLatch countDownLatch) {
            this.jsonReport = jsonReport;
            this.test = test;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            //evaluate text extraction updating recognized text and confidence with the
            //recognition
            evaluateTest(test);

            //evaluate alterations if any updating, for each alteration, recognized text and
            // confidence with the extraction
            evaluateTestAlterations(test);

            try {
                //add the test using a synchronized function to avoid concurrency
                addTestElement(jsonReport, test);
            } catch (JSONException e) {
                //if a listener has been set, then signal the failure
                if(testListener != null) {
                    testListener.onTestFailure(TestListener.TEST_ADDITION_FAILURE, test.getFileName());
                }
                Log.e(TAG, "Failed to add test element '"
                        + test.getFileName() + " to json report");
            }

            //calls TestListener.onTestFinished (synchronized to avoid concurrency)
            signalTestFinished();

            //signal the end of the evaluation of this test
            countDownLatch.countDown();
        }


        /**
         * Evaluate a test setting the recognized text and the confidence with the recognition
         * @param test TestElement to evaluate
         * @modify test
         * @author Luca Moroldo
         */
        private void evaluateTest(TestElement test) {
            String imagePath = test.getImagePath();
            Bitmap testBitmap = Utils.loadBitmapFromFile(imagePath);

            //extract text from the image
            String ocrText = executeOcr(testBitmap);

            //compare extracted ingredients with real ingredients
            String correctIngredients = test.getIngredients();
            float confidence = ingredientsTextComparison(correctIngredients, ocrText);

            //credits Francesco Pham
            String ingredientsExtractionReport =
                    buildIngredientsExtractionReport(ocrText, correctIngredients);

            //insert results in test
            test.setConfidence(confidence);
            test.setRecognizedText(ocrText);
            test.setIngredientsExtraction(ingredientsExtractionReport);
        }


        /**
         * Execute ingredients extraction from recognized text and compare with correct ingredients.
         * Then build and return a report.
         * @param recognizedText Text recognized by the OCR
         * @param correctIngredientsText Text containing the correct ingredients separated by commas.
         * @return String report
         * @author Francesco Pham
         */
        private String buildIngredientsExtractionReport(String recognizedText, String correctIngredientsText){
            //extract ingredients from ocr text and from correctIngredientsText
            String correctedRecognizedText = textCorrector.correctText(recognizedText);
            List<Ingredient> extractedIngredients = ocrIngredientsExtractor.findListIngredients(correctedRecognizedText);
            List<Ingredient> correctListIngredients = correctIngredientsExtractor.findListIngredients(correctIngredientsText);

            //show extracted ingredients from ocr text
            StringBuilder extractionReport = new StringBuilder();
            extractionReport.append("Extracted: ");
            Iterator<Ingredient> iterator = extractedIngredients.iterator();
            while(iterator.hasNext()){
                extractionReport.append(iterator.next().getInciName());
                extractionReport.append(iterator.hasNext() ? ", " : "\n\n");
            }

            //show correct ingredients (extracted from correct ingredients string)
            extractionReport.append("Correct: ");
            iterator = correctListIngredients.iterator();
            while(iterator.hasNext()){
                extractionReport.append(iterator.next().getInciName());
                extractionReport.append(iterator.hasNext() ? ", " : "\n\n");
            }


            //compare extracted ingredients with correct ingredients
            int nCorrectExtractedIngreds = 0;
            for(Ingredient correct : correctListIngredients){
                iterator = extractedIngredients.iterator();
                while(iterator.hasNext()){
                    if(iterator.next().getCosingRefNo().equalsIgnoreCase(correct.getCosingRefNo())) {
                        nCorrectExtractedIngreds++;
                        iterator.remove();
                    }
                }
            }

            //make ingredients extraction statistics report
            int nWrongExtractedIngreds = extractedIngredients.size();
            float percent = (float)100*nCorrectExtractedIngreds / correctListIngredients.size();
            if(Float.isNaN(percent)) percent = 0;
            test.setPercentCorrectIngredients(percent);
            String percentCorrectIngreds = String.format("%.2f",percent);
            extractionReport.append(
                    "% correct ingredients extracted: "
                            + percentCorrectIngreds+"% \n"
                            + "# wrong ingredients extracted: "+nWrongExtractedIngreds);

            return extractionReport.toString();
        }

    }


    /**
     * Returns a HashMap of (Tag, Value) pairs where value is the average test result of the photos tagged with that Tag
     * @author Nicolò Cervo (g3) with the tutoring of Francesco Pham (g3)
     */
    private HashMap getTagsStats() {

        HashMap<String, Float> tagStats = new HashMap<>(); //contains the cumulative score of every tag
        HashMap<String, Integer> tagOccurrences = new HashMap<>(); //contains the number of occurrences of each tag

        for(TestElement element : testElements) {
            for (String tag : element.getTags()) {
                if(tagStats.containsKey(tag)) {
                    float newValue = tagStats.get(tag) + element.getConfidence();
                    tagStats.put(tag, newValue);
                    tagOccurrences.put(tag, tagOccurrences.get(tag) + 1);
                }else{
                    tagStats.put(tag, element.getConfidence());
                    tagOccurrences.put(tag, 1);
                }
            }
        }

        Log.i(TAG, "getTagStats():");
        for(String tag : tagStats.keySet()){
            tagStats.put(tag, tagStats.get(tag)/tagOccurrences.get(tag)); // average of the scores
            Log.i(TAG, "-" + tag + " score: " + tagStats.get(tag));
        }
        return tagStats;
    }

    /**
     * Calculate average of percentage of correct ingredients extracted from the ocr.
     * @return Average of percentage of correct ingredients extracted from the ocr.
     * @author Francesco Pham
     */
    private float getAverageCorrectIngredients(){
        float total = 0;
        for(TestElement element : testElements)
            total += element.getPercentCorrectIngredients();
        return total/testElements.size();
    }

    /**
     * Convert statistics returned by getTagsStats() into a readable text
     * @author Francesco Pham (g3)
     */
    public String getTagsStatsString() {
        HashMap tagsStats = getTagsStats();
        String report = "Average confidence by tags: \n";
        while(!tagsStats.isEmpty()){
            String keymin = getMinKey(tagsStats);
            report = report + keymin + " : " + tagsStats.get(keymin) + "%\n";
            tagsStats.remove(keymin);
        }

        HashMap alterationsTagsGainStats = getAlterationsTagsGainStats();
        report += "\nAvarage gain by alterations tags: \n";
        while(!alterationsTagsGainStats.isEmpty()) {
            String keymin = getMinKey(alterationsTagsGainStats);
            report = report + keymin + " : " + alterationsTagsGainStats.get(keymin) + "%\n";
            alterationsTagsGainStats.remove(keymin);
        }

        report += "\nAverage percentage correct ingredients extracted: "+getAverageCorrectIngredients() +" %";

        Log.d(TAG, "Tag stats: \n" + report);

        return report;

    }

    /**
     * Find and return key corresponding to minimum value of the map
     * @param map Map which has to be searched for the minimum value
     * @return Key corresponding to minimum value
     * @author Francesco Pham
     */
    private String getMinKey(Map<String, Float> map) {
        String minKey = null;
        float minValue = Float.MAX_VALUE;
        for(String key : map.keySet()) {
            float value = map.get(key);
            if(value < minValue) {
                minValue = value;
                minKey = key;
            }
        }
        return minKey;
    }
}
