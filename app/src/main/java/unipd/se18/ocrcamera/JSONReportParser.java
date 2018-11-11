package unipd.se18.ocrcamera;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class JSONReportParser {

    //TODO no need a class for those two methods

    /**
     * Get a JSONObject where the key are linked with JSONObject with the photo information.
     * Pass the information to a TestEntry and return all the TestEntry in an array
     * @param json JSONObject file extracted by PhotoTester class
     * @return TestEntry[]
     */
    public TestEntry[] JSONReader(JSONObject json) {

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
     * Add in TestEntry all the given JSONObject value
     * @param json the JSONObject where to find the value
     * @modify TestEntry add all json value
     * @return TestEntry with all the JSONObject value
     */
    public TestEntry JSONExtractValue(JSONObject json) {

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
