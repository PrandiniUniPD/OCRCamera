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
        //define 0 the name of the ingredients
        int name=0;
        //define 1 the description of the ingredients
        int description=1;
        //open Buffer Reader for read the database
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        String ingredient;
        try {
            //slide every line of the database and add in the appropriate list
            while ((ingredient = reader.readLine()) != null) {
                String[] inci=ingredient.split(";");
                listInci.add(inci[name].trim());
                //listInciDescription.add(inci[description].trim());
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
    public void findIngredientsList_old(String text) {
        listIngredientFound = new ArrayList<String>();
        //remove every \n
        //split by Ingredients to reduce the number of words to be analyzed
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


    /**
     * @author Giovanni Fasan(g1)
     * @param text String in which you have to find the ingredients
     * loads all the ingredients inci found in the ArrayList listIngredientFound
     */
    public void findIngredientsList(String text) {
        listIngredientFound = new ArrayList<String>();
        //remove every \n and every :
        //split by Ingredients to reduce the number of words to be analyzed
        String[] noEnd = text.replaceAll("\n", " ").replaceAll(":", " ").split("Ingredients");
        if (noEnd.length<=2){
            //use 1 because if I have a length of the vector <= 2, I take the second part of the vector, that is the one where there is the list of ingredients
            String [] word = noEnd[1].split(",");
            Log.d("inci", "<=2");
            for (String w : word) {
                for (int j=0; j<this.listInci.size(); j++){
                    if (w.trim().toUpperCase().equals(listInci.get(j))) {
                        listIngredientFound.add(listInci.get(j));
                    }
                }
            }
        }else{
            //in case there are more words Ingredients are forced to scroll and search all vectors to see if I find more lists of ingredients
            Log.d("inci", ">2");
            for (String par : noEnd){
                String [] word = par.split(",");
                for (String w : word) {
                    for (int j=0; j<this.listInci.size(); j++){
                        if (w.trim().toUpperCase().equals(listInci.get(j))) {
                            listIngredientFound.add(listInci.get(j));
                        }
                    }
                }
            }
        }
    }

}
