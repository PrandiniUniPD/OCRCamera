package unipd.se18.ocrcamera;



import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import unipd.se18.ocrcamera.inci.Ingredient;
import unipd.se18.ocrcamera.inci.IngredientsExtractor;
import unipd.se18.ocrcamera.inci.NameMatchIngredientsExtractor;

/**
 * Fragment of the gallery layout
 * @author Romanello Stefano
 */
public class MainFragment extends Fragment {

    GalleryManager.RecycleCardsAdapter cardAdapter;

    /**
     * Event triggered once everything in the activity have finished loading
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar =((FragmentManagerDesign)getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.galleryFragmentTitle);
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
        ArrayList<GalleryManager.PhotoStructure> photos = GalleryManager.getImages();
        cardAdapter = new GalleryManager.RecycleCardsAdapter(view.getContext(), photos);
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
            ActionBar actionBar =((FragmentManagerDesign)getActivity()).getSupportActionBar();
            actionBar.setTitle(R.string.galleryFragmentTitle);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
        }

    }
}
