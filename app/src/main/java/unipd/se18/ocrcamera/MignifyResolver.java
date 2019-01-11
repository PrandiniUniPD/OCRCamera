package unipd.se18.eanresolvemodule;

import android.util.Log;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class implements the Mignify API for EAN13 code resolve
 * More at https://market.mashape.com/mignify/product-intelligence#
 * @author Elia Bedin
 */
public class MignifyResolver implements EAN {

    //The string resolved from an EAN product code
    private String product = "";
    //The string passed to the get method, postponed by the EAN string code
    private static final String URL =
            "https://mignify.p.mashape.com/gtins/v1.0/productsToGtin?gtin=";
    //The authentication string you get from
    //https://market.mashape.com/mignify/product-intelligence#
    //after you subscribe to the Mignify API
    private static final String MASHAPE_AUTH = "Nx432kOz2Wmsh83IAjUv6cUaxT7Rp1tDUbKjsnu7ueSdRFdOPK";
    //Error text when connecting to the Mashape API
    private static final String HTTP_ERROR = "Unable to connect to the server.";
    //Error text if there's no product with entered EAN code in the database
    private static final String NO_PRODUCT_ERROR =
            "No product found in the database for that barcode.";
    //Error text if there was an error parsing the JSON response
    private static final String JSON_ERROR = "Error parsing the response from the server.";

    /**
     *
     * @param EANCode string extracted from an EAN13 barcode
     * @return the product relative to the input string
     */
    @Override
    public String decodeEAN(String EANCode) {

        //Adding to the URL request the EAN code taken from the barcode
        String fullURL = URL + EANCode;
        //HTTP response from get method to Mignify API
        HttpResponse<JsonNode> response = null;
        //Unirest library useful for making an HTTP request for the Mashape API
        //More at http://unirest.io/java
        try {
            response = Unirest
                    .get(fullURL)
                    //Authentication header
                    .header("X-Mashape-Key",
                            MASHAPE_AUTH)
                    //Accept and Content-Type selection header
                    .header("Accept", "application/json")
                    //Type of response we expect
                    .asJson();
        } catch (UnirestException e) {
            Log.e("Unirest error", e.getMessage());
        }
        if (response == null) {
            product = HTTP_ERROR;
        } else {
            try {
                //Retrieve the parsed JSONObject from the Mashape response
                JSONObject jsonResponse = response.getBody().getObject();
                //Getting the status from the JSON
                //The server responds with either "ok" or "error"
                String status = jsonResponse.getString("status");
                if (status.equals("ok")) {
                    //If the EAN code was found, in the JSON there is an list of products, each
                    //one with name, brand and language
                    JSONArray productList = jsonResponse.getJSONArray("results");
                    String[] productNames = new String[productList.length()];
                    //Future proofing usage of brands and language, still unimplemented
                    String[] productBrands = new String[productList.length()];
                    String[] productLanguages = new String[productList.length()];
                    //If no product are in the response
                    if (jsonResponse.length() == 0) {
                        product = NO_PRODUCT_ERROR;
                    } else {
                        //Getting name, brand and language for each product
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            //Getting the current product from the JSON list and it's values
                            JSONObject currentJsonObject = productList.getJSONObject(i);
                            productNames[i] = currentJsonObject.getString("productName");
                            productBrands[i] = currentJsonObject.getString("brand");
                            productLanguages[i] = currentJsonObject.getString("languageCode");
                        }

                        //Selecting the first name in the list as the result product name.
                        //It should be the most plausible product name.
                        //In the future a better selection will be made, checking brand and language
                        product = productNames[0];
                    }
                } else if(status.equals("error")){
                    //In case the EAN wasn't valid, the error in the JSON will be returned
                    product = jsonResponse.getString("message");
                }
            } catch (JSONException e){
                product = JSON_ERROR;
                Log.e("JSON error", e.getMessage());
            }
        }
        return product;
    }
}
