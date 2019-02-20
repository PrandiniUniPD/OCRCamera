package unipd.se18.eanresolvemodule;

import android.util.Log;
import com.mashape.unirest.http.*;
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
    /*
     * URL and header strings declaration
     */
    //The string passed to the get method, postponed by the EAN string code
    private static final String BASE_URL =
            "https://mignify.p.mashape.com/gtins/v1.0/productsToGtin?gtin=";
    //Type of http authentication
    private static final String AUTH_TYPE = "X-Mashape-Key";
    //The authentication string you get from
    //https://market.mashape.com/mignify/product-intelligence#
    //after you subscribe to the Mignify API
    //private static final String MASHAPE_AUTH = "Nx432kOz2Wmsh83IAjUv6cUaxT7Rp1tDUbKjsnu7ueSdRFdOPK";
    private static final String MASHAPE_AUTH = "m2GurVPnwZmshBibQLntKBBpPe3mp1MdKF6jsnJg4SoxE9qSH0";
    //Name of the header for selecting content type
    private static final String ACCEPT_HEADER_TYPE = "Accept";
    //Content type wanted as response
    private static final String CONTENT_TYPE = "application/json";
    //Error text when connecting to the Mashape API
    /*
     *JSON keys declaration: all the keys from the JSON response that are used
     */
    private static final String STATUS   = "status";
    private static final String PAYLOAD  = "payload";
    private static final String RESULTS  = "results";
    private static final String NAME     = "productName";
    private static final String BRAND    = "brand";
    private static final String LANGUAGE = "languageCode";
    private static final String MESSAGE  = "message";
    /*
     * Error strings declaration
     */
    private static final String HTTP_ERROR = "Unable to connect to the server.";
    //Error text if there's no product with entered EAN code in the database
    private static final String NO_PRODUCT_ERROR =
            "No product found in the database for that barcode.";
    //Error text if there was an error parsing the JSON response
    private static final String JSON_ERROR = "Error parsing the response from the server.";
    //Error text if the EAN code was invalid
    private static final String INVALID_BARCODE = "The barcode value is invalid.";

    /**
     *
     * @param EANCode string extracted from an EAN13 barcode
     * @return the product relative to the input string
     */
    @Override
    public String decodeEAN(String EANCode) {

        //Adding to the URL request the EAN code taken from the barcode
        String fullURL = BASE_URL + EANCode;
        //HTTP response from get method to Mignify API
        HttpResponse<JsonNode> response = null;
        //Unirest library useful for making an HTTP request for the Mashape API
        //More at http://unirest.io/java
        try {
            response = Unirest
                    .get(fullURL)
                    //Authentication header
                    .header(AUTH_TYPE,
                            MASHAPE_AUTH)
                    //Accept and Content-Type selection header
                    .header(ACCEPT_HEADER_TYPE, CONTENT_TYPE)
                    //Type of response we expect
                    .asJson();
            //Retrieve the parsed JSONObject from the Mashape response
            JSONObject jsonResponse = response.getBody().getObject();
            //Getting the status from the JSON
            //The server responds with either "ok" or "error"
            String status = jsonResponse.getString(STATUS);
            if (status.equals("ok")) {
                //Getting the payload from the JSON
                JSONObject payload = jsonResponse.getJSONObject(PAYLOAD);
                //Since the EAN code was found, inside the JSON there's a list of products, each
                //one with name, brand and language
                JSONArray productList = payload.getJSONArray(RESULTS);
                //Getting the number of products in the list
                int productListSize = productList.length();
                //If no product are in the response
                if (productListSize == 0) {
                    product = NO_PRODUCT_ERROR;
                } else {
                    String[] productNames = new String[productList.length()];
                    //Future proofing usage of brands and language, still unimplemented
                    String[] productBrands = new String[productList.length()];
                    String[] productLanguages = new String[productList.length()];
                    //Getting name, brand and language for each product
                    for (int i = 0; i < productListSize; i++) {
                        //Getting the current product from the JSON list and it's values
                        JSONObject currentJsonObject = productList.getJSONObject(i);
                        productNames[i] = currentJsonObject.getString(NAME);
                        productBrands[i] = currentJsonObject.getString(BRAND);
                        productLanguages[i] = currentJsonObject.getString(LANGUAGE);
                    }
                    /*
                     * Selecting the first name in the list as the result product name.
                     * It should be the most plausible product name.
                     * In the future a better selection will be made, checking brand and language
                     */
                    //If there's a product brand string and it isn't already contained inside the
                    //product name string then it's added to the result
                    if(!(productBrands[0] == null) && !productNames[0].toLowerCase()
                            .contains(productBrands[0].toLowerCase())) {
                        product = productBrands[0] + " ";
                    }
                    product += productNames[0];
                }
            } else if(status.equals("error")){
                //This else if should never be entered since the Barcode Module already checks
                //if the barcode value if correct.
                //In case the EAN wasn't valid, the invalid barcode error is returned
                Log.d("MignifyResolver -> ",
                        "onInvalidBarcode ->"+ jsonResponse.getString(MESSAGE));
                product = INVALID_BARCODE;
            }
        } catch (UnirestException e) {
            product = HTTP_ERROR;
            Log.e("Unirest error", e.getMessage());
        } catch (JSONException e) {
            product = JSON_ERROR;
            Log.e("JSON error", e.getMessage());
        }
        return product;
    }
}
