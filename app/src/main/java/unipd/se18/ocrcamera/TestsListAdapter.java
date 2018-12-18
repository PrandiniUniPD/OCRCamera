package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Adapter for the view of the processing result of the pics
 * @author Pietro Prandini (g2)
 */
public class TestsListAdapter extends BaseAdapter {
    /**
     * String used for the logs of this class
     */
    private final String TAG = "TestsListAdapter -> ";

    /**
     * Context of the app
     */
    private Context context;

    /**
     * Elements of test
     */
    private static TestElement[] entries;

    /**
     * Defines an object of AdapterTestElement type
     * @param context The reference to the activity where the adapter will be used
     * @param entries The list of the test elements containing data from photos test
     */
    TestsListAdapter(Context context, TestElement[] entries) {
        this.context = context;
        TestsListAdapter.entries = entries;
    }

    @Override
    public int getCount() { return entries.length; }

    @Override
    public Object getItem(int position) { return entries[position]; }

    @Override
    public long getItemId(int position) {
        return TestUtils.getTestElementId(entries[position]);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.v(TAG, "getView of the test " + entries[position].getFileName()
                + " placed in the position == " + position);
        // Prepares the view of an element of the list
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.test_element, parent, false);
        }

        // Sets the correctness value
        TextView correctness = convertView.findViewById(R.id.correctness_view);
        float confidence = entries[position].getConfidence();
        correctness.setText(TestUtils.formatPercentString(confidence));

        // Sets the color of the correctness text value
        correctness.setTextColor(
                TestUtils.chooseColorOfValue(
                        confidence,
                        TestUtils.defaultRedUntil,
                        TestUtils.defaultYellowUntil
                )
        );

        // Sets the name of the pic
        TextView name = convertView.findViewById(R.id.pic_name_view);
        String picName = entries[position].getFileName();
        name.setText(picName);

        // Sets the Tags text
        TextView tags = convertView.findViewById(R.id.tags_view);
        StringBuilder assignedTags = new StringBuilder();
        for (String tag : entries[position].getTags()) {
            assignedTags.append(tag).append(", ");
        }
        tags.setText(assignedTags.toString());

        // Sets the alterations view
        TestUtils.setAlterationsView(
                context,
                (RelativeLayout) convertView.findViewById(R.id.result_view),
                R.id.tags_view,
                entries[position],
                false
        );

        // Sets the button that launches the details activity
        Button viewDetailsButton = convertView.findViewById(R.id.view_details_button);
        viewDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "View the details of the element " + entries[position].getFileName()
                        + " at the position == " + position);
                // Prepares the Intent for launching the TestDetailsActivity
                final Intent testDetailsActivity = new Intent(context, TestDetailsActivity.class);

                // Prepares the values to be passed by the intent
                testDetailsActivity.putExtra(TestUtils.positionString,position);
                testDetailsActivity.putExtra(TestUtils.redUntilString,TestUtils.defaultRedUntil);
                testDetailsActivity.putExtra(TestUtils.yellowUntilString,TestUtils.defaultYellowUntil);

                // Starts the details activity
                context.startActivity(testDetailsActivity);
            }
        });
        return convertView;
    }

    /**
     * Gets the test elements analyzed
     * @return The array of the Test Elements analyzed
     * @author Pietro Prandini (g2)
     */
    static TestElement[] getTestElements() { return TestsListAdapter.entries; }
}