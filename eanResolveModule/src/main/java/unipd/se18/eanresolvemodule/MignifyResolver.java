package unipd.se18.eanresolvemodule;

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
    //The authentication string from https://market.mashape.com/mignify/product-intelligence# after
    //you subscribe to the Mignify API
    private static final String MASHAPE_AUTH = "Nx432kOz2Wmsh83IAjUv6cUaxT7Rp1tDUbKjsnu7ueSdRFdOPK";

    /**
     *
     * @param EANCode string extracted from an EAN13 barcode
     * @return the product relative to the input string
     */
    @Override
    public String decodeEAN(String EANCode) {

        String fullURL = URL + EANCode;
        //HTTP response from get method to Mignify API
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest
                    .get(fullURL)
                    //authentication header
                    .header("X-Mashape-Key",
                            MASHAPE_AUTH)
                    //asking for a json response
                    .header("Accept", "application/json")
                    //type of response we expect
                    .asJson();
        } catch (UnirestException e) {
            //TODO auto-generated catch block
            e.printStackTrace();
        }
        if (response == null) {
            product = "Unable to connect to Mashape.";
        } else {
            try {
                //Retrieve the parsed JSONObject from the Mashape response
                JSONObject jsonResponse = response.getBody().getObject();
                String status = jsonResponse.getString("status");
                if (status.equals("ok")) {
                    JSONArray productList = jsonResponse.getJSONArray("results");
                    String[] productNames = new String[productList.length()];
                    String[] productBrands = new String[productList.length()];
                    String[] productLanguages = new String[productList.length()];
                    if (jsonResponse.length() == 0) {
                        product = "No product found in Mignify database.";
                    } else {
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject currentJsonObject = productList.getJSONObject(i);

                            productNames[i] = currentJsonObject.getString("productName");
                            productBrands[i] = currentJsonObject.getString("brand");
                            productLanguages[i] = currentJsonObject.getString("languageCode");
                        }

                        //TODO better string selection
                        //Selecting the first name in the list as the result product name
                        product = productNames[0];
                    }
                } else if(status.equals("error")){
                    product = jsonResponse.getString("message");
                }
            } catch (JSONException e){
                //TODO auto-generated catch block
                e.printStackTrace();
            }
        }

        return product;
    }
}
