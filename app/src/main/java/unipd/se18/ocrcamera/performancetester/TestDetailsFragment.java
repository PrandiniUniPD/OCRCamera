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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_element_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieves the TestElement to be viewed (default == 0)
        Intent lastIntent = requireActivity().getIntent();
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
        TextView correctness = requireActivity().findViewById(R.id.correctness_view);
        float confidence = entry.getConfidence();
        correctness.setText(TestUtils.formatPercentString(confidence));

        // Sets the color of the correctness text value
        correctness.setTextColor(TestUtils.chooseColorOfValue(confidence,redUntil,yellowUntil));

        // Sets the name of the pic
        TextView name = requireActivity().findViewById(R.id.pic_name_view);
        String picName = entry.getFileName();
        name.setText(picName);

        // Sets the pic view
        ImageView analyzedPic = requireActivity().findViewById(R.id.pic_view);
        String imagePath = entry.getImagePath();
        Bitmap img = Utils.loadBitmapFromFile(imagePath);
        analyzedPic.setImageBitmap(TestUtils.scaleBitmap(requireContext(), img));

        // Sets the Tags text
        TextView tags = requireActivity().findViewById(R.id.tags_view);
        StringBuilder assignedTags = new StringBuilder();
        for(String tag: entry.getTags()) {
            assignedTags.append(tag).append(", ");
        }
        tags.setText(assignedTags.toString());

        // Sets the ingredients text
        TextView ingredients = requireActivity().findViewById(R.id.ingredients_view);
        StringBuilder realIngredients = new StringBuilder();
        for(String ingredient: entry.getIngredientsArray()) {
            realIngredients.append(ingredient).append(", ");
        }
        ingredients.setText(realIngredients);

        // Sets the extracted text
        TextView extractedText = requireActivity().findViewById(R.id.extractedText_view);
        extractedText.setText(entry.getRecognizedText());

        // Sets the notes text
        TextView notes = requireActivity().findViewById(R.id.notes_view);
        notes.setText(entry.getNotes());

        // Sets the ingredients extraction report
        TextView extractedIngredientsView =
                requireActivity().findViewById(R.id.extracted_ingredients_view);
        extractedIngredientsView.setText(entry.getIngredientsExtraction());

        // Sets the alterations view
        TestUtils.setAlterationsView(
                requireContext(),
                (RelativeLayout) requireActivity().findViewById(R.id.result_view),
                extractedIngredientsView,
                entry,
                true
        );
    }
}
