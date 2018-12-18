package unipd.se18.ocrcamera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import unipd.se18.ocrcamera.inci.Ingredient;

/**
 * Adapter for list view of ingredients after processing with incidb
 * @author Francesco Pham
 */
public class AdapterIngredient extends BaseAdapter {

    //Context of the app
    private Context context;

    private List<Ingredient> ingredients;


    AdapterIngredient(Context context, List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        this.context = context;
    }

    @Override
    public int getCount() {
        return ingredients.size();
    }

    @Override
    public Object getItem(int position) {
        return ingredients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ingredient_element, parent, false);
        }

        // Set the inci name
        TextView nameText = convertView.findViewById(R.id.inci_name_view);
        nameText.setText(ingredients.get(position).getInciName());

        // Set description
        TextView description = convertView.findViewById(R.id.description_view);
        description.setText(ingredients.get(position).getDescription());

        // Set function
        TextView function = convertView.findViewById(R.id.function_view);
        function.setText(ingredients.get(position).getFunction());

        return convertView;
    }
}
