package unipd.se18.ocrcamera;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

        catch (IOException e) {
            e.printStackTrace();
            return null;
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
    //TODO ask if cheking null here or when the method is called
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
        else {
            //no ingredients found
        }
        }


    /**
     * Method to reset all the ingredients
     * @author Leonardo Pratesi
     */
    public void resetStats() {

        HashMap<String, Integer> temp = new HashMap<>();
        saveMap(temp);

    }

    /**
     * Method to sort the map by value -
     */
    //TODO do this method (should check that does not sort the wrong map)
    //TODO why choose HashMap instead of Map?
    public HashMap<String, Integer> sortMap(HashMap<String, Integer> hashMap)  {
            List<Map.Entry<String, Integer>> list = new LinkedList(hashMap.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

            HashMap<String, Integer> result = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : list) {
                result.put(entry.getKey(), entry.getValue());
            }
            Log.i("sort", "sorted");
            return result;
        }
}


