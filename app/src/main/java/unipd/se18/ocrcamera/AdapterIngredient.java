package unipd.se18.ocrcamera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for list view of ingredients after processing with incidb
 * @Author Francesco Pham
 */
public class AdapterIngredient extends BaseAdapter {
    ArrayList<Ingredient> ingredients;

    //Context of the app
    private Context context;

    AdapterIngredient(Context context, ArrayList<Ingredient> ingredients) {
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

        // Set the found text
        TextView foundText = convertView.findViewById(R.id.found_text_view);
        foundText.setText(ingredients.get(position).getFoundText());

        // Set description
        TextView description = convertView.findViewById(R.id.description_view);
        description.setText(ingredients.get(position).getDescription());

        // Set similarity
        TextView similarityView = convertView.findViewById(R.id.similarity_view);
        double similarity = ingredients.get(position).getOcrTextSimilarity()*100;
        similarityView.setText(String.format("%.1f", similarity));

        return convertView;
    }
}
