package unipd.se18.ocrcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

public class TestElementDetails extends AppCompatActivity {
    protected static TestElement entry;

    private String TAG = "TestElementDetails -> ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_element_details);

        // Set the correctness value
        TextView correctness = findViewById(R.id.correctness_view);
        float confidence = entry.getConfidence();
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
        TextView name = findViewById(R.id.pic_name_view);
        String picName = entry.getFileName();
        name.setText(picName);

        // Set the pic view
        ImageView analyzedPic = findViewById(R.id.pic_view);
        String imagePath = entry.getImagePath();
        Bitmap img = Utils.loadBitmapFromFile(imagePath);

        // Scaling the pic view
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        WindowManager mWindowManager = (WindowManager) TestElementDetails.this
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
}
