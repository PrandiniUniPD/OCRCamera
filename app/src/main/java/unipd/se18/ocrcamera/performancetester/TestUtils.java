package unipd.se18.ocrcamera.performancetester;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import unipd.se18.ocrcamera.R;
import unipd.se18.ocrcamera.Utils;

/**
 * Class useful for the testing area
 * @author Pietro Prandini (g2)
 */
class TestUtils {
    /*
    Strings used for passing by intent some data to others activity
     */
    static final String positionString = "position";
    static final String redUntilString = "redUntil";
    static final String yellowUntilString = "yellowUntil";

    /*
    Default values for choosing the color of the correctness test
     */
    static final int defaultRedUntil = 70;
    static final int defaultYellowUntil = 85;

    /**
     * Formats the percent String
     * More details at: {@link DecimalFormat}
     * @param value The percent value
     * @return The String formatted
     * @author Pietro Prandini (g2)
     */
    static String formatPercentString(float value) {
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
    static int chooseColorOfValue(float value, float redUntil, float yellowUntil) {
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
    static int chooseColorOfValue(float value, float redUntil) {
        if(value < redUntil) {
            return Color.RED;
        } else {
            return Color.GREEN;
        }
    }

    /**
     * Scales a Bitmap pic relatively to the width of the screen
     * More details at:
     * {@link Context#getSystemService(String)}
     * {@link Context#WINDOW_SERVICE}
     * {@link WindowManager}
     * {@link WindowManager#getDefaultDisplay()}
     * {@link DisplayMetrics}
     * {@link Display#getMetrics(DisplayMetrics)}
     * {@link Bitmap#createScaledBitmap(Bitmap, int, int, boolean)}
     * @param context The context of the activity
     * @param img The Bitmap to be scaled
     * @return Bitmap scaled with the width same as the width of the screen
     * @author Pietro Prandini (g2)
     */
    static Bitmap scaleBitmap(Context context, Bitmap img) {
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
     * @param belowOf The view where putting the alterations text below of
     * @param element The test element where searching the alterations
     * @param viewDetails True for viewing details of alterations, false otherwise
     * @author Pietro Prandini (g2)
     */
    static void setAlterationsView(Context context, RelativeLayout relativeLayout,
                                   View belowOf, TestElement element, Boolean viewDetails) {
        // Gets the alterations name (if any)
        String[] alterations = element.getAlterationsNames();

        // If there aren't alteration there are nothing to view
        if(alterations == null) { return; }

        /*
        Now will be prepared the views that represent the details of the alteration.
        Not all the test pics have alterations so each views will be added programmatically only
        if needed.
         */

        StringBuilder alterationsText = new StringBuilder();
        for (String alteration : alterations) {
            // Prepares the alterations String with the relative confidence (Title)
            float confidenceOfAlteration = element.getAlterationConfidence(alteration);
            alterationsText
                    .append(alteration)
                    .append(" - ")
                    .append(context.getString(R.string.confidence))
                    .append(" ")
                    .append(TestUtils.formatPercentString(confidenceOfAlteration))
                    .append("\n");

            // Prepares the alterations TextView programmatically
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
            belowOf = addViewBelow(relativeLayout,paramsView,belowOf,alterationsView);

            // Sets other details if required
            if(viewDetails) {
                belowOf = viewAlterationDetails(
                        context,
                        relativeLayout,
                        belowOf,
                        element,
                        alteration
                );
            }
        }
    }

    /**
     * Sets the details of an altered test programmatically
     * @param context The context where would be the alterations text
     * @param relativeLayout The layout to add the text views
     * @param belowOf The view where putting the alterations text below of
     * @param element The test element where searching the alterations
     * @param alteration The alteration type String
     * @return the last View added
     * @author Pietro Prandini (g2)
     */
    private static View viewAlterationDetails(Context context, RelativeLayout relativeLayout,
                                              View belowOf, TestElement element, String alteration) {
        // Obtains the altered pic
        String imagePath = element.getAlterationImagePath(alteration);
        Bitmap img = scaleBitmap(context, Utils.loadBitmapFromFile(imagePath));
        ImageView picView = new ImageView(context);
        picView.setImageBitmap(img);

        // Adds the pics view (set the id to the id of that view)
        belowOf = addViewBelow(relativeLayout, belowOf, picView);

        // Adds the alteration details
        // Tags title
        TextView tagsTitle = new TextView(context);
        tagsTitle.setText(R.string.tags);

        int titlePadding = 5;
        tagsTitle.setPadding(titlePadding,titlePadding,titlePadding,titlePadding);
        tagsTitle.setTypeface(Typeface.DEFAULT_BOLD);

        belowOf = addViewBelow(relativeLayout,belowOf,tagsTitle);

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

        belowOf = addViewBelow(relativeLayout,belowOf,tags);

        // Extracted text title
        TextView extractedTextTitle = new TextView(context);
        extractedTextTitle.setText(R.string.extrected_text);

        extractedTextTitle.setPadding(titlePadding,titlePadding,titlePadding,titlePadding);
        extractedTextTitle.setTypeface(Typeface.DEFAULT_BOLD);

        belowOf = addViewBelow(relativeLayout,belowOf,extractedTextTitle);

        // Extracted text details
        TextView extractedText = new TextView(context);
        extractedText.setText(element.getAlterationRecognizedText(alteration));
        extractedText.setPadding(detailsPadding, detailsPadding, detailsPadding, detailsPadding);

        belowOf = addViewBelow(relativeLayout,belowOf,extractedText);

        // Notes title
        TextView notesTitle = new TextView(context);
        notesTitle.setText(R.string.notes);

        notesTitle.setPadding(titlePadding,titlePadding,titlePadding,titlePadding);
        notesTitle.setTypeface(Typeface.DEFAULT_BOLD);

        belowOf = addViewBelow(relativeLayout,belowOf,notesTitle);

        // Notes details
        TextView notes = new TextView(context);
        notes.setText(element.getAlterationNotes(alteration));
        notes.setPadding(detailsPadding, detailsPadding, detailsPadding, detailsPadding);

        belowOf = addViewBelow(relativeLayout,belowOf,notes);

        return belowOf;
    }

    /**
     * Adds a view below with parameters set to "WRAP_CONTENT"
     * @param relativeLayout The layout to add the text views
     * @param belowOf The view where putting the alterations text below of
     * @param view The view to be added
     * @return The view added
     * @author Pietro Prandini (g2)
     */
    private static View addViewBelow(RelativeLayout relativeLayout,
                                      View belowOf, View view) {
        // Configures the layout parameters
        RelativeLayout.LayoutParams paramsView = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        return addViewBelow(relativeLayout, paramsView, belowOf,view);
    }

    /**
     * Adds a view below
     * @param relativeLayout The layout to add the text views
     * @param layoutParams The parameters of the layout
     * @param belowOf The view where putting the alterations text below of
     * @param view The view to be added
     * @return The view added
     * @author Pietro Prandini (g2)
     */
    private static View addViewBelow(RelativeLayout relativeLayout,
                                      RelativeLayout.LayoutParams layoutParams,
                                      View belowOf, View view) {
        // Puts below of the id passed
        layoutParams.addRule(RelativeLayout.BELOW, belowOf.getId());

        // Adds the view
        relativeLayout.addView(view,layoutParams);

        // Sets the appropriate id
        view.setId(View.generateViewId());
        return view;
    }

    /**
     * From a TestElement it returns the appropriate id
     * @param element The TestElement to be analyzed
     * @return The id of the TestElement passed
     * @author Pietro Prandini (g2)
     */
    static long getTestElementId(TestElement element) {
        // The prefix is "foto", so the suffix starts at 4
        int suffix = 4;
        return Integer.parseInt(element.getFileName().substring(suffix));
    }
}
