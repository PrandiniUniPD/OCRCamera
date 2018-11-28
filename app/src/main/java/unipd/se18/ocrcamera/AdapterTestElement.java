package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.text.DecimalFormat;

/**
 * Adapter for the view of the processing result of the pics
 * @author Pietro Prandini
 */
public class AdapterTestElement extends BaseAdapter
{
    /**
     * Context of the app
     */
    private Context context;

    /**
     * Elements of test
     */
    private TestElement[] entries;

    /**
     * String used for the logs of this class
     */
    private final String TAG = "AdapterTestElement";

    /**
     * Defines an object of AdapterTestElement type
     * @param context The reference to the activity where the adapter will be used
     * @param entries The list of the test elements containing data from photos test
     */
    AdapterTestElement(Context context, TestElement[] entries)
    {
        this.context = context;
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return entries.length;
    }

    @Override
    public Object getItem(int position) { return entries[position]; }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.test_element, parent, false);
        }

        // Set the correctness value

        Button viewAlterationsButton = convertView.findViewById(R.id.view_alterations_button);
        viewAlterationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,TestAlterationsActivity.class);
                TestAlterationsActivity.entry = entries[position];
                context.startActivity(i);
            }
        });
        viewAlterationsButton.setEnabled(false);
        TextView correctness = convertView.findViewById(R.id.correctness_view);
        float confidence = entries[position].getConfidence();
        String confidenceText = new DecimalFormat("#0").format(confidence) + " %";

        // Set the color of the correctness
        if(confidence < 70) {
            correctness.setTextColor(Color.RED);
        } else if (confidence < 85) {
            correctness.setTextColor(Color.YELLOW);
        } else {
            correctness.setTextColor(Color.GREEN);
        }

        correctness.setText(confidenceText);

        // Set the name of the pic
        TextView name = convertView.findViewById(R.id.pic_name_view);
        String picName = entries[position].getFileName();
        name.setText(picName);

        // Set the pic view
        ImageView analyzedPic = convertView.findViewById(R.id.pic_view);
        Bitmap img = entries[position].getPicture();

        // Scaling the pic view
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        WindowManager mWindowManager = (WindowManager) convertView.getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        Display mDisplay = mWindowManager.getDefaultDisplay();
        mDisplay.getMetrics(mDisplayMetrics);
        int scaledWidth = mDisplayMetrics.widthPixels;
        int scaledHeight = (scaledWidth*imgHeight)/imgWidth;

        Log.v(TAG,"pic \"" + picName + "\" scaled from " + imgWidth + "x" + imgHeight +
                " to " + scaledWidth + "x" + scaledHeight);
        analyzedPic.setImageBitmap(Bitmap.createScaledBitmap(img, scaledWidth, scaledHeight,false));

        // Set the Tags text
        TextView tags = convertView.findViewById(R.id.tags_view);
        StringBuilder assignedTags = new StringBuilder();
        for(String tag: entries[position].getTags()) {
            assignedTags.append(tag).append(", ");
        }
        tags.setText(assignedTags.toString());

        // Set the ingredients text
        TextView ingredients = convertView.findViewById(R.id.ingredients_view);
        StringBuilder realIngredients = new StringBuilder();
        for(String ingredient: entries[position].getIngredientsArray()) {
            realIngredients.append(ingredient).append(", ");
        }
        ingredients.setText(realIngredients);

        // Set the extracted text
        TextView extractedText = convertView.findViewById(R.id.extractedText_view);
        extractedText.setText(entries[position].getRecognizedText());

        // Set the notes text
        TextView notes = convertView.findViewById(R.id.notes_view);
        notes.setText(entries[position].getNotes());

        // Set alterations view
        String[] alterations = entries[position].getAlterationsNames();
        StringBuilder alterationsText = new StringBuilder();

        if(alterations != null) {
            for(String alteration: alterations) {
                alterationsText.append(alteration).append(" - confidence ")
                        .append(entries[position].getAlterationConfidence(alteration)).append("\n");
            }

            viewAlterationsButton.setEnabled(true);
        }



        // return the view of the entry
        return convertView;

    }
}