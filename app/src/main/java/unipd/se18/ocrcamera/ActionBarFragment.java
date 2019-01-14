package unipd.se18.ocrcamera;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
 * from your Activity call enphasizeButton() method to set state_selected of one of the
 * buttons.
 */
public class ActionBarFragment extends Fragment {

    /**
     * set the onClickListener for each buttons in the action_bar_layout
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.action_bar_layout, container, false);

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

        return view;
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
                Intent intent = new Intent(getActivity(), cls);
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
    protected static void enphasizeButton(ViewGroup actionBarViewGroup, int idSelectedButton) {
        int totalActionButton = actionBarViewGroup.getChildCount();
        for (int i = 0; i < totalActionButton; i++) {
            View currentButton = actionBarViewGroup.getChildAt(i);
            if (currentButton.getId() == idSelectedButton) {
                currentButton.setSelected(true);
            } else {
                currentButton.setSelected(false);
            }
        }
    }
}
