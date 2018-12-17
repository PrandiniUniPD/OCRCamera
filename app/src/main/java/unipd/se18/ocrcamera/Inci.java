package unipd.se18.ocrcamera;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Inci {

    //list where save the ingredients
    private ArrayList<String> listInci;
    //list where save the description of ingredients
    private ArrayList<String> listInciDescription;
    //list where save the ingredients found
    private ArrayList<String> listIngredientFound;

    /**
     * @author Giovanni Fasan(g1)
     * Constructor
     * @param inputStream of the R.raw.database
     * with loadDB it load Inci ingredients to arraylist
     */
    public Inci(InputStream inputStream) {
        listInci = new ArrayList<String>();
        listInciDescription = new ArrayList<String>();
        listIngredientFound = new ArrayList<String>();
        loadDB(inputStream);
    }

    public ArrayList<String> getListInci(){
        return this.listInci;
    }

    public ArrayList<String> getListInciDescription(){
        return this.listInciDescription;
    }

    public ArrayList<String> getListIngredientFound(){
        return this.listIngredientFound;
    }


    /**
     * @author Giovanni Fasan(g1)
     * @param inputStream of the R.raw.database
     * @return void
     * Load every Inci ingredients to arrayList
     */
    private void loadDB(InputStream inputStream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        String ingredient;
        try {
            while ((ingredient = reader.readLine()) != null) {
                String[] inci=ingredient.split(";");
                listInci.add(inci[0].trim());
                //listInciDescription.add(inci[1].trim());
            }
            Log.d("inci", "create listInci");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @author Giovanni Fasan(g1)
     * @return String with ingredients list of the label
     * Concatenate all the ingredients found in a string. It puts the ingredidients in bold
     */

    public String ingredientsFoundToString() {
        String inci="";
        for (int j=0; j<this.listIngredientFound.size(); j++){
            inci = inci + "<b>" + listIngredientFound.get(j) + "</b>; ";
        }

        return inci;
    }

    /**
     * @author Giovanni Fasan(g1)
     * @param text String in which you have to find the ingredients
     * loads all the ingredients inci found in the ArrayList listIngredientFound
     */
    public void findIngredientsList(String text) {
        listIngredientFound = new ArrayList<String>();
        String noEnd = text.replaceAll("\n", " ");
        String [] word = noEnd.split(",");
        for (int i = 0; i < word.length; i++) {
            for (int j=0; j<this.listInci.size(); j++){
                if (word[i].trim().toUpperCase().equals(listInci.get(j))) {
                    listIngredientFound.add(listInci.get(j));
                }
            }
        }

    }

}
