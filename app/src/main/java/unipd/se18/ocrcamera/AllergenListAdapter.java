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

/*
 * This class is used to initiate the listViews in MainAllergensActivity
 * Also defined here are the instructions performed when a user selects a new allergen from the list
 * @author Pietro Balzan
 */
public class AllergenListAdapter extends ArrayAdapter<Allergen> {

    private static final String TAG = "AllergenListAdapter";
    private Context mContext;
    private int mResource;
    private AllergensManager mAllergensManager;
    private ArrayList<Allergen> selectedAllergens;

    /**
     * Default constructor for an AllergensListAdapter
     * @param context of the calling activity
     * @param resource int of the resource layout file of the view to adapt
     * @param objects an arraylist of objects to be adapted
     */
    AllergenListAdapter(Context context, int resource, ArrayList<Allergen> objects) {
        super(context, resource, objects);
        mContext = context;
        //in this case we will be using allergen_single.xml
        mResource = resource;
        //set an AllergenManager used to modify users' allergens list
        mAllergensManager = InciSingleton.getInstance(context).getAllergensManager();
        selectedAllergens = mAllergensManager.getSelectedAllergensList();
    }

    /**
     * this method deals with the adaptation of the single allergen view
     * to the ListView of AllergensSearchFragment
     * @param position of the Allergen
     * @param convertView the view to be adapted
     * @param parent viewGroup
     * @return convertView the adapted View
     */
    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            //Allergen information
            final Allergen mAllergen= getItem(position);
            String name = mAllergen.getCommonName();
            boolean selected = selectedAllergens.contains(mAllergen);

            //create Allergen in the layout
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            //set TextView to name of the Allergen
            Log.i(TAG, "Set allergen name to "+name);
            TextView allergenTv = convertView.findViewById(R.id.singleAllergenTv);
            allergenTv.setText(name);

            //set ToggleButton to checked or not depending on the boolean value "selected"
            ToggleButton allergenToggleButton = convertView.findViewById(R.id.toggleButton);
            Log.i(TAG, "Set button state to "+selected);
            allergenToggleButton.setChecked(selected);

            //change allergen selection state and remove from/add to user's list if the button is clicked
            allergenToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //check if it is still selected or not (user may press the button multiple times
                    boolean isSelected= selectedAllergens.contains(mAllergen);
                    //add or remove the one chosen by the user
                    if (isSelected){
                        Log.i(TAG, "remove allergen from user's list");
                        selectedAllergens.remove(mAllergen);
                    }
                    else {
                        Log.i(TAG, "add allergen to user's list");
                        selectedAllergens.add(mAllergen);
                    }
                    Log.i(TAG, "Lista degli allergeni: "+selectedAllergens.toString());
                    mAllergensManager.updateSelectedAllergens(selectedAllergens);
                }
            });
        }
        catch (NullPointerException npe){
            Log.e(TAG, "Missing Parameters");
            npe.printStackTrace();
        }
        return convertView;
    }
}
