package unipd.se18.ocrcamera;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

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

    /*
        threshold of similarity between the found text and the ingredient name inside db
        below this threshold the ingredient is considered not found inside db therefore not pushed into the list
    */
    private static final double similarityThreshold = 0.6;

    /*
        Set of algorithms available for findListIngredients.
        LEVENSHTEIN_DISTANCE: splits the ocr text and for each block of text search
                            inside INCI DB for the ingredient with minimum distance using Levenshtein.
        TEXT_PRECORRECTION: try to correct the text using a wordlist then for each ingredient inside
                            INCI DB search if it is contained inside the corrected text
     */
    public enum Algorithm {TEXT_PRECORRECTION, LEVENSHTEIN_DISTANCE}

    private Context context;

    /**
     * constructor
     * @param inputStream inputStream of the inci database file
     * @author Francesco Pham
     */
    public Inci(Context context, InputStream inputStream){
        this.context = context;
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
    }

    /**
     * Find the best matching ingredient in the database using weighted levenshtein algorithm
     * @param ingredient The ingredient we are looking for
     * @return Ingredient object that contains the most similar ingredient to the text taken from ocr
     * @author Francesco Pham
     */
    private Ingredient findBestMatchingIngredient(String ingredient){
        //ignoring case by converting to upper case like all texts in database
        ingredient = ingredient.toUpperCase();

        LevenshteinStringDistance stringComparator = new LevenshteinStringDistance();
        double maxSimilarity = -1;
        int bestMatchingIngredient = -1;
        for(int i = 0; i< listIngredients.size(); i++){
            double similarity = stringComparator.getNormalizedSimilarity(listIngredients.get(i).getInciName(), ingredient);
            if(similarity>maxSimilarity) {
                maxSimilarity = similarity;
                bestMatchingIngredient = i;
            }
        }

        //store similarity in the object for later usage
        listIngredients.get(bestMatchingIngredient).setOcrTextSimilarity(maxSimilarity);

        return listIngredients.get(bestMatchingIngredient);
    }

    /**
     * this method takes the entire OCR text and split it using commas and calls the findBestMatchingIngredient method
     * @param text The entire OCR text
     * @return List of Ingredient objects where are stored ingredient's informations
     * @author Francesco Pham
     */
    public ArrayList<Ingredient> findListIngredients(String text, Algorithm algorithm){

        //initializing the list
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();

        switch (algorithm) {
            case LEVENSHTEIN_DISTANCE: {
                String[] splittedText = text.trim().split("[,.]+"); //split removing whitespaces

                //for every splitted text inside the ocr text search for the most similar in the inci db
                for (String str : splittedText) {
                    Ingredient bestMatchingIngredient = findBestMatchingIngredient(str);

                    //discard ingredients that not satisfy similarityThreshold
                    if (bestMatchingIngredient.getOcrTextSimilarity() > similarityThreshold) {
                        //set the original text taken from ocr for later
                        bestMatchingIngredient.setFoundText(str);

                        //add the ingredient object to the list
                        ingredients.add(bestMatchingIngredient);
                    }
                }
            }

            case TEXT_PRECORRECTION: {
                TextAutoCorrection corrector = new TextAutoCorrection(context);
                text = corrector.correctText(text);
                for(Ingredient ingredient : listIngredients){
                    if(text.contains(ingredient.getInciName())){
                        ingredients.add(ingredient);
                    }
                }
            }
        }

        return ingredients;
    }
}
