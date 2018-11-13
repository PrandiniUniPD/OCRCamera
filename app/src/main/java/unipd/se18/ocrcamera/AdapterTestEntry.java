package unipd.se18.ocrcamera;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Adapter for the view of the processing result of the pics
 * @author Pietro Prandini
 */
public class AdapterTestEntry extends BaseAdapter
{
    private Context context;
    private TestEntry[] entries;

    /**
     * Defines an object of AdapterTestEntry type
     * @param context The reference to the activity where the adapter will be used
     * @param entries The list of the entries containing data from photos test
     */
    AdapterTestEntry(Context context, TestEntry[] entries)
    {
        this.context = context;
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return entries.length;
    }

    @Override
    public Object getItem(int position) { return entries[position]; }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.test_entry, parent, false);
        }

        // Set the correctness value
        TextView correctness = convertView.findViewById(R.id.correctness_view);
        double confidence = entries[position].getConfidence();
        String confidenceText = new DecimalFormat("#0").format(confidence) + " %";

        // Set the color of the correctness
        if(confidence < 70) {
            correctness.setTextColor(Color.RED);
        } else if (confidence < 85) {
            correctness.setTextColor(Color.YELLOW);
        } else {
            correctness.setTextColor(Color.GREEN);
        }

        correctness.setText(confidenceText);

        // Set the name of the pic
        TextView name = convertView.findViewById(R.id.pic_name_view);
        name.setText(entries[position].getPhotoName());

        // Set the Tags text
        TextView tags = convertView.findViewById(R.id.tags_view);
        StringBuilder assignedTags = new StringBuilder();
        for(String tag: entries[position].getTags()) {
            assignedTags.append(tag).append(", ");
        }
        tags.setText(assignedTags.toString());

        // Set the ingredients text
        TextView ingredients = convertView.findViewById(R.id.ingredients_view);
        StringBuilder realIngredients = new StringBuilder();
        for(String ingredient: entries[position].getIngredients()) {
            realIngredients.append(ingredient).append(", ");
        }
        ingredients.setText(realIngredients);

        // Set the notes text
        TextView notes = convertView.findViewById(R.id.notes_view);
        notes.setText(entries[position].getNotes());

        // return the view of the entry
        return convertView;
    }
}