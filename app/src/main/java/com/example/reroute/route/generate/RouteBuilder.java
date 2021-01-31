package com.example.reroute.route.generate;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.reroute.R;
import com.google.android.libraries.places.api.model.Place;

/**
 * This class is responsible for building the random route by using the Google Maps Places Search API.
 */
class RouteBuilder {
    private final static String TAG = "[PLACE]";
    private static RouteBuilder instance;

    private RouteBuilder() {
    }

    /**
     * Setup a single instance of this class
     */
    static RouteBuilder getInstance() {
        if (instance == null) {
            instance = new RouteBuilder();
        }
        return instance;
    }

    void generateRoute(Context context, Place place, int distance) {
        VolleyController controller = VolleyController.getInstance(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, buildHttpRequest(context, place, distance), null, response -> {
                    Log.i(TAG, "Response " + response.toString());
                }, error -> {
                    Log.e(TAG, "Error making Places Search request " + error.getMessage());
                });
        controller.addToRequestQueue(context, jsonObjectRequest);
    }

    /**
     * Builds the Places API Search request which is an HTTP URL
     * @param context
     * @param place
     * @param distance
     * @return String representing the request
     */
    private String buildHttpRequest(Context context, Place place, int distance) {
        StringBuilder builder = new StringBuilder();

        if (place.getName() != null && place.getLatLng() != null) {
            builder.append("https://maps.googleapis.com/maps/api/place/textsearch/json?query=");
            builder.append(place.getName().replaceAll(" ", "+"));
            builder.append("&location=");
            builder.append(place.getLatLng().latitude);
            builder.append(",");
            builder.append(place.getLatLng().longitude);
            builder.append("&radius=100");
            builder.append("&key=");
            builder.append(context.getString(R.string.places_api_key));
        } else if (place.getName() == null) {
            Log.i(TAG, "Name is null");
        } else if (place.getLatLng() == null) {
            Log.i(TAG, "Lat Long is null");
        }
        Log.i(TAG, "HTTP Request: " + builder.toString());

        return builder.toString();
    }
}
