package unipd.se18.ocrcamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Prints to the screen the details of a TestElement
 * @author Pietro Prandini (g2)
 */
public class TestDetailsActivity extends AppCompatActivity {
    /**
     * String used for the log of this class
     */
    private String TAG = "TestDetailsActivity -> ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_element_details);

        // Retrieves the TestElement to be viewed (default == 0)
        Intent lastIntent = getIntent();
        int position = lastIntent.getIntExtra(TestUtils.positionString,0);
        TestElement entry = TestsListAdapter.getTestElements()[position];

        // Retrieves the limits for the correctness color
        int redUntil = lastIntent.getIntExtra(
                TestUtils.redUntilString,
                TestUtils.defaultRedUntil
        );
        int yellowUntil = lastIntent.getIntExtra(
                TestUtils.yellowUntilString,
                TestUtils.defaultYellowUntil
        );

        // Sets the correctness value
        TextView correctness = findViewById(R.id.correctness_view);
        float confidence = entry.getConfidence();
        correctness.setText(TestUtils.formatPercentString(confidence));

        // Sets the color of the correctness text value
        correctness.setTextColor(TestUtils.chooseColorOfValue(confidence,redUntil,yellowUntil));

        // Sets the name of the pic
        TextView name = findViewById(R.id.pic_name_view);
        String picName = entry.getFileName();
        name.setText(picName);

        // Sets the pic view
        ImageView analyzedPic = findViewById(R.id.pic_view);
        String imagePath = entry.getImagePath();
        Bitmap img = Utils.loadBitmapFromFile(imagePath);
        analyzedPic.setImageBitmap(TestUtils.scaleBitmap(TestDetailsActivity.this, img));

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
        TestUtils.setAlterationsView(
                TestDetailsActivity.this,
                (RelativeLayout) findViewById(R.id.result_view),
                extractedIngredientsView,
                entry,
                true
        );
    }
}
