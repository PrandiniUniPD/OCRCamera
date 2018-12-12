package unipd.se18.ocrcamera;

import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.util.DataUtils;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        //Load the gallery layout
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentPlaceHolder, new GalleryActivity.MainFragment());
        ft.commit();
    }

    //Fragment of the gallery layout
    public static class MainFragment extends Fragment {
        // The onCreateView method is called when Fragment should create its View object hierarchy,
        // either dynamically or via XML layout inflation.
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            // Defines the xml file for the fragment
            return inflater.inflate(R.layout.activity_gallery_content, parent, false);
        }

        // This event is triggered soon after onCreateView().
        // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {

            //Create the container for all my cards
            RecyclerView picturesRecycleView = view.findViewById(R.id.recycle_view);
            picturesRecycleView.setHasFixedSize(true);

            //Set a grid (2 columns) from my recycleView
            GridLayoutManager recycleLayoutManager = new GridLayoutManager(view.getContext(), 2, GridLayoutManager.VERTICAL, false);
            picturesRecycleView.setLayoutManager(recycleLayoutManager);
            picturesRecycleView.setItemAnimator(new DefaultItemAnimator());

            //Load the cards using the RecycleCardsAdapter into the picturesRecycleView
            ArrayList<GalleryManager.PhotoStructure> photos = GalleryManager.getImages(view.getContext());
            GalleryManager.RecycleCardsAdapter cardAdapter = new GalleryManager.RecycleCardsAdapter(view.getContext(), photos);
            picturesRecycleView.setAdapter(cardAdapter);
        }
    }

    //Fragment of the detailed photo layout
    public static class DetailFragment extends Fragment {
        // The onCreateView method is called when Fragment should create its View object hierarchy,
        // either dynamically or via XML layout inflation.
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            // Defines the xml file for the fragment
            return inflater.inflate(R.layout.activitygallerydetails, parent, false);
        }

        // This event is triggered soon after onCreateView().
        // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            // Setup any handles to view objects here
            // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
            ((GalleryActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
