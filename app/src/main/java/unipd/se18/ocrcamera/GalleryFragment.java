package unipd.se18.ocrcamera;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {


    //Code for internet permission
    private static final int REQUEST_PERMISSION_CODE = 500;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (verifyStoragePermission()) {
            loadHomeFragment();
        }
    }

    /**
     * Function for load the home fragment from onActivityCreated and
     * onRequestPermissionsResult in case I get the storage permission from the permission
     */
    FragmentManager fm;

    private void loadHomeFragment() {
        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragmentPlaceHolder, new MainFragment(), getString(R.string.homeFragmentTag));
        ft.commit();
    }



    /************************************************************/
    /***************************FRAGMENTS************************/
    /************************************************************/


    /**
     * Fragment of the gallery layout
     *
     * @author Romanello Stefano
     */
    public static class MainFragment extends Fragment {

        GalleryManager.RecycleCardsAdapter cardAdapter;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_gallery_content, parent, false);


            return view;

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
            cardAdapter = new GalleryManager.RecycleCardsAdapter(getActivity(), photos);
            picturesRecycleView.setAdapter(cardAdapter);
        }

        /**
         * Event when the main fragment change its hidden status.
         * @param hidden true=is hidden, false=is not hidden.
         */
        @Override
        public void onHiddenChanged(boolean hidden) {
            //I have to understand if the user has deleted an image
            //If the answer is yes I have to remove the item from the recycleView
            if(!hidden)
            {
                //When I delete something from the DetailsFragment using the method GalleryManager.deleteImage(photoInfos);
                //I set in the GalleryManager an object containing the PhotoStructure to delete.
                //Calling .getDeletedPhotoPosition() I check if the object is null, if not I return the position of
                //the PhotoStructure to delete

                int deletedPosition=cardAdapter.getDeletedPhotoPosition();
                if(deletedPosition!=-1)
                {
                    //have to remove the photo from the recycler
                    cardAdapter.photosList.remove(deletedPosition);
                    cardAdapter.notifyItemRemoved(deletedPosition);
                }

                //Restore the actionBar
            }

        }
    }

        /**
         * Fragment of the detailed photo layout
         *
         * @author Romanello Stefano
         */
        public static class DetailFragment extends Fragment {
            //Object used to store the photo that I want to load.
            private GalleryManager.PhotoStructure photoInfos;


            /**
             * Event triggered when I create the view
             */
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
                // Defines the xml file for the fragment
                View view = inflater.inflate(R.layout.activitygallerydetails, parent, false);
                //Very important part. Without this line the fragment does't know that it has a menu and it will not trigger onOptionsItemSelected
                setHasOptionsMenu(true);
                return view;
            }


            @Override
            public void onViewCreated(View view, Bundle savedInstanceState) {
                //Load the information from the bundle
                Bundle bundle = getArguments();
                if (bundle != null) {
                    photoInfos = (GalleryManager.PhotoStructure) bundle.getSerializable(getString(R.string.serializableObjectName));
                }

                ListView ingredientsListView = (getActivity()).findViewById(R.id.ingredients_list_gallery);
                ImageView imageView = (getActivity()).findViewById(R.id.imageViewGalleryDetailPhoto);

                imageView.setImageBitmap(photoInfos.photo);
            }

            /**
             * Action triggered when I click the delete icon in the actionBar
             * Method colled from onOptionsItemSelected
             */
            private void deleteCurrentPhoto() {
                try {
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
                                public void onClick(DialogInterface dialog, int which) {
                                    //I'm not performing any action because this allert is just for provide information
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }

            /**
             * Method for closing the current fragment
             */
            private void closeDetailFragment() {
                getChildFragmentManager().popBackStack();
            }
        }


        /*********************************************/
        /******************PERMISSIONS****************/
        /*********************************************/


        /**
         * Verify if the user granted the permission
         */
        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (requestCode == REQUEST_PERMISSION_CODE) {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // permission denied
                    showPermissionErrorDialog();
                } else {
                    loadHomeFragment();
                }
            }
        }

        /**
         * Verify if the app has the storage permission
         *
         * @return boolean of the current status (before asking the permission)
         * if its false this prevent the app to load the fragment that uses the internal storage
         */
        public boolean verifyStoragePermission() {
            //Check and in case Ask for permission
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_CODE);
                return false;
            } else {
                return true;
            }
        }

        /**
         * Show a simple error message before closing the activity in case bad permissioN
         */
        public void showPermissionErrorDialog() {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(getActivity());
            }
            builder.setTitle("Permission error")
                    .setMessage("You did not have authorized the app to use internal storage.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with closing the activity
                            getChildFragmentManager().popBackStack();

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }
