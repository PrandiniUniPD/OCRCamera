package unipd.se18.ocrcamera;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Gallery activity
 * @author Stefano Romanello - Fragment suggestion Leonardo Rossi
 */
public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragmentPlaceHolder, new MainFragment(),"home");
        ft.commit();
    }

    /**
     * Fragment of the gallery layout
     */
    public static class MainFragment extends Fragment {

        /**
         * Event triggered once everything in the activity have finished loading
         */
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);

            ActionBar actionBar =((GalleryActivity)getActivity()).getSupportActionBar();
            actionBar.setTitle("Gallery");
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
        }

        /**
         * Event triggered when I create the view
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
        {
            // Defines the xml file for the fragment
            return inflater.inflate(R.layout.activity_gallery_content, parent, false);
        }

        /**
         * This event is triggered soon after onCreateView().
         * Any view setup should occur here.
         */
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState)
        {

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

    /**
     * Fragment of the detailed photo layout
     */
    public static class DetailFragment extends Fragment
    {
        private GalleryManager.PhotoStructure photoInfos;

        /**
         * Event triggered once everything in the activity have finished loading
         */
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);

            ActionBar actionBar =((GalleryActivity)getActivity()).getSupportActionBar();
            actionBar.setTitle("Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        /**
         * Event triggered when I create the view
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
        {
            // Defines the xml file for the fragment
            View view = inflater.inflate(R.layout.activitygallerydetails, parent, false);
            setHasOptionsMenu(true);
            return view;
        }

        /**
         * Listner for the back button action that will close the deailed Fragment
         * @param item value of the item clicked
         */
        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            switch (item.getItemId()) {
                case android.R.id.home:
                    //Go back with fragment and restore the actionBar for the main gallery
                    getFragmentManager().popBackStack();
                    ActionBar actionBar =((GalleryActivity)getActivity()).getSupportActionBar();
                    actionBar.setTitle("Gallery");
                    actionBar.setDisplayHomeAsUpEnabled(false);
                    actionBar.setHomeButtonEnabled(false);
                    break;
            }
            return super.onOptionsItemSelected(item);
        }


        //

        /**
         * This event is triggered soon after onCreateView()
         */
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState)
        {

            //Load the information from the bundle
            Bundle bundle = getArguments();
            if (bundle != null) {
                photoInfos = (GalleryManager.PhotoStructure)bundle.getSerializable("photoObject");
            }

            TextView txtIngredients = ((GalleryActivity) getActivity()).findViewById(R.id.textViewGalleryDetailIngredients);
            TextView txtPercentage = ((GalleryActivity) getActivity()).findViewById(R.id.textViewGalleryDetailPercentage);
            ImageView imageView = ((GalleryActivity) getActivity()).findViewById(R.id.imageViewGalleryDetailPhoto);

            //Fill the detailed page with informations
            String formattedIngredients = photoInfos.ingredients.toString()
                    .replace("[", "")  //remove the right bracket
                    .replace("]", "")  //remove the left bracket
                    .trim();
            txtIngredients.setText("Ingredients: " + formattedIngredients);
            txtPercentage.setText("Reliability: " +photoInfos.reliability);
            imageView.setImageBitmap(photoInfos.photo);
        }
    }
}
