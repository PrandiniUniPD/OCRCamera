package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

    private AllergensManager allergensManager;


    AdapterIngredient(Context context, List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        this.context = context;
        this.allergensManager = InciSingleton.getInstance(context).getAllergensManager();
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

        Ingredient ingredient = ingredients.get(position);
        final String inciName = ingredient.getInciName();
        final String description = ingredient.getDescription();
        final String function = ingredient.getFunction();

        // Set the inci name
        TextView nameText = convertView.findViewById(R.id.inci_name_view);
        nameText.setText(inciName);

        // Set description
        TextView descriptionView = convertView.findViewById(R.id.description_view);
        descriptionView.setText(description);

        // Set function
        TextView functionView = convertView.findViewById(R.id.function_view);
        functionView.setText(function);

        // Highlight if it is an allergen
        if(allergensManager.checkForSelectedAllergens(ingredient).size() > 0)
            convertView.setBackgroundColor(Color.RED);
        else if(allergensManager.checkForAllergens(ingredient).size() > 0)
            convertView.setBackgroundColor(Color.YELLOW);

        return convertView;
    }


}
