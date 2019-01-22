package unipd.se18.ocrcamera;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static android.support.constraint.Constraints.TAG;

public class JSONReportParser {

    /**
     * Enum with TestEntry fields
     */
    enum Fields {
        NAME("original_name"),
        CONFIDENCE("confidence"),
        INGREDIENTS("ingredients"),
        TAGS("tags"),
        NOTES("notes");

        private String field;

        Fields(String field){
            this.field=field;
        }

        public String getValue() {
            return field;
        }
    }

    /**
     * Convert a string in TestEntry[] and call method JSONReader
     * The JSONObject inside the string must have linked other JSONObjects with Fields enum
     * @param fileContent a String in JSON format
     * @return TestEntry[] an array of Test Entry with all the JSONObject value inside or null if fileContent is not in JSON format
     * @author Giovanni Furlan (gr2)
     * @throws JSONException
     * @throws InvalidJSON
     */
    public static TestEntry[] parseReport(String fileContent) throws JSONException, InvalidJSON
    {
        TestEntry[] array;

        JSONObject json = new JSONObject(fileContent);
        array = retrieveEntries(json);

        return array;
    }

    /**
     * Get a JSONObject with keys linked to JSONObjects A that contains the information.
     * Put information of a JSONObject A in a TestEntry, do it for all JSONObject and return all the TestEntry in an array
     * The JSONObject B must have the fields "original_name","confidence", "ingredients", "notes" and "tags"
     * @param json JSONObject file extracted by PhotoTester class
     * @return TestEntry[] an array of Test Entry with all the JSONObject inside
     * @author Giovanni Furlan (gr2) - modified by Leonardo Rossi (g2)
     * @throws JSONException
     * @throws InvalidJSON
     */
    private static TestEntry[] retrieveEntries(JSONObject json) throws JSONException, InvalidJSON
    {

        TestEntry[] entryArray = new TestEntry[json.length()];

        //Iterator that slides all JSONObject inside the param
        Iterator<String> keys = json.keys();
        int counter = 0;
        while(keys.hasNext())
        {
            //Add all the information inside a TestEntry
            JSONObject jsonDetails = json.getJSONObject(keys.next());
            TestEntry entry = extractValues(jsonDetails);
            entryArray[counter]=entry;
            counter++;
        }

        return entryArray;
    }

    /**
     * Add all the given JSONObject value in a TestEntry
     * @param json the JSONObject where to find the value, with fields "original_name","confidence", "ingredients", "notes" and "tags"
     * @return TestEntry with all the JSONObject value
     * @author Giovanni Furlan (gr2) - modified by Leonardo Rossi (g2)
     * @throws JSONException
     * @throws InvalidJSON
     */
    private static TestEntry extractValues(JSONObject json) throws JSONException, InvalidJSON
    {

        TestEntry entry;

        //Check if all fields of TestEntry are valid otherwise throw exception
        for(Fields field: Fields.values()){
            if(json.has(field.getValue())){
                throw new InvalidJSON();
            }
        }

        //Construct a new TestEntry with some JSONObject values
        entry = new TestEntry(json.getString(Fields.NAME.getValue()),
                json.getDouble(Fields.CONFIDENCE.getValue()));

        //Add ingredients
        entry.addIngredient(json.getString(Fields.INGREDIENTS.getValue()));

        //Add notes
        entry.setNotes(json.getString(Fields.NOTES.getValue()));

        //Add tag, method addTags requires a list
        List<String> list = new ArrayList<>();
        //Tags are content in JSONObject as an array of String
        JSONArray jsonArray = json.getJSONArray(Fields.TAGS.getValue());
        if (jsonArray != null)
        {
            for (int i=0; i < jsonArray.length() ;i++)
            {
                list.add(jsonArray.get(i).toString());
            }
        }
        entry.addTags(list);

        return entry;
    }
}

/**
 * New Exception
 */
class InvalidJSON extends Exception
{
    // Parameterless Constructor
    InvalidJSON() {}

    // Constructor that accepts a message
    InvalidJSON(String message)
    {
        super(message);
    }
}
