package unipd.se18.ocrcamera;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
 * This activity shows more information about the ingredient selected.
 * @author Francesco Pham
 */
public class IngredientDetailsActivity extends AppCompatActivity {

    private static final String TAG = "IngredDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_details);

        // get ingredient information from extra
        Intent intent = getIntent();
        final String inciName = intent.getStringExtra("NAME");
        final String description = intent.getStringExtra("DESCRIPTION");
        final String function = intent.getStringExtra("FUNCTION");

        //set on click listener on search button
        final Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //credit to Luca Moroldo
                Intent webSearchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
                webSearchIntent.putExtra(SearchManager.QUERY, inciName);
                startActivity(webSearchIntent);
            }
        });

        // show ingredient information
        TextView nameView = findViewById(R.id.inci_name_view);
        nameView.setText(inciName);

        TextView descriptionView = findViewById(R.id.description_view);
        descriptionView.setText(description);

        TextView functionView = findViewById(R.id.function_view);
        functionView.setText(function);

        final TextView wikipediaView = findViewById(R.id.wikipedia_view);

        // Get a RequestQueue
        RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        // Make request to wikipedia, searching for the ingredient.
        String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&" +
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
        RequestQueueSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
