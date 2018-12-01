package unipd.se18.ocrcamera;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static android.support.constraint.Constraints.TAG;

//All methods are static so no need for an dedicated class, i made it to have a clearer code while im writing it
//The methods have to be added to the class that calls them
//The class refers to TestEntry class
//The class can receive only a particular type of JSONObject to have a idea see the example at this link:
// https://ingsw18.slack.com/archives/CDFU418Q4/p1541678187026900

public class JSONReportParser {


    /**
     * Convert a string in JSONObject and call metod JSONReader
     * @param fileContent a String in JSON format
     * @return TestEntry[] an array of Test Entry with all the JSONObject value inside or null if fileContent is not in JSON format
     * @author Giovanni Furlan (gr2)
     */
    public static TestEntry[] parseReport(String fileContent)
    {
        TestEntry[] array = null;
        try
        {
            JSONObject json = new JSONObject(fileContent);
            array = retrieveEntries(json);
        }
        catch(JSONException e)
        {
            Log.e(TAG, "Error in parseReport");
        }
        return array;
    }



    /**
     * Get a JSONObject with keys linked to JSONObjects A that contains the informations.
     * Put informations of a JSONObject A in a TestEntry, do it for all JSONObject and return all the TestEntry in an array
     * @param json JSONObject file extracted by PhotoTester class
     * @return TestEntry[] an array of Test Entry with all the JSONObject inside
     * @author Giovanni Furlan (gr2) - modified by Leonardo Rossi (g2)
     */
    private static TestEntry[] retrieveEntries(JSONObject json)
    {

        TestEntry[] entryArray = new TestEntry[json.length()];
        try
        {
            //Iterator that slides all JSONObject inside the param
            Iterator<String> keys = json.keys();
            int i = 0;
            while(keys.hasNext())
            {
                //Add all the information inside a TestEntry
                JSONObject jsonDetails = json.getJSONObject(keys.next());
                TestEntry entry = extractValues(jsonDetails);
                entryArray[i]=entry;
                i++;
            }
        }
        catch(JSONException e)
        {
            Log.e(TAG, "Error in JSONReader");
        }
        return entryArray;
    }

    /**
     * Add all the given JSONObject value in a TestEntry
     * @param json the JSONObject where to find the value
     * @return TestEntry with all the JSONObject value
     * @author Giovanni Furlan (gr2) - modified by Leonardo Rossi (g2)
     */
    private static TestEntry extractValues(JSONObject json) {

        TestEntry entry = null;
        try
        {

            //Construct a new TestEntry with some JSONObject values
            entry = new TestEntry(json.getString("original_name"), json.getDouble("confidence"));

            //Add ingredients
            entry.addIngredient(json.getString("ingredients"));

            //Add notes
            entry.setNotes(json.getString("notes"));

            //Add tag
            List<String> list = new ArrayList<String>();
            //Tags are content in JSONObject as an array of String
            JSONArray jsonArray = json.getJSONArray("tags");
            if (jsonArray != null)
            {
                for (int i=0; i < jsonArray.length() ;i++)
                {
                    list.add(jsonArray.get(i).toString());
                }
            }
            entry.addTags(list);
        }
        catch(JSONException e)
        {
            Log.e(TAG, "Error in JSONExtractValue");
        }
        return entry;
    }
}
