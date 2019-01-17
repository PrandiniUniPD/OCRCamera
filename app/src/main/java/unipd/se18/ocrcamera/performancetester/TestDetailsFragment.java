package unipd.se18.ocrcamera.performancetester;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import unipd.se18.ocrcamera.R;
import unipd.se18.ocrcamera.Utils;

/**
 * Prints to the screen the details of a TestElement
 * @author Pietro Prandini (g2)
 */
public class TestDetailsFragment extends Fragment {
    /**
     * String used for the log of this class
     */
    private String TAG = "TestDetailsFragment -> ";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_element_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieves the TestElement to be viewed (default == 0)
        Intent lastIntent = getActivity().getIntent();
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
        TextView correctness = getActivity().findViewById(R.id.correctness_view);
        float confidence = entry.getConfidence();
        correctness.setText(TestUtils.formatPercentString(confidence));

        // Sets the color of the correctness text value
        correctness.setTextColor(TestUtils.chooseColorOfValue(confidence,redUntil,yellowUntil));

        // Sets the name of the pic
        TextView name = getActivity().findViewById(R.id.pic_name_view);
        String picName = entry.getFileName();
        name.setText(picName);

        // Sets the pic view
        ImageView analyzedPic = getActivity().findViewById(R.id.pic_view);
        String imagePath = entry.getImagePath();
        Bitmap img = Utils.loadBitmapFromFile(imagePath);
        analyzedPic.setImageBitmap(TestUtils.scaleBitmap(getContext(), img));

        // Sets the Tags text
        TextView tags = getActivity().findViewById(R.id.tags_view);
        StringBuilder assignedTags = new StringBuilder();
        for(String tag: entry.getTags()) {
            assignedTags.append(tag).append(", ");
        }
        tags.setText(assignedTags.toString());

        // Sets the ingredients text
        TextView ingredients = getActivity().findViewById(R.id.ingredients_view);
        StringBuilder realIngredients = new StringBuilder();
        for(String ingredient: entry.getIngredientsArray()) {
            realIngredients.append(ingredient).append(", ");
        }
        ingredients.setText(realIngredients);

        // Sets the extracted text
        TextView extractedText = getActivity().findViewById(R.id.extractedText_view);
        extractedText.setText(entry.getRecognizedText());

        // Sets the notes text
        TextView notes = getActivity().findViewById(R.id.notes_view);
        notes.setText(entry.getNotes());

        // Sets the ingredients extraction report
        TextView extractedIngredientsView = getActivity().findViewById(R.id.extracted_ingredients_view);
        extractedIngredientsView.setText(entry.getIngredientsExtraction());

        // Sets the alterations view
        TestUtils.setAlterationsView(
                getContext(),
                (RelativeLayout) getActivity().findViewById(R.id.result_view),
                extractedIngredientsView,
                entry,
                true
        );
    }
}
