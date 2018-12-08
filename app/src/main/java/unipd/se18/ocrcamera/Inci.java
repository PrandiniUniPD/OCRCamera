package unipd.se18.ocrcamera;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

public class Inci {

    private List<Ingredient> listInci;

    public Inci(InputStream inputStream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        CsvToBean<Ingredient> csvToBean = new CsvToBeanBuilder(reader)
                .withType(Ingredient.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        listInci = csvToBean.parse();
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
        double maxSimilarity = 0;
        Ingredient bestMatchingIngredient = null;
        for(Ingredient ingr : listInci){
            double similarity = stringComparator.getNormalizedSimilarity(ingr.getInciName(), ingredient);
            if(similarity>maxSimilarity) {
                maxSimilarity = similarity;
            }
            bestMatchingIngredient = ingr;
        }
        return bestMatchingIngredient;
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
