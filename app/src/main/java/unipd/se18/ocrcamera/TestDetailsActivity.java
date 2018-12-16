package unipd.se18.ocrcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Prints to the screen the details of a TestElement
 * @author Pietro Prandini (g2)
 */
public class TestDetailsActivity extends AppCompatActivity {
    protected static TestElement entry;
    protected static float redUntil;
    protected static float yellowUntil;

    private String TAG = "TestDetailsActivity -> ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_element_details);

        // Set the correctness value
        TextView correctness = findViewById(R.id.correctness_view);
        float confidence = entry.getConfidence();
        correctness.setText(formatPercentString(confidence));

        // Set the color of the correctness text value
        correctness.setTextColor(chooseColorOfValue(confidence,redUntil,yellowUntil));

        // Set the name of the pic
        TextView name = findViewById(R.id.pic_name_view);
        String picName = entry.getFileName();
        name.setText(picName);

        // Set the pic view
        ImageView analyzedPic = findViewById(R.id.pic_view);
        String imagePath = entry.getImagePath();
        Bitmap img = Utils.loadBitmapFromFile(imagePath);
        analyzedPic.setImageBitmap(scaleBitmap(TestDetailsActivity.this, img));

        // Set the Tags text
        TextView tags = findViewById(R.id.tags_view);
        StringBuilder assignedTags = new StringBuilder();
        for(String tag: entry.getTags()) {
            assignedTags.append(tag).append(", ");
        }
        tags.setText(assignedTags.toString());

        // Set the ingredients text
        TextView ingredients = findViewById(R.id.ingredients_view);
        StringBuilder realIngredients = new StringBuilder();
        for(String ingredient: entry.getIngredientsArray()) {
            realIngredients.append(ingredient).append(", ");
        }
        ingredients.setText(realIngredients);

        // Set the extracted text
        TextView extractedText = findViewById(R.id.extractedText_view);
        extractedText.setText(entry.getRecognizedText());

        // Set the notes text
        TextView notes = findViewById(R.id.notes_view);
        notes.setText(entry.getNotes());

        // Set alterations view
        setAlterationsView(
                TestDetailsActivity.this,
                (RelativeLayout) findViewById(R.id.result_view),
                R.id.notes_view,
                entry);
    }

    /**
     * Formats the percent String
     * @param value The percent value
     * @return The String formatted
     * @author Pietro Prandini (g2)
     */
    protected static String formatPercentString(float value) {
        return new DecimalFormat("#0").format(value) + " %";
    }

    /**
     * Return the appropriate color of a value.
     * The color is picket between red (not good), yellow (ok) and green (very good).
     * @param value The value to be colored
     * @param redUntil Under this value would be red and upper this yellow.
     * @param yellowUntil Under this value would be yellow and upper this green.
     * @return The relative color.
     * @author Pietro Prandini (g2)
     */
    protected static int chooseColorOfValue(float value, float redUntil, float yellowUntil) {
        if(value < redUntil) {
            return Color.RED;
        } else if (value < yellowUntil) {
            return Color.YELLOW;
        } else {
            return Color.GREEN;
        }
    }

    /**
     * Return the appropriate color of a value.
     * The color is picket between red (not better than redUntil) and green (better than redUntil).
     * @param value The value to be colored
     * @param redUntil Under this value would be red and upper this yellow.
     * @return The relative color.
     * @author Pietro Prandini (g2)
     */
    protected static int chooseColorOfValue(float value, float redUntil) {
        if(value < redUntil) {
            return Color.RED;
        } else {
            return Color.GREEN;
        }
    }

    /**
     * Scales a Bitmap pic relatively to the width of the screen
     * @param context The context of the activity
     * @param img The Bitmap to be scaled
     * @return Bitmap scaled with the width same as the width of the screen
     * @author Pietro Prandini (g2)
     */
    protected static Bitmap scaleBitmap(Context context, Bitmap img) {
        // Obtains the original dimensions of the pic
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();

        // Obtains the metrics of the screen (useful to obtain the with of the screen)
        WindowManager mWindowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        Display mDisplay = mWindowManager.getDefaultDisplay();
        mDisplay.getMetrics(mDisplayMetrics);

        // Calculates the new dimensions of the pic
        int scaledWidth = mDisplayMetrics.widthPixels;
        int scaledHeight = (scaledWidth*imgHeight)/imgWidth;

        // Returns the Bitmap scaled
        return Bitmap.createScaledBitmap(img, scaledWidth, scaledHeight,false);
    }

    /**
     * Set the alterations text to the view of the activity
     * @param context The context where would be the alterations text
     * @param relativeLayout The layout to add the text views
     * @param idBelowOf The id of the view where putting the alterations text below of
     * @param entry The test element where searching the alterations
     * @author Pietro Prandini (g2)
     */
    protected static void setAlterationsView(Context context, RelativeLayout relativeLayout,
                                             int idBelowOf, TestElement entry) {
        String[] alterations = entry.getAlterationsNames();
        StringBuilder alterationsText = new StringBuilder();
        if(alterations != null) {

            // Sets details of alterations
            for (String alteration : alterations) {
                // Prepares the alterations String
                float confidenceOfAlteration = entry.getAlterationConfidence(alteration);
                alterationsText.append(alteration)
                        .append(" - confidence ")
                        .append(TestDetailsActivity.formatPercentString(confidenceOfAlteration))
                        .append("\n");

                // Prepares the alterations TextView
                TextView alterationsView = new TextView(context);
                alterationsView.setText(alterationsText.toString());
                alterationsView.setTextColor(
                        chooseColorOfValue(confidenceOfAlteration, entry.getConfidence()));
                // Sets shadow (supports the coloured view)
                float radius = 1;
                float dx = 0;
                float dy = 0;
                alterationsView.setShadowLayer(radius,dx,dy,Color.BLACK);

                // Sets bold (supports the coloured view)
                alterationsView.setTypeface(null, Typeface.BOLD);

                // Padding
                int padding = 10;
                alterationsView.setPadding(padding, padding, padding, padding);

                // Sets layout params
                RelativeLayout.LayoutParams paramsView = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsView.addRule(RelativeLayout.BELOW, idBelowOf);
                paramsView.addRule(RelativeLayout.CENTER_HORIZONTAL);

                relativeLayout.addView(alterationsView, paramsView);

                // Sets an appropriate id
                alterationsView.setId(View.generateViewId());
                idBelowOf = alterationsView.getId();
            }
        }
    }
}
