package unipd.se18.ocrcamera;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * this fragment want to manage the action bar in the activity
 * in the xml files you need:
 * action_bar_layout.xml which has the buttons nested in a ViewGroup
 *
 * from your Activity call emphasizeButton() method to set state_selected of one of the
 * buttons.
 */
public class ActionBarFragment extends Fragment {

    /**
     * Instantiate user interface
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.action_bar_layout, container, false);
    }

    /**
     * Initialize the on click listener for the action bar buttons
     * @param view The view returned from onCreateView() method
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                          saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton cameraButton = (ImageButton)view.findViewById(R.id.action_bar_camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mayStartActivity(CameraActivity.class);
            }
        });

        ImageButton galleryButton = (ImageButton)view.findViewById(R.id.action_bar_gallery_button);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mayStartActivity(GalleryActivity.class);
            }
        });

        ImageButton settingButton = (ImageButton)view.findViewById(R.id.action_bar_settings_button);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mayStartActivity(SettingsActivity.class);
            }
        });

    }

    /**
     * Start a new Activity if is different from the current activity
     * @param cls the class of the activity to start
     */
    private void mayStartActivity(Class<?> cls) {
        FragmentActivity currentFragmentActivity = getActivity();
        if (currentFragmentActivity != null) {
            String currentClassName = currentFragmentActivity.getLocalClassName();
            String nextClassName = cls.getSimpleName();
            if (!currentClassName.equals(nextClassName)) {
                Intent intent = new Intent(currentFragmentActivity, cls);
                startActivity(intent);
            }
        }
    }

    /**
     * if idSelectedButton equals one of the actionBarViewGroup children's id, the button
     * state_selected is set to true. All the other children's state_selected are set to false.
     * @param actionBarViewGroup is the view which contain the action buttons
     * @param idSelectedButton is the id of the action button passed from R.id.button_id
     */
    protected static void emphasizeButton(ViewGroup actionBarViewGroup, int idSelectedButton) {
        //get the number of button in the action bar
        int totalActionButton = actionBarViewGroup.getChildCount();

        //for each button
        for (int i = 0; i < totalActionButton; i++) {
            View currentButton = actionBarViewGroup.getChildAt(i);

            // if it's the button to emphasize, set state_selected to true
            if (currentButton.getId() == idSelectedButton) {
                currentButton.setSelected(true);
            } else {  //else set state_selected to false
                currentButton.setSelected(false);
            }
        }
    }
}
