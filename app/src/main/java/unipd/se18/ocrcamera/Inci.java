package unipd.se18.ocrcamera;

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

    private List<Ingredient> listIngredients;
    private static final String TAG = "Inci";

    /**
     * constructor
     * @param inputStream inputStream of the inci database file
     * @author Francesco Pham
     */
    public Inci(InputStream inputStream){
        Reader reader = new BufferedReader(new InputStreamReader(inputStream));
        /*
        ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
        strategy.setType(Ingredient.class);
        String[] memberFieldsToBindTo = {"cosingRefNo", "inciName", "innName", "phEurName", "casNo", "ecNo", "description", "restriction", "function", "updateDate"};
        strategy.setColumnMapping(memberFieldsToBindTo);

        CsvToBean<Ingredient> csvToBean = new CsvToBeanBuilder(reader)
                .withMappingStrategy(strategy)
                .withSkipLines(1)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        listIngredients = csvToBean.parse();*/

        listIngredients = new ArrayList<Ingredient>(); //initializing list of ingredients

        //initializing openCSV reader
        CSVReader csvReader = new CSVReader(reader);
        String[] line;

        //for each line in the csv add an Ingredient object to the list
        try {
            while ((line = csvReader.readNext()) != null) {
                Ingredient element = new Ingredient();
                element.setCosingRefNo(line[0]);

                if(line.length > 1)
                    element.setInciName(line[1]);
                else
                    Log.d(TAG, "inci name not found while parsing "+element.getCosingRefNo());

                if(line.length > 6)
                    element.setDescription(line[6]);
                else
                    Log.d(TAG, "description not found while parsing "+element.getCosingRefNo());

                listIngredients.add(element);
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

    /*
    public Inci(InputStream inputStream){
        listIngredients = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                Ingredient ingredient = new Ingredient(row[0],row[1],row[2],row[3],row[4],row[5],
                                                        row[6],row[7],row[8],row[9]);
                listIngredients.add(ingredient);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
    }
    */

    /**
     * Find the best matching ingredient in the database using weighted levenshtein algorithm
     * @param ingredient The ingredient we are looking for
     * @return Ingredient object that contains the most similar ingredient to the text taken from ocr
     * @author Francesco Pham
     */
    private Ingredient findBestMatchingIngredient(String ingredient){
        LevenshteinStringComparator stringComparator = new LevenshteinStringComparator();
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
    public ArrayList<Ingredient> findListIngredients(String text){
        String[] splittedText = text.trim().split("[,.]+"); //split removing whitespaces
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>(); //initializing the list

        //for every splitted text inside the ocr text search for the most similar in the inci db
        for(String str : splittedText){
            Ingredient bestMatchingIngredient = findBestMatchingIngredient(str);

            //set the original text taken from ocr for later
            bestMatchingIngredient.setFoundText(str);

            //add the ingredient object to the list
            ingredients.add(bestMatchingIngredient);
        }
        return ingredients;
    }
}
