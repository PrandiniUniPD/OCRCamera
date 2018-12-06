package unipd.se18.ocrcamera;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Inci extends AppCompatActivity {

    private List listInci;        //list where save the ingredients found
    

    public Inci() {
        listInci = new ArrayList();
    }

    public List getDb(){
        return this.listInci;
    }


    /**
     * @author Giovanni Fasan(g1)
     * @param inputStream of the R.raw.database
     * @return void
     * Load every Inci ingredients to arrayList
     */
    public void loadDB(InputStream inputStream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        String ingredient;
        try {
            while ((ingredient = reader.readLine()) != null) {//defines ingredient as each row of the database
                String[] inci=ingredient.split(",");
                listInci.add(inci[0]);
            }
            Log.d("inci", "create listInci");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @author Giovanni Fasan(g1)
     * @param text String in which you have to find the ingredients
     * @return String with ingredients list of the label
     */

    public String findIngredients(String text) {
        String inci="";
        String noEnd = text.replaceAll("\n", " ");
        String [] word = noEnd.split(",");
        for (int i = 0; i < word.length; i++) {
            Log.d("inci", word[i]);
            for (int j=0; j<this.listInci.size(); j++){       //defines ingredient as each row of the database
                if (word[i].trim().toUpperCase().equals(listInci.get(j))) {
                    inci = inci + "<b>" + listInci.get(j) + "</b>; ";
                }
            }
        }

        return inci;
    }

    /**
     * @author Giovanni Fasan(g1)
     * @param text String in which you have to find the ingredients
     * @return List of ingredients list of the label
     */
    public List findIngredientsList(String text) {
        List inci = new ArrayList();

        String noSpace = text.replaceAll("\n", " ");
        String [] word = noSpace.split(",;");
        for (int i = 0; i < word.length; i++) {
            for (int j=0; j<this.listInci.size(); j++){       //defines ingredient as each row of the database
                if (word[i].trim().toUpperCase().equals(listInci.get(j))) {
                    inci.add(listInci.get(j));
                }
            }
        }

        return inci;
    }

}
