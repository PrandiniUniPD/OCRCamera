package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.Intent;
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
    /**
     * String used for the log of this class
     */
    private String TAG = "TestDetailsActivity -> ";

    /**
     * TestElement to be viewed
     */
    private TestElement entry;

    /*
    Limits for choosing the color of the correctness relatively to the goodness of the extraction
     */
    private float redUntil;
    private float yellowUntil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_element_details);

        // Retrieves the TestElement to be viewed (default == 0)
        Intent lastIntent = getIntent();
        int position = lastIntent.getIntExtra(TestsListAdapter.positionString,0);
        entry = TestsListAdapter.getTestElements()[position];

        // Retrieves the limits for the correctness color (default == 0)
        redUntil = lastIntent.getIntExtra(TestsListAdapter.redUntilString, 0);
        yellowUntil = lastIntent.getIntExtra(TestsListAdapter.yellowUntilString, 0);

        // Sets the correctness value
        TextView correctness = findViewById(R.id.correctness_view);
        float confidence = entry.getConfidence();
        correctness.setText(formatPercentString(confidence));

        // Sets the color of the correctness text value
        correctness.setTextColor(chooseColorOfValue(confidence,redUntil,yellowUntil));

        // Sets the name of the pic
        TextView name = findViewById(R.id.pic_name_view);
        String picName = entry.getFileName();
        name.setText(picName);

        // Sets the pic view
        ImageView analyzedPic = findViewById(R.id.pic_view);
        String imagePath = entry.getImagePath();
        Bitmap img = Utils.loadBitmapFromFile(imagePath);
        analyzedPic.setImageBitmap(scaleBitmap(TestDetailsActivity.this, img));

        // Sets the Tags text
        TextView tags = findViewById(R.id.tags_view);
        StringBuilder assignedTags = new StringBuilder();
        for(String tag: entry.getTags()) {
            assignedTags.append(tag).append(", ");
        }
        tags.setText(assignedTags.toString());

        // Sets the ingredients text
        TextView ingredients = findViewById(R.id.ingredients_view);
        StringBuilder realIngredients = new StringBuilder();
        for(String ingredient: entry.getIngredientsArray()) {
            realIngredients.append(ingredient).append(", ");
        }
        ingredients.setText(realIngredients);

        // Sets the extracted text
        TextView extractedText = findViewById(R.id.extractedText_view);
        extractedText.setText(entry.getRecognizedText());

        // Sets the notes text
        TextView notes = findViewById(R.id.notes_view);
        notes.setText(entry.getNotes());

        // Sets the ingredients extraction report
        TextView extractedIngredientsView = findViewById(R.id.extracted_ingredients_view);
        extractedIngredientsView.setText(entry.getIngredientsExtraction());

        // Sets the alterations view
        setAlterationsView(
                TestDetailsActivity.this,
                (RelativeLayout) findViewById(R.id.result_view),
                R.id.extracted_ingredients_view,
                entry,
                true);
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
     * @param element The test element where searching the alterations
     * @param viewDetails True for viewing details of alterations, false otherwise
     * @author Pietro Prandini (g2)
     */
    protected static void setAlterationsView(Context context, RelativeLayout relativeLayout,
                                             int idBelowOf, TestElement element, Boolean viewDetails) {
        String[] alterations = element.getAlterationsNames();
        StringBuilder alterationsText = new StringBuilder();
        if(alterations != null) {
            // Sets details of alterations
            for (String alteration : alterations) {
                // Prepares the alterations String
                float confidenceOfAlteration = element.getAlterationConfidence(alteration);
                alterationsText.append(alteration)
                        .append(" - confidence ")
                        .append(TestDetailsActivity.formatPercentString(confidenceOfAlteration))
                        .append("\n");

                // Prepares the alterations TextView
                TextView alterationsView = new TextView(context);
                alterationsView.setText(alterationsText.toString());
                alterationsView.setTextColor(
                        chooseColorOfValue(confidenceOfAlteration, element.getConfidence()));
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

                // View horizontally centered
                paramsView.addRule(RelativeLayout.CENTER_HORIZONTAL);

                // Adds the view
                idBelowOf = addViewBelow(relativeLayout,paramsView,idBelowOf,alterationsView);

                // Sets details if required
                if(viewDetails) {
                    idBelowOf = viewAlterationDetails(
                            context,
                            relativeLayout,
                            idBelowOf,
                            element,
                            alteration
                    );
                }
            }
        }
    }

    /**
     * Sets the details of an altered test
     * @param context The context where would be the alterations text
     * @param relativeLayout The layout to add the text views
     * @param idBelowOf The id of the view where putting the alterations text below of
     * @param element The test element where searching the alterations
     * @param alteration The alteration type String
     * @author Pietro Prandini (g2)
     */
    protected static int viewAlterationDetails(Context context, RelativeLayout relativeLayout,
                                        int idBelowOf, TestElement element, String alteration) {

        // Obtains the altered pic
        String imagePath = element.getAlterationImagePath(alteration);
        Bitmap img = scaleBitmap(context,Utils.loadBitmapFromFile(imagePath));
        ImageView picView = new ImageView(context);
        picView.setImageBitmap(img);

        // Adds the pics view (set the id to the id of that view)
        idBelowOf = addViewBelow(relativeLayout, idBelowOf, picView);

        // Adds the alteration details
        // Tags title
        TextView tagsTitle = new TextView(context);
        tagsTitle.setText(R.string.tags);

        int titlePadding = 5;
        tagsTitle.setPadding(titlePadding,titlePadding,titlePadding,titlePadding);
        tagsTitle.setTypeface(Typeface.DEFAULT_BOLD);

        idBelowOf = addViewBelow(relativeLayout,idBelowOf,tagsTitle);

        // Tags details
        TextView tags = new TextView(context);
        int detailsPadding = 10;
        // Sets the Tags text
        StringBuilder assignedTags = new StringBuilder();
        for(String tag: element.getAlterationTags(alteration)) {
            assignedTags.append(tag).append(", ");
        }

        tags.setText(assignedTags.toString());
        tags.setPadding(detailsPadding, detailsPadding, detailsPadding,detailsPadding);

        idBelowOf = addViewBelow(relativeLayout,idBelowOf,tags);

        // Extracted text title
        TextView extractedTextTitle = new TextView(context);
        extractedTextTitle.setText(R.string.extrected_text);

        extractedTextTitle.setPadding(titlePadding,titlePadding,titlePadding,titlePadding);
        extractedTextTitle.setTypeface(Typeface.DEFAULT_BOLD);

        idBelowOf = addViewBelow(relativeLayout,idBelowOf,extractedTextTitle);

        // Extracted text details
        TextView extractedText = new TextView(context);
        extractedText.setText(element.getAlterationRecognizedText(alteration));
        extractedText.setPadding(detailsPadding, detailsPadding, detailsPadding, detailsPadding);
        
        idBelowOf = addViewBelow(relativeLayout,idBelowOf,extractedText);

        // Notes title
        TextView notesTitle = new TextView(context);
        notesTitle.setText(R.string.notes);

        notesTitle.setPadding(titlePadding,titlePadding,titlePadding,titlePadding);
        notesTitle.setTypeface(Typeface.DEFAULT_BOLD);

        idBelowOf = addViewBelow(relativeLayout,idBelowOf,notesTitle);

        // Notes details
        TextView notes = new TextView(context);
        notes.setText(element.getAlterationNotes(alteration));
        notes.setPadding(detailsPadding, detailsPadding, detailsPadding, detailsPadding);

        idBelowOf = addViewBelow(relativeLayout,idBelowOf,notes);

        return idBelowOf;
    }

    /**
     * Adds a view below with parameters set to "WRAP_CONTENT"
     * @param relativeLayout The layout to add the text views
     * @param idBelowOf The id of the view where putting the alterations text below of
     * @param view The view to be added
     * @return The id of the view added
     * @author Pietro Prandini (g2)
     */
    protected static int addViewBelow(RelativeLayout relativeLayout,
                                      int idBelowOf, View view) {
        // Configures the layout parameters
        RelativeLayout.LayoutParams paramsView = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);

        return addViewBelow(relativeLayout, paramsView, idBelowOf,view);
    }

    /**
     * Adds a view below
     * @param relativeLayout The layout to add the text views
     * @param layoutParams The parameters of the layout
     * @param idBelowOf The id of the view where putting the alterations text below of
     * @param view The view to be added
     * @return The id of the view added
     * @author Pietro Prandini (g2)
     */
    protected static int addViewBelow(RelativeLayout relativeLayout,
                                      RelativeLayout.LayoutParams layoutParams,
                                      int idBelowOf, View view) {
        // Puts below of the id passed
        layoutParams.addRule(RelativeLayout.BELOW, idBelowOf);

        // Adds the view
        relativeLayout.addView(view,layoutParams);

        // Sets the appropriate id
        view.setId(View.generateViewId());
        idBelowOf = view.getId();
        return idBelowOf;
    }

    /**
     * From a TestElement it returns the appropriate id
     * @param element The TestElement to be analyzed
     * @return The id of the TestElement passed
     * @author Pietro Prandini (g2)
     */
    protected static long getTestElementId(TestElement element) {
        // The prefix is "foto", so the suffix starts at 4
        int suffix = 4;
        return Integer.parseInt(element.getFileName().substring(suffix));
    }
}
