package unipd.se18.ocrcamera;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/*
 * @author Pietro Balzan
 */
public class UserAllergensFragment extends Fragment {
    private static final String TAG = "UserAllergensFragment";
    private ListView mAllergensListView;
    private AllergensManager mAllergensManager;
    private AllergenListAdapter mAllergensListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //the inflater is not considered as child of "parent"
        final boolean isParent = false;

        return inflater.inflate(R.layout.fragment_users_allergens, container,  isParent);
    }

    /**
     * This method is used to get the View that will make the fragment' layout in the Activity
     * @param fragmentView the view that was inflated in onCreateView
     * @param savedInstanceState
     * @return fragmentView
     * @author Pietro Balzan
     */
    @Override
    public void onViewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragmentView, savedInstanceState);
        mAllergensListView = (ListView) fragmentView.findViewById(R.id.users_allergens_list_view);

        mAllergensManager = InciSingleton.getInstance(getActivity()).getAllergensManager();
        //initialize the user's own list
        ArrayList<Allergen> userList= mAllergensManager.getSelectedAllergensList();
        //create mAllergensListAdapter with the list of the users' allergens
        mAllergensListAdapter= new AllergenListAdapter(getActivity(), R.layout.allergen_single, userList);
        mAllergensListView.setAdapter(mAllergensListAdapter);
    }

    /**
     * When the user focus the fragment, update mAllergensListView to show any change
     * @param isVisibleToUser
     * @author Luca Moroldo g3
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            mAllergensListAdapter.clear();

            ArrayList<Allergen> userAllergensList = mAllergensManager.getSelectedAllergensList();
            mAllergensListAdapter = new AllergenListAdapter(getActivity(), R.layout.allergen_single, userAllergensList);
            mAllergensListView.setAdapter(mAllergensListAdapter);
        }
    }
}
