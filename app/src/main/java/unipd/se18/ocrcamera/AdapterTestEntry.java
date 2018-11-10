package unipd.se18.ocrcamera;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return null;
    }
}
