package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import com.opencsv.CSVReader;

import unipd.se18.ocrcamera.inci.Ingredient;



/**
 * Class that contains the methods to read the allergens from a file, create an allergen list,
 * find the allergens in the recognized ingredients from the OCR.
 */
class AllergensManager {

    private final int COMMON_NAME = 0;
    private final int INCI_NAMES = 1;

    private ArrayList<Allergen> allergensList = new ArrayList<>();

    private Context context;
    private SharedPreferences sp = null;

    /**
     * constructor
     * @param cntx context of the app/calling activity
     */
    AllergensManager(Context cntx) {

        context = cntx;
        sp = context.getSharedPreferences("unipd.se18.ocrcamera.SELECTED_ALLERGENS", Context.MODE_PRIVATE);
        readAllergensdb();

    }

    /**
     * loads the allergens contained in raw resource file allergendb.csv into allergensList
     */
    private  void readAllergensdb() {

        InputStream inputStream = context.getResources().openRawResource(R.raw.allergendb);
        InputStreamReader allergenDbReader = new InputStreamReader(inputStream);
        CSVReader csvReader = new CSVReader(allergenDbReader);

        String line[];
        String commonName;

        try {
            while ((line = csvReader.readNext()) != null) {

                Allergen allergen = new Allergen();
                String inciNames[];

                // get the Allergen fields from line[]
                commonName = line[COMMON_NAME].replace("\"", "");
                inciNames = line[INCI_NAMES].replace("\"", "").split(", ");

                // set the fields
                allergen.setCommonName(commonName);
                allergen.setInciNames(inciNames);

                allergensList.add(allergen);
            }

        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This method returns an list of Allergen objects from allergendb.csv
     * @return allergensList, the converted ArrayList of Allergen objects
     * @modify allergensList by putting all allergens in it
     * @author Cervo Nicolò
     */
    public ArrayList<Allergen> getAllergensList() {

        return allergensList;
    }

    /**
     * parses through selected_allergens to return the list of selected allergens
     * @return selectedAllergensList, ArrayList<Allergen> of selected allergens
     */
    public ArrayList<Allergen> getSelectedAllergensList() {

        Map<String, ?> selectedAllergens = sp.getAll();
        ArrayList<Allergen> selectedAllergensList = new ArrayList<>();

        for(Map.Entry<String, ?> entry : selectedAllergens.entrySet()) {

            Allergen allergen = new Allergen();
            allergen.setCommonName(entry.getKey());
            allergen.setInciNames(entry.getValue().toString().split(", "));
            selectedAllergensList.add(allergen);

        }
        return  selectedAllergensList;
    }

    /**
     * compares the list of allergens to the ingredient ingr
     * @param ingr, a List of the ingredients (recognized form the Ocr text)
     *        to be compared to the allergens
     * @return foundAllergens, ArrayList<Allergen> containing the allergens found in the inciName or
     *         in the description of ingr, empty list if none is found
     */
    public ArrayList<Allergen> checkForAllergens(Ingredient ingr) {
        ArrayList<Allergen> foundAllergens = new ArrayList<>();

        for(Allergen allergen : allergensList) {

            // for every inciName associated with this allergen
            for(String allergenInciName : allergen.getInciNames()) {

                // if the allergen commonName or inciName appears in the ingredient inciName or
                // description respectively
                if(ingr.getInciName().contains(allergenInciName) ||
                        ingr.getDescription().contains(allergen.getCommonName())) {

                    // add the allergen to foundAllergens
                    foundAllergens.add(allergen);

                    // if the allergen is found go to the next one
                    break;
                }
            }
        }
        return foundAllergens;
    }

    /**
     * Check if an ingredient contains a selected allergen common name or inci name
     * @param ingr, ingredient to check for allergens
     * @return the allergen found in the ingredient, null if no allergen is found
     */
    public ArrayList<Allergen> checkForSelectedAllergens(Ingredient ingr) {

        Map<String, ?> selectedAllergens = sp.getAll();
        Allergen allergen = new Allergen();
        ArrayList<Allergen> foundSelectedAllergens = new ArrayList<>();

        for(Map.Entry<String, ?> entry : selectedAllergens.entrySet()) {
            for(String inciName : entry.getValue().toString().split(", ")) {
                if(ingr.getInciName().contains(inciName) ||
                        ingr.getDescription().contains(entry.getKey())) {

                    allergen.setInciNames(entry.getValue().toString().split(", "));
                    allergen.setCommonName(entry.getKey());
                    foundSelectedAllergens.add(allergen);
                }
            }
        }
        return foundSelectedAllergens;
    }

    /**
     * adds allergen to the SharedPreferences file selected_allergens
     * @param allergen, the Allergen to add
     * @modify selected_allergens
     */
    public void selectAllergen(Allergen allergen) {

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(allergen.getCommonName(), allergen.inciNamesString());
        editor.apply();
    }

    /**
     * removes an allergen from the SharedPreferences file selected_allergens
     * @param allergen, the Allergen to remove
     * @modify selected_allergens
     */
    public void deselectAllergen(Allergen allergen) {

        SharedPreferences.Editor editor = sp.edit();
        editor.remove(allergen.getCommonName());
        editor.apply();
    }

    /**
     * updates the sharedpreferences file selected_allergens with the new allergen list newSelectedAllergens
     * @param newSelectdAllergens, new list of selected allergens
     */
    public void updateSelectedAllergens(ArrayList<Allergen> newSelectdAllergens) {

        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        for (Allergen allergen : newSelectdAllergens) {
            editor.putString(allergen.getCommonName(), allergen.inciNamesString());
        }
        editor.apply();
    }
}
