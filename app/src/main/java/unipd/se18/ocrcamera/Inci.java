package unipd.se18.ocrcamera;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * @see <a href="https://en.wikipedia.org/wiki/International_Nomenclature_of_Cosmetic_Ingredients">International Nomenclature of Cosmetic ingredients (INCI)</a>
 */
public class Inci {

    //list where save the ingredients
    private ArrayList<String[]> listInci;
    //list where save the ingredients found
    private ArrayList<String[]> listIngredientFound;

    /**
     * Constructor
     * with loadDB it load Inci ingredients to arraylist
     * @param inputStream of the R.raw.database
     * @author Giovanni Fasan(g1)
     */
    public Inci(InputStream inputStream) {
        listInci = new ArrayList<String[]>();
        listIngredientFound = new ArrayList<String[]>();
        loadDB(inputStream);
    }

    public ArrayList<String[]> getListInci(){
        return this.listInci;
    }

    public ArrayList<String[]> getListIngredientFound(){
        return this.listIngredientFound;
    }


    /**
     * Load every Inci ingredients to arrayList
     * @param inputStream of the R.raw.database
     * @return void
     * @author Giovanni Fasan(g1)
     */
    private void loadDB(InputStream inputStream){
        //define 0 the name of the ingredients
        final int name=0;
        //define 1 the description of the ingredients
        final int description=1;
        //open Buffer Reader for read the database
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        try {
            //slide every line of the database and add in the appropriate list
            for(String ingredient = reader.readLine(); ingredient != null; ingredient = reader.readLine()){
                String[] inci=ingredient.split(";");
                String[] str = {inci[name].trim(), inci[description].trim()};
                listInci.add(str);
            }
            Log.d("inci", "create listInci");
        }catch (Exception e){
            String[] error = {"No Inci ingredients"};
            listInci.add(error);
        }
    }

    /**
     * Concatenate all the ingredients found in a string. It puts the ingredidients in bold
     * @return String with ingredients list of the label
     * @author Giovanni Fasan(g1)
     */

    public String ingredientsFoundToString() {
        //define 0 the name of the ingredients
        final int name=0;
        //define 1 the description of the ingredients
        final int description=1;
        StringBuilder inci = new StringBuilder("");
        //Inserts the name of the ingredient in bold and the description into the StringBuilder
        for (int j=0; j<this.listIngredientFound.size(); j++){
            inci.append("<b>" + listIngredientFound.get(j)[name] + "</b>:"+listIngredientFound.get(j)[description]+"\n");
        }

        return inci.toString();
    }

    /**
     * @param text String in which you have to find the ingredients
     * loads all the ingredients inci found in the ArrayList listIngredientFound
     * @author Giovanni Fasan(g1)
     */
    private void findIngredientsList_old(String text) {
        listIngredientFound = new ArrayList<String[]>();
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
     * loads all the ingredients inci found in the ArrayList listIngredientFound
     * @param text String in which you have to find the ingredients
     * @author Giovanni Fasan(g1)
     */
    public void findIngredientsList(String text) {
        listIngredientFound = new ArrayList<String[]>();
        //remove every \n and every :
        //split by Ingredients to reduce the number of words to be analyzed
        String[] noEnd = text.replaceAll("\n", " ").replaceAll(":", " ").split("Ingredients");
        if (noEnd.length==2){
            //if I have a length of the vector is 2, I take the second part of the vector, that is the one where there is the list of ingredients
            String [] word = noEnd[1].split(",");
            for (String w : word) {
                for (int j=0; j<this.listInci.size(); j++){
                    if (w.trim().toUpperCase().equals(listInci.get(j))) {
                        listIngredientFound.add(listInci.get(j));
                    }
                }
            }
        }else{
            //in case there are more words Ingredients are forced to scroll and search all vectors to see if I find more lists of ingredients
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
