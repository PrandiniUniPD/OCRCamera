package unipd.se18.ocrcamera;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;


public class AllergenListAdapter extends ArrayAdapter<Allergen> {

    private static final String TAG = "AllergenListAdapter";
    private Context mContext;
    private int mResource;

    /**
     * Default constructor for an AllergensListAdapter
     * @param context of the activity
     * @param resource int of the resource of the view to adapt
     * @param objects an arraylist of objects to be adapted
     */
    public AllergenListAdapter(Context context, int resource, ArrayList<Allergen> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    /**
     * this method deals with the adaptation of the single allergen view
     * to the ListView of AllergensActivity
     * @param position
     * @param convertView the view to be adapted
     * @param parent
     * @return convertView the adapted View
     */
    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //Allergen information
        final String name;
        final Boolean selected;
        try {
            name = getItem(position).getCommonName();
            selected = getItem(position).isSelected();


            //create Allergen object with the information
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            //set TextView to name of the Allergen
            Log.i(TAG, "Set allergen name");
            TextView allergenTv = convertView.findViewById(R.id.singleAllergenTv);
            allergenTv.setText(name);

            //set ToggleButton to toggled or not depending on the boolean value "selected"
            ToggleButton allergenToggleButton = convertView.findViewById(R.id.toggleButton);
            Log.i(TAG, "Set button state");
            allergenToggleButton.setChecked(selected);

            //change allergen selected state and remove from/add to user's list if the button is clicked
            allergenToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //change the value of "selected" for the Allergen
                    Log.i(TAG, "change allergen selected state and remove from/add to user's list");
                    getItem(position).setSelection(!selected);
                }
            });
        }
        catch (NullPointerException npe){
            Log.e(TAG, "Missing Parameters");
        }
        return convertView;
    }
}
