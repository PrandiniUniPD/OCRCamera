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
 * Class for Inci database management
 * The database is in CSV format, using openCSV to parse the file.
 * For more informations about openCSV:
 * http://opencsv.sourceforge.net/
 * @author Francesco Pham
 */
public class Inci {

    private static final String TAG = "Inci";

    /**
     * Load inci database and return list of Ingredient objects
     * @param context The application context
     * @author Francesco Pham
     */
    public static List<Ingredient> getListIngredients(Context context){
        InputStream inputStream = context.getResources().openRawResource(R.raw.incidb);
        Reader reader = new BufferedReader(new InputStreamReader(inputStream));

        List<Ingredient> listIngredients = new ArrayList<Ingredient>(); //initializing list of ingredients

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

        return listIngredients;
    }

}
