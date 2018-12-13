package unipd.se18.ocrcamera;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.opencsv.CSVReader;

import edu.gatech.gtri.bktree.BkTreeSearcher;
import edu.gatech.gtri.bktree.Metric;
import edu.gatech.gtri.bktree.MutableBkTree;

/**
 * Class for compare ingredients taken from OCR text with INCI database.
 * The database is in CSV format, using openCSV to parse the file.
 * For more informations about openCSV:
 * http://opencsv.sourceforge.net/
 * @author Francesco Pham
 */
public class Inci {

    //list of recognized ingredients where are stored informations about ingredients
    private List<Ingredient> listIngredients;

    private static final String TAG = "Inci";

    private TextAutoCorrection corrector;

    private BkTreeSearcher<String> inciNameSearcher;

    /**
     * Constructor loads inci database
     * @param context The application context
     * @author Francesco Pham
     */
    public Inci(Context context){
        InputStream inputStream = context.getResources().openRawResource(R.raw.incidb);
        Reader reader = new BufferedReader(new InputStreamReader(inputStream));

        listIngredients = new ArrayList<Ingredient>(); //initializing list of ingredients

        //initializing openCSV reader
        CSVReader csvReader = new CSVReader(reader);
        String[] line;

        //for each line in the csv add an Ingredient object to the list
        try {
            while ((line = csvReader.readNext()) != null) {
                if(line.length > 6) {
                    Ingredient element = new Ingredient();
                    element.setCosingRefNo(line[0]);
                    element.setInciName(line[1]);
                    element.setDescription(line[6]);
                    listIngredients.add(element);
                }
                else System.out.println("There is an empty line in the database file, line "+csvReader.getLinesRead());
            }

        } catch(IOException e){
            Log.e(TAG, "Error trying to read csv");
        }

        //closing
        try {
            reader.close();
            csvReader.close();
        } catch(IOException e){
            Log.e(TAG, "Error closing csv reader");
        }

        //sort by inci name
        Collections.sort(listIngredients, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient o1, Ingredient o2) {
                return o1.compareTo(o2.getInciName());
            }
        });

        corrector = new TextAutoCorrection(context);
    }


    /**
     * Constructor loads inci database.
     * @param context The application context
     * @param usingTextSplitSearch Set this to true if TEXT_SPLIT search method is used.
     * @author Francesco Pham
     */
    public Inci(Context context, boolean usingTextSplitSearch){
        this(context);

        if(usingTextSplitSearch){
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
    }



    /*
        Set of algorithms available for findListIngredients.
        LEVENSHTEIN_DISTANCE: splits the ocr text and for each block of text search
                            inside INCI DB for the ingredient with minimum distance using Levenshtein.
        TEXT_PRECORRECTION: try to correct the text using a wordlist then for each ingredient inside
                            INCI DB search if it is contained inside the corrected text
    */
    public enum SearchMethod {TEXT_PRECORRECTION, TEXT_SPLIT}


    /**
     * This method extract ingredients from the ocr text and returns the list of ingredients.
     * @param text The entire OCR text
     * @return List of Ingredient objects where are stored ingredient's informations
     * @author Francesco Pham
     */
    public ArrayList<Ingredient> findListIngredients(String text, SearchMethod method){

        //initializing the list
        ArrayList<Ingredient> foundIngredients = new ArrayList<Ingredient>();

        switch (method) {
            case TEXT_SPLIT: {

                //maximum accepted distance between block of text and inci name
                final double maxDistance = 0.2;

                if(inciNameSearcher == null) {
                    Log.e(TAG, "inciNameSearcher is null, try set usingTextSplitSearch on constructor");
                    return foundIngredients;
                }

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

                    //add the ingredient object to list
                    if(found) {
                        Log.d(TAG, "found "+ingredientName+" in "+str+". Distance="+minDistance);
                        int indexBestIngredient = Collections.binarySearch(listIngredients, ingredientName);
                        if(indexBestIngredient >= 0)
                            foundIngredients.add(listIngredients.get(indexBestIngredient));
                        else
                            Log.e(TAG, "Couldn't find ingredient, this is strange.");
                    }
                }
            }

            case TEXT_PRECORRECTION: {
                text = corrector.correctText(text);
                for(Ingredient ingredient : listIngredients){
                    if(text.contains(ingredient.getInciName())){
                        foundIngredients.add(ingredient);
                    }
                }
            }
        }

        return foundIngredients;
    }

}
