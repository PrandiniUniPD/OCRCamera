package unipd.se18.ocrcamera.inci;

import android.util.Log;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Class for Inci database management
 * The database is in CSV format, using openCSV to parse the file.
 * For more informations about openCSV:
 * http://opencsv.sourceforge.net/
 * @author Francesco Pham
 */
public class Inci {

    private static final String TAG = "Inci";

    private static final int COL_COSING_REF_NO = 0;
    private static final int COL_INCI_NAME = 1;
    private static final int COL_DESCRIPTION = 6;
    private static final int COL_FUNCTION = 8;

    /**
     * Load inci database and return list of Ingredient objects
     * @param inciDbStream InputStream from inci db csv file
     * @author Francesco Pham
     */
    public static ArrayList<Ingredient> getListIngredients(InputStream inciDbStream){
        Reader reader = new BufferedReader(new InputStreamReader(inciDbStream));

        ArrayList<Ingredient> listIngredients = new ArrayList<>(); //initializing list of ingredients

        //initialize openCSV reader
        CSVReader csvReader = new CSVReader(reader);

        //skip first line containing field names
        try {
            csvReader.skip(1);
        } catch (IOException e) {
            Log.e(TAG, "Error skipping first line");
        }

        String[] line;

        //for each line in the csv add an Ingredient object to the list
        try {
            while ((line = csvReader.readNext()) != null) {
                if(line.length > COL_FUNCTION) {
                    Ingredient element = new Ingredient();
                    element.setCosingRefNo(line[COL_COSING_REF_NO]);
                    element.setInciName(line[COL_INCI_NAME]);
                    element.setDescription(line[COL_DESCRIPTION]);
                    element.setFunction(line[COL_FUNCTION]);
                    listIngredients.add(element);
                }
                else Log.d(TAG, "There is an empty line in the database file, line "+csvReader.getLinesRead());
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
