package unipd.se18.ocrcamera;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

/**
 * Fragment of the detailed photo layout
 * @author Romanello Stefano
 */
public class DetailFragment extends Fragment
{
    //Object used to store the photo that I want to load.
    private GalleryManager.PhotoStructure photoInfos;

    /**
     * Event triggered once everything in the activity have finished loading
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar =((GalleryActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.galleryDetailsFragmentTitle);
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
        //Very important part. Without this line the fragment does't know that it has a menu and it will not trigger onOptionsItemSelected
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
     * Listner for actionBar buttons of the DetailsFragment
     * @param item value of the item clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            //I've clicked the back buton on the actionBar
            case android.R.id.home: closeDetailFragment(); break;
            //Delete the current photo and close the detailsFragment
            case R.id.galleryDelete: deleteCurrentPhoto(); break;
            default: break;
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
            photoInfos = (GalleryManager.PhotoStructure)bundle.getSerializable(getString(R.string.serializableObjectName));
        }

        TextView txtIngredients = ((GalleryActivity) getActivity()).findViewById(R.id.textViewGalleryDetailIngredients);
        TextView txtPercentage = ((GalleryActivity) getActivity()).findViewById(R.id.textViewGalleryDetailPercentage);
        ImageView imageView = ((GalleryActivity) getActivity()).findViewById(R.id.imageViewGalleryDetailPhoto);

        //Fill the detailed page with informations
        String formattedIngredients = photoInfos.ingredients.toString()
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim();
        txtIngredients.setText(formattedIngredients);
        txtPercentage.setText(photoInfos.reliability);
        imageView.setImageBitmap(photoInfos.photo);
    }

    /**
     * Action triggered when I click the delete icon in the actionBar
     * Method colled from onOptionsItemSelected
     */
    private void deleteCurrentPhoto()
    {
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
    private void closeDetailFragment()
    {
        getFragmentManager().popBackStack();
    }
}
