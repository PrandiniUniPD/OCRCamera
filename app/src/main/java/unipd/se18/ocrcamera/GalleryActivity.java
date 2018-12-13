package unipd.se18.ocrcamera;



import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Gallery activity
 * @author Stefano Romanello - Fragment suggestion Leonardo Rossi
 */
public class GalleryActivity extends AppCompatActivity {

    //Code for internet permission
    private final int REQUEST_PERMISSION_CODE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        if(verifyStoragePermission())
        {
            loadHomeFragment();
        }
    }

    /**
     * Function for load the home fragment from onActivityCreated and onRequestPermissionsResult in case I don't have the storage permission
     */
    private void loadHomeFragment()
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragmentPlaceHolder, new MainFragment(),getString(R.string.homeFragmentTag));
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Fragment of the gallery layout
     * @author Romanello Stefano
     */
    public static class MainFragment extends Fragment {


        GalleryManager.RecycleCardsAdapter cardAdapter;

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
            cardAdapter = new GalleryManager.RecycleCardsAdapter(view.getContext(), photos);
            picturesRecycleView.setAdapter(cardAdapter);


        }


        /**
         * Event when the main fragment change its hidden status.
         * @param hidden true=is hidden, false=is not hidden.
         */
        @Override
        public void onHiddenChanged(boolean hidden) {
            //I have to understand if the used has deleted an image
            //If the answer is yes I have to remove the item from the recycleView
            if(!hidden)
            {
                if(DetailFragment.deleteActionOccour!=null)
                {
                    //have to remove the photo from the recycler
                    int cardPosition = cardAdapter.getPhotoPosition(DetailFragment.deleteActionOccour);
                    cardAdapter.photosList.remove(cardPosition);
                    cardAdapter.notifyItemRemoved(cardPosition);
                }

                //Restore the actionBar
                ActionBar actionBar =((GalleryActivity)getActivity()).getSupportActionBar();
                actionBar.setTitle("Gallery");
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setHomeButtonEnabled(false);
            }

        }
    }

    /**
     * Fragment of the detailed photo layout
     * @author Romanello Stefano
     */
    public static class DetailFragment extends Fragment
    {
        private GalleryManager.PhotoStructure photoInfos;

        //this variable is used for undersend if I've deleted something when I close the DetailFragment
        //if this variable is null I didn't delete anything
        //If this variable is not null the value of the variable is the image (card) that I have to remove
        public static GalleryManager.PhotoStructure deleteActionOccour=null;

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

            //When I load the fragment I always set the delete action to false.
            deleteActionOccour=null;
        }

        /**
         * Event triggered when I create the view
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
        {
            // Defines the xml file for the fragment
            View view = inflater.inflate(R.layout.activitygallerydetails, parent, false);
            //Very important part. Whiout this line the fragment doens't know that it has a menu and it will not trigger onOptionsItemSelected
            setHasOptionsMenu(true);
            return view;
        }

        /**
         * Add the delete button to the actionBar
         */
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu items for use in the action bar
            inflater.inflate(R.menu.gallery_details_menu, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }

        /**
         * Listner for the back button action that will close the deailed Fragment
         * @param item value of the item clicked
         */
        @Override
        public boolean onOptionsItemSelected(MenuItem item)
                {
                    switch (item.getItemId()) {
                        //I've clicked the back buton on the actionBar
                        case android.R.id.home:
                            //Go back with fragment and restore the actionBar for the main gallery
                            closeDetailFragment();
                            break;
                        case R.id.galleryDelete:
                            deleteCurrentPhoto();
                            break;
            }
            return super.onOptionsItemSelected(item);
        }



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

        /**
         * Action triggered when I click the delete icon in the actionBar
         * Method colled from onOptionsItemSelected
         */
        private void deleteCurrentPhoto()
        {
            try {
                //Notify the MainFragment that I have deleted something and that he need to remove the card
                deleteActionOccour=photoInfos;

                //Delete the actual image
                GalleryManager.deleteImage(photoInfos);

                //Close current fragment
                closeDetailFragment();

            } catch (IOException e) {
                //Manage the exaption with a dialog
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }
                builder.setTitle("Error")
                        .setMessage("Error while deleting the photo. Retry")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) { }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }

        /**
         * Method for closing the current fragment
         */
        private void closeDetailFragment()
        {
            getFragmentManager().popBackStack();
        }
    }


    /*********************************************/
    /******************PERMISSIONS****************/
    /***********@author Romanello Stefano*********/
    /*********************************************/


    /**
     * Verify if the user granted the permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // permission denied
                    showPermissionErrorDialog();
                }
                else
                {
                    loadHomeFragment();
                }
                break;
            }
        }
    }

    /**
     * Verify if the app has the storage permission
     * @return boolean of the current status (before asking the permission)
     * if its false this prevent the app to load the fragment that uses the internal storage
     */
    public boolean verifyStoragePermission()
    {
        //Check and in case Ask for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Show a simple error message before closing the activity in case bad permissioN
     */
    public void showPermissionErrorDialog()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Permission error")
                .setMessage("You did not have authorized the app to use internal storage.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with closing the activity
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
