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

public class AdapterTestAlterations extends BaseAdapter {

    /**
     * Context of the app
     */
    private Context context;

    /**
     * Elements of test
     */
    private TestElement entry;

    /**
     * String used for the logs of this class
     */
    private final String TAG = "AdapterTestElement";

    /**
     * Defines an object of AdapterTestAlterations type
     * @param context The reference to the activity where the adapter will be used
     * @param entry The list of the test elements containing data from photos test
     */
    AdapterTestAlterations(Context context, TestElement entry)
    {
        this.context = context;
        this.entry = entry;
    }


    @Override
    public int getCount() {
        return entry.getAlterationsNames().length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {if (convertView == null) {
        convertView = LayoutInflater.from(context).inflate(R.layout.test_alteration_element, parent, false);
    }
        Log.v(TAG, "position == " + position + ", name == " + entry.getAlterationsNames()[position]);
        // Set the correctness value
        TextView correctness = convertView.findViewById(R.id.correctness_view);
        float confidence = entry.getAlterationConfidence(entry.getAlterationsNames()[position]);
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
        String picName = entry.getAlterationsNames()[position];
        name.setText(picName);

        // Set the pic view
        ImageView analyzedPic = convertView.findViewById(R.id.pic_view);
        Bitmap img = entry.getAlterationBitmap(entry.getAlterationsNames()[position]);

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

        for(String tag: entry.getAlterationTags(entry.getAlterationsNames()[position])) {
            assignedTags.append(tag).append(", ");
        }
        tags.setText(assignedTags.toString());

        // Set the ingredients text
        TextView ingredients = convertView.findViewById(R.id.ingredients_view);
        StringBuilder realIngredients = new StringBuilder();
        for(String ingredient: entry.getIngredientsArray()) {
            realIngredients.append(ingredient).append(", ");
        }
        ingredients.setText(realIngredients);

        // Set the extracted text
        TextView extractedText = convertView.findViewById(R.id.extractedText_view);
        extractedText.setText(entry.getAlterationRecognizedText(entry.getAlterationsNames()[position]));

        // Set the notes text
        TextView notes = convertView.findViewById(R.id.notes_view);
        notes.setText(entry.getAlterationNotes(entry.getAlterationsNames()[position]));



        // return the view of the entry
        return convertView;

    }
}
