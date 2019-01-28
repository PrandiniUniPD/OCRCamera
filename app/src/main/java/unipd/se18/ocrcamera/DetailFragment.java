package unipd.se18.ocrcamera;


import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import java.util.List;

import unipd.se18.ocrcamera.inci.Ingredient;
import unipd.se18.ocrcamera.inci.IngredientsExtractor;

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

        ActionBar actionBar =((MainActivity)getActivity()).getSupportActionBar();
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

        ListView ingredientsListView = ((MainActivity) getActivity()).findViewById(R.id.ingredients_list_gallery);
        ImageView imageView = ((MainActivity) getActivity()).findViewById(R.id.imageViewGalleryDetailPhoto);

        //Convert the arrayList<String> of ingredients obtained from the metadata to List<Ingredient>

        //Get a single string with all ingredients fount from the ocr
        String formattedIngredients = photoInfos.ingredients.toString()
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim();
        IngredientsExtractor extractor = InciSingleton.getInstance(((MainActivity) getActivity())).getIngredientsExtractor();
        List<Ingredient> ingredientsToPrint = extractor.findListIngredients(formattedIngredients);

        //Use tha adapter to display the ingredients
        AdapterIngredient adapter = new AdapterIngredient(((MainActivity) getActivity()), ingredientsToPrint);
        ingredientsListView.setAdapter(adapter);

        //event onClick for display the details of the ingredients
        ingredientsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ingredient selectedIngredient = (Ingredient) parent.getItemAtPosition(position);

                //performing a click on OCR recognized text causes a crash (because selectedIngredient is null)
                if(selectedIngredient != null) {
                    String inciName = selectedIngredient.getInciName();
                    String description = selectedIngredient.getDescription();
                    String function = selectedIngredient.getFunction();
                    FragmentManager fm = getFragmentManager();
                    IngredientDetailsFragment detailsFragment = IngredientDetailsFragment.newInstance(inciName, description, function);
                    detailsFragment.show(fm, "fragment_ingredient_details");
                }
            }
        });

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

