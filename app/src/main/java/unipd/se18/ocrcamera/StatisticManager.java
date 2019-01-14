package unipd.se18.ocrcamera;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unipd.se18.ocrcamera.inci.Ingredient;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.System.err;

/**
 * Class that contains all the method related to the statistics about ingredients
 */
public class StatisticManager {

    /**
     * App Context
     */
    private Context context;

    public StatisticManager(Context context) {
        this.context = context;
    }


    /**
     * Method to save the map in properties
     * @param map the hashMap containing the list of the ingredients
     * @author Leonardo Pratesi
     */
    public void saveMap(HashMap<String, Integer> map) {

        try {
            FileOutputStream fos = context.openFileOutput("hashmap.ser", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
            fos.close();
            Log.e("hashsave", map.toString());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method to load the map from file
     * @return HashMap<String, Integer>
     * @author Leonardo Pratesi
     */
    //TODO check exceptions
    public HashMap<String, Integer> loadMap() {
        HashMap<String, Integer> map = null;
        try {
            FileInputStream fis = context.openFileInput("hashmap.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            if (fis != null) {
                try {
                    map = (HashMap) ois.readObject();
                    ois.close();
                    fis.close();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            // if the file is not found creates an empty map
            return new HashMap<>();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * update the map and saves it
     * @param
     * @param ingredients
     * @author Leonardo Pratesi
     *
     */
    public void updateMap(List<Ingredient> ingredients) {
        if (ingredients != null){
            HashMap<String, Integer> temp = loadMap();
            for (Ingredient s : ingredients) {
                if (temp.containsKey(s.getInciName().toUpperCase()))
                    temp.put(s.getInciName(), temp.get(s.getInciName()) + 1);

                else {
                    temp.put(s.getInciName(), 1);
                }
            }
            saveMap(temp);
        }
        else {}
    }

    /**
     * Method to reset all the ingredients
     * @author Leonardo Pratesi
     */
    public void resetStats() {

        HashMap<String, Integer> temp = new HashMap();
        saveMap(temp);

    }
}

