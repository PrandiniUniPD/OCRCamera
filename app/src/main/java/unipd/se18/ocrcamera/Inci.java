package unipd.se18.ocrcamera;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

public class Inci {

    private List<Ingredient> listInci;

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

        listInci = csvToBean.parse();*/

        listInci = new ArrayList<Ingredient>();
        CSVReader csvReader = new CSVReader(reader);
        String[] line;
        try {
            while ((line = csvReader.readNext()) != null) {
                Ingredient element = new Ingredient(line[0],line[1],line[2],line[3],line[4],line[5],line[6],line[7],line[8],line[9]);
                listInci.add(element);
            }
            reader.close();
            csvReader.close();
        } catch(Exception e){}
    }

    /*
    public Inci(InputStream inputStream){
        listInci = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                Ingredient ingredient = new Ingredient(row[0],row[1],row[2],row[3],row[4],row[5],
                                                        row[6],row[7],row[8],row[9]);
                listInci.add(ingredient);
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

    private Ingredient findBestMatchingIngredient(String ingredient){
        LevenshteinStringComparator stringComparator = new LevenshteinStringComparator();
        double maxSimilarity = -1;
        int bestMatchingIngredient = -1;
        for(int i=0; i<listInci.size(); i++){
            double similarity = stringComparator.getNormalizedSimilarity(listInci.get(i).getInciName(), ingredient);
            if(similarity>maxSimilarity) {
                maxSimilarity = similarity;
                bestMatchingIngredient = i;
            }
        }
        return listInci.get(bestMatchingIngredient);
    }

    public ArrayList<Ingredient> findListIngredients(String text){
        String[] splittedText = text.trim().split("\\s*,\\s*"); //split removing whitespaces
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        for(String str : splittedText){
            Ingredient bestMatchingIngredient = findBestMatchingIngredient(str);
            bestMatchingIngredient.setFoundText(str);
            ingredients.add(bestMatchingIngredient);
        }
        return ingredients;
    }
}
