package unipd.se18.ocrcamera;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Class that implements the adapter to show the list of BlurObject
 * Leonardo Pratesi - gruppo 1
 *  USING MODEL-VIEW-CONTROLLER
 */
public class BlurObjectAdapter extends ArrayAdapter<BlurObject> {

    private static class ViewHolder {
        private TextView itemView;

    }

    public BlurObjectAdapter(Context context, int textViewResourceId, ArrayList<BlurObject> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(android.R.layout.simple_list_item_1
                            , parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) convertView.findViewById(android.R.id.list_container);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BlurObject item = getItem(position);
        if (item!= null) {
            // My layout has only one TextView
            // do whatever you want with your string and long
            viewHolder.itemView.setText(String.format("%s %f", item.image, item.blur));
        }

        return convertView;
    }
}
