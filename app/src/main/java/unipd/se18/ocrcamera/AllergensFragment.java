package unipd.se18.ocrcamera;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment that manages the allergens

 */
public class AllergensFragment extends Fragment {

    private static final String TAG = "GalleryFragment";


    public AllergensFragment() {
        //null constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.gallery_layout, container, false);

        //code inside here


        return view;
    }


}
