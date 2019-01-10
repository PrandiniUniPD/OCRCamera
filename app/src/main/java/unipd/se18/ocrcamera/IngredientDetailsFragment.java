package unipd.se18.ocrcamera;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


/**
 * Dialog Fragment that shows details about the ingredient selected such as name, description,
 * function and an extract from wikipedia.
 * @author Francesco Pham
 */
public class IngredientDetailsFragment extends DialogFragment {

    private static final String TAG = "IngredDetailsFragment";

    private static final String ARG_NAME = "name";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_FUNCTION = "function";

    public IngredientDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Method for the fragment creation
     * @param inciName Title to show in the fragment
     * @param description Description of the ingredient
     * @param function Functions of the ingredient
     * @return
     */
    public static IngredientDetailsFragment newInstance(String inciName, String description, String function) {
        IngredientDetailsFragment fragment = new IngredientDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, inciName);
        args.putString(ARG_DESCRIPTION, description);
        args.putString(ARG_FUNCTION, function);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ingredient_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            final String inciName = getArguments().getString(ARG_NAME);
            final String description = getArguments().getString(ARG_DESCRIPTION);
            final String function = getArguments().getString(ARG_FUNCTION);

            //set on click listener on search button
            final Button searchButton = view.findViewById(R.id.search_button);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //credit to Luca Moroldo
                    Intent webSearchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
                    webSearchIntent.putExtra(SearchManager.QUERY, inciName);
                    startActivity(webSearchIntent);
                }
            });

            //set close button listener
            final Button closeButton = view.findViewById(R.id.close_button);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            // show ingredient information
            TextView nameView = view.findViewById(R.id.inci_name_view);
            nameView.setText(inciName);

            TextView descriptionView = view.findViewById(R.id.description_view);
            descriptionView.setText(description);

            TextView functionView = view.findViewById(R.id.function_view);
            functionView.setText(function);

            final TextView wikipediaView = view.findViewById(R.id.wikipedia_view);

            // Get a RequestQueue
            RequestQueueSingleton.getInstance(getActivity()).getRequestQueue();

            // Make request to wikipedia, searching for ingredient informations.
            assert inciName != null;
            final String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&" +
                    "exintro&explaintext&redirects=1&titles="+inciName.toLowerCase();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONObject query = (JSONObject) response.get("query");
                                JSONObject pages = (JSONObject) query.get("pages");
                                Iterator<String> keys = pages.keys();

                                while(keys.hasNext()) {
                                    String key = keys.next();
                                    if (!key.equals("-1") && pages.get(key) instanceof JSONObject) {
                                        //show wikipedia extract
                                        JSONObject page = (JSONObject) pages.get(key);
                                        String wikipediaExtract = (String) page.get("extract");
                                        wikipediaView.setText(wikipediaExtract);
                                    } else {
                                        //wikipedia page not found
                                        wikipediaView.setText(R.string.wikipedia_not_found);
                                    }
                                }

                            } catch (JSONException e) {
                                Log.e(TAG, "invalid response from wikipedia");
                                wikipediaView.setText(R.string.wikipedia_failed_request);
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Could not send request to wikipedia");
                            wikipediaView.setText(R.string.wikipedia_failed_request);
                        }
                    });

            // Add the request to the RequestQueue.
            RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //set dialog fragment size (width and height values in fragment_ingredients_details.xml do not work)
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}