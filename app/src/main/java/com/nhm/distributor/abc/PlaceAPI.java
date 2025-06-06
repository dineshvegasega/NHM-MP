package com.nhm.distributor.abc;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class PlaceAPI {

    private static final String TAG = PlaceAPI.class.getSimpleName();
//    private static final String PLACES_API_BASE1 = WSContants.Companion.getBASE()

//    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
//    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
//    private static final String OUT_JSON = "/json";
//
//    private static final String API_KEY = "AIzaSyCu3uKvAohPy9jgqw1CluTCAsSS6XKRvq4";

    public ArrayList<String> autocomplete (String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
//            StringBuilder sb = new StringBuilder(WSContants.PLACES_API_BASE + WSContants.TYPE_AUTOCOMPLETE + WSContants.OUT_JSON);
//            sb.append("?key=" + WSContants.API_KEY);
//           // sb.append("&types=(cities)");
//            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL("https://fakestoreapi.com/products/category/"+input);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {


            // Create a JSON object hierarchy from the results
//            JSONObject jsonObj = new JSONObject(jsonResults.toString());
//            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
//
//            // Extract the Place descriptions from the results
//            resultList = new ArrayList<String>(predsJsonArray.length());
//            for (int i = 0; i < predsJsonArray.length(); i++) {
//                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
//            }

            JSONArray predsJsonArray = new JSONArray(jsonResults.toString());
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                JSONObject predsJsonObject = predsJsonArray.getJSONObject(i);
                String title = predsJsonObject.getString("title");
                Log.e(TAG, "XXXXXXXXXXX "+title);
                resultList.add(title);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
}
