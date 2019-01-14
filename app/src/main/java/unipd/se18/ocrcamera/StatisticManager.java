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

import unipd.se18.ocrcamera.inci.Ingredient;

import static android.content.Context.MODE_PRIVATE;

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
     * @param hmap
     * @author Leonardo Pratesi
     */
    public void saveMap(HashMap hmap) {

        try {
            FileOutputStream fos = context.openFileOutput("hashmap.ser", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(hmap);
            oos.close();
            fos.close();
            Log.e("hashsave", hmap.toString());
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

        HashMap temp = loadMap();
        for (Ingredient s : ingredients) {
            if (temp.containsKey(s.getInciName().toUpperCase()))
                temp.put(s.getInciName(), (Integer)temp.get(s.getInciName()) + 1);

            else {
                temp.put(s.getInciName(), 1);
            }
        }
        saveMap(temp);

    }

    /**
     * Method to reset all the ingredients
     *
     */
    private void resetStats() {
        HashMap temp = null;
        saveMap(temp);

    }
}

