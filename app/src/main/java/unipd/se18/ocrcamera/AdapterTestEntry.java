package unipd.se18.ocrcamera;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

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
    public TestEntry getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Layout that contains the views of the result
        RelativeLayout resultView = convertView.findViewById(R.id.result_view);

        // Set the image preview
        ImageView pic = resultView.findViewById(R.id.analyzed_pic);
        //pic.setImageBitmap(entries.get(position).);

        // Set the name of the pic
        TextView name = resultView.findViewById(R.id.pic_name);
        name.setText(entries.get(position).getPhotoName());

        //
        return resultView;
    }
}