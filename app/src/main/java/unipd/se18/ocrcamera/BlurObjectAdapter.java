package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Class that implements the adapter to show the list of BlurObject
 * Leonardo Pratesi - gruppo 1
 *  USING MODEL-VIEW-CONTROLLER
 */
public class BlurObjectAdapter extends ArrayAdapter<BlurObject> {

    private static class ViewHolder {                       //for better performance I use a ViewHolder
        private TextView itemView;
        private ImageView imageView;
        public int position;                                //counter to save the position of the adapter to keep correct onClick listeners

    }

    public BlurObjectAdapter(Context context, int textViewResourceId, ArrayList<BlurObject> items) {
        super(context, textViewResourceId, items);
    }

    //create the view for each BlurObject
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            Log.e("erre", "ViewholderNONesiste");

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.adapterview
                            , parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) convertView.findViewById(R.id.textView);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);


        } else {
            Log.e("err", "Viewholdersiste");
            viewHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setTag(viewHolder);
        BlurObject item = getItem(position);

        /////////Click Listener
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if listener works
                Toast.makeText(getContext(),(String) "bene",
                        Toast.LENGTH_SHORT).show();

            }
        });


        if (item!= null) {
            Log.e("err", item.toString());
            viewHolder.itemView.setText(item.toString());
            viewHolder.imageView.setImageBitmap(item.getImage());
        }
        viewHolder.position=position;
        return convertView;
    }
}
