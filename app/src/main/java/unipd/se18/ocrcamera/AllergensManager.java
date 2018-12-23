package unipd.se18.ocrcamera;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVReader;

import org.apache.commons.collections.ArrayStack;

import unipd.se18.ocrcamera.inci.IngredientsExtractor;


/**
 * Class that contains the methods to read the allergens from a file, create an allergen list,
 * find the allergens in the recognized ingredients from the OCR.
 */
class AllergensManager {

    private static final int COMMON_NAME = 0;
    private static final int INCI_NAMES = 1;
    private static final int SELECTION = 2;

    static ArrayList<Allergen> allergensList = new ArrayList<>();

    /**
     * This method returns an list of Allergen objects from allergendb.csv
     * @param allergenDbStream, an InputStream containing the list of allergens from allergendb.csv
     * @return allergensList, the converted ArrayList of Allergen objects
     * @modify allergensList by putting all allergens in it
     * @author Cervo Nicolò
     * @author Balzan Pietro
     */
    public static ArrayList<Allergen> getAllergensList(InputStream allergenDbStream) {

        List<Allergen> allergenList = new ArrayList<>();

        InputStreamReader allergenDbReader = new InputStreamReader(allergenDbStream);

        CSVReader csvReader = new CSVReader(allergenDbReader);

        String line[];
        String inciNames[];
        String commonName;
        boolean selection;

        try {
            while ((line = csvReader.readNext()) != null) {
                Allergen allergen = new Allergen();

                commonName = line[COMMON_NAME].replace("\"", "");
                inciNames = line[INCI_NAMES].replace("\"", "").split(", ");
                selection = (line[SELECTION] == "selected");

                allergen.setCommonName(commonName);
                allergen.setInciNames(inciNames);
                allergen.setSelection(selection);

                allergenList.add(allergen);
            }

        } catch(IOException e){
            e.printStackTrace();
        }

        return allergensList;
    }

    /**
     * this method compares the list of allergens to the list of recognized ingredients
     * @param ingredients a List of the ingredients (recognized form the Ocr text)
     *                    to be compared to the allergens
     * @return refNoList, ArrayList<String> of cosingRefNo of the ingredients containing allergens
     * @author Cervo Nicolò
     * @author Balzan Pietro
     */
    public static ArrayList<String> findAllergens(List ingredients) {
        ArrayList<String> refNoList= new ArrayList<>();

        return refNoList;
    }
}
