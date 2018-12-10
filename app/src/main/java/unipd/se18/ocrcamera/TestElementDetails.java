package unipd.se18.ocrcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Prints to the screen the details of a TestElement
 * @author Pietro Prandini (g2)
 */
public class TestElementDetails extends AppCompatActivity {
    protected static TestElement entry;
    protected static float redUntil;
    protected static float yellowUntil;

    private String TAG = "TestElementDetails -> ";

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
        analyzedPic.setImageBitmap(scaleBitmap(TestElementDetails.this, img));

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
        String[] alterations = entry.getAlterationsNames();
        StringBuilder alterationsText = new StringBuilder();
        TextView alterationsTitle = findViewById(R.id.alterations_title);
        alterationsTitle.setVisibility(View.INVISIBLE);
        TextView alterationsView = findViewById(R.id.alterations_view);

        if(alterations != null) {
            alterationsTitle.setVisibility(View.VISIBLE);
            for(String alteration: alterations) {
                alterationsText.append(alteration).append(" - confidence ")
                        .append(entry.getAlterationConfidence(alteration)).append("\n");
            }
            alterationsView.setText(alterationsText.toString());
        }
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
}
