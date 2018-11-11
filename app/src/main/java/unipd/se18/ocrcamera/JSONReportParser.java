package unipd.se18.ocrcamera;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import static android.support.constraint.Constraints.TAG;

public class JSONReportParser {

    //TODO no need a class for those two methods

    /**
     * Get a JSONObject with keys linked to JSONObjects that contains the photo information.
     * Pass the information of a single JSONObject to a TestEntry and return all the TestEntry in an array
     * @param json JSONObject file extracted by PhotoTester class
     * @return TestEntry[] an array of TestEntry
     * @author Giovanni Furlan (gr2)
     */
    public TestEntry[] JSONReportParser(JSONObject json) {

        int lenght = json.length();
        TestEntry[] entryArray = new TestEntry[lenght-1];
        try {
            for(int i=1; i <= lenght; i++ )
            {
                //TODO check if filename is right
                String filename = "filename" + i;
                JSONObject jsonDetails = json.getJSONObject(filename);
                TestEntry entry = JSONExtractValue(jsonDetails);
                entryArray[i-1]=entry;

            }
        } catch(org.json.JSONException e) {
            Log.e(TAG, "Error in JSONReader");
        }
        return entryArray;
    }

    /**
     * Add all the given JSONObject value in avTestEntry
     * @param json the JSONObject where to find the value
     * @return TestEntry with all the JSONObject value
     * @author Giovanni Furlan (gr2)
     */
    private TestEntry JSONExtractValue(JSONObject json) {

        TestEntry entry = null;
        try {
            //Construct a new TestEntry with the JSONObject value
            entry = new TestEntry(json.getString("original_name"),
                    json.getDouble("confidence"), null, null,
                    json.getString("notes"));

            //Add ingredients
            entry.addIngredient(json.getString("ingredients"));

            //Add tag
            List<String> list = new ArrayList<String>();;
            JSONArray jsonArray = json.getJSONArray("tags");
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i=0;i<len;i++){
                    list.add(jsonArray.get(i).toString());
                }
            }
            entry.addTags(list);

        }  catch(org.json.JSONException e) {
            Log.e(TAG, "Error in JSONExtractValue");
        }
        return entry;
    }

}
