package unipd.se18.ocrcamera;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class AdapterTestEntry extends BaseAdapter
{
    private Context context;
    private ArrayList<TestEntry> entries;

    /**
     * Defines an object of AdapterTestEntry type
     * @param context The reference to the activity where the adapter will be used
     * @param entries The list of the entries containing data from photos test
     */
    AdapterTestEntry(Context context, ArrayList<TestEntry> entries)
    {
        this.context = context;
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.test_entry, parent, false);
        }

        ClipData.Item entry = (ClipData.Item) getItem(position);

        // Set the image preview
        ImageView pic = convertView.findViewById(R.id.analyzed_pic);
        //pic.setImageBitmap(entries.get(position).getPhoto()); //TODO Implement getPhoto() method in TestEntry.java

        // Set the name of the pic
        TextView name = convertView.findViewById(R.id.pic_name);
        name.setText(entries.get(position).getPhotoName());

        // Set the correctness value
        TextView correctness = convertView.findViewById(R.id.calculated_correctness);
        String confidence = new DecimalFormat("#0.00").format(entries.get(position).getConfidence()) + " %";
        correctness.setText(confidence);

        // Set the Tags text
        TextView tags = convertView.findViewById(R.id.assigned_tags);
        StringBuilder assignedTags = new StringBuilder();
        for(String tag : entries.get(position).getTags()) {
            assignedTags.append(", ").append(tag);
        }
        tags.setText(assignedTags.toString());

        // return the view of the entry
        return convertView;
    }
}