package unipd.se18.ocrcamera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import unipd.se18.ingredientsextractor.Ingredient;

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

        final String inciName = ingredients.get(position).getInciName();
        final String description = ingredients.get(position).getDescription();
        final String function = ingredients.get(position).getFunction();

        // Set the inci name
        TextView nameText = convertView.findViewById(R.id.inci_name_view);
        nameText.setText(inciName);

        // Set description
        TextView descriptionView = convertView.findViewById(R.id.description_view);
        descriptionView.setText(description);

        // Set function
        TextView functionView = convertView.findViewById(R.id.function_view);
        functionView.setText(function);

        return convertView;
    }


}
