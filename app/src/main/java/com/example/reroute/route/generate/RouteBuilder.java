package com.example.reroute.route.generate;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.reroute.R;
import com.google.android.libraries.places.api.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class is responsible for building the random route by using the Google Maps Places Search API.
 */
class RouteBuilder {

    private final static String TAG = "[PLACE]";
    private final static int MAX_DISTANCE_DIFFERENCE = 3;

    private static RouteBuilder instance;
    private RouteBuilderCallback routeBuilderCallback;
    private VolleyController volleyController;
    private int routeDistanceSelected;

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

    void generateRoute(Context context, RouteBuilderCallback callback, Place origin, int distance) {
        Log.i(TAG, "RouteBuilder called with DEBUG parameters, ORIGIN is null and distance is 20!!");
        volleyController = VolleyController.getInstance(context);
        this.routeBuilderCallback = callback;
        routeDistanceSelected = distance;

        //Create and send the request for places within a certain radius using Google Maps Places Search
        sendRequest(context, buildPlacesSearchRequest(context, origin, distance), response -> {
            Log.i(TAG, "Successfully received response from Places Search API");
            calculateDistances(response, context, origin);
        }, error -> {
            Log.e(TAG, "Error making Places Search API request " + error.getMessage());
            routeBuilderCallback.onError();
        });
    }

    private void calculateDistances(JSONObject addressResponse, Context context, Place origin) {
        //Parse the addresses and Place ID from the Nearby Search response
        ArrayList<Waypoint> waypoints = parseSearchResponse(addressResponse);
        for (int i = 0; i < waypoints.size(); i++) {
            Log.i(TAG, "PLACE " + i + ": ID = " + waypoints.get(i).getId() + " address = " + waypoints.get(i).getAddress());
        }

        //Iterate through the various addresses and calculate the distance from the starting point
        ArrayList<Integer> distances = new ArrayList<>();
        //for (int i = 0; i < addresses.size(); i++) {
        for (int i = 0; i < 1; i++) {
            int index = i;
            //Send requests to calculate the distance
            sendRequest(context, buildDistanceMatrixRequest(context, origin, waypoints.get(i)), response -> {
                Log.i(TAG, "Distance Maxtrix Response: " + response.toString());
                //Log.i(TAG, "Successfully received response from Distance Matrix API");
                //distances.add(index, parseDistance(response));
                //TODO: check if the distance is close enough

            }, error -> {
                routeBuilderCallback.onError();
                Log.e(TAG, "Error making Distance Matrix API request " + error.getMessage());
            });
        }
    }

    /**
     * Creates and adds various requests to the Request Queue
     * @param context Application context
     * @param request HTTP URL to add to the Request Queue
     * @param successListener Listener for success
     * @param errorListener Listener for errors
     */
    private void sendRequest(Context context,
                             String request,
                             Response.Listener<JSONObject> successListener,
                             Response.ErrorListener errorListener) {
        JsonObjectRequest placesSearchRequest = new JsonObjectRequest(Request.Method.GET,
                request, null, successListener, errorListener);
        volleyController.addToRequestQueue(context, placesSearchRequest);
    }

    /**
     * Parses the JSON response from the Places Search request
     * @param object Response from the Places Search API
     * @return ArrayList containing the Places returned
     */
    private ArrayList<Waypoint> parseSearchResponse(JSONObject object) {
        ArrayList<Waypoint> waypointList = new ArrayList<>();
        try {
            JSONArray array = object.getJSONArray("results");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                waypointList.add(new Waypoint(obj.getString("place_id"), obj.getString("vicinity")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return waypointList;
    }

    /**
     * Builds the Places API Search request which is an HTTP URL
     * @param context
     * @param place
     * @param distance
     * @return String representing the request
     */
    private String buildPlacesSearchRequest(Context context, Place place, int distance) {
        StringBuilder builder = new StringBuilder();

/*        if (place.getName() != null && place.getLatLng() != null) {
            builder.append("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            builder.append("&location=");
            builder.append(place.getLatLng().latitude);
            builder.append(",");
            builder.append(place.getLatLng().longitude);
            builder.append("&radius=");
            builder.append(calculateRadius(distance));
            builder.append("&key=");
            builder.append(context.getString(R.string.places_api_key));
        } else if (place.getName() == null) {
            Log.i(TAG, "Name is null");
        } else if (place.getLatLng() == null) {
            Log.i(TAG, "Lat Long is null");
        }
        Log.i(TAG, "HTTP Request: " + builder.toString());

        return builder.toString();*/

        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=21.424723,-157.7467421&radius=50000&key=AIzaSyD3HGrj_jZmd_OYlOaqfNiG0JyC61Gs9Fs";
    }

    private String buildDistanceMatrixRequest(Context context, Place origin, Waypoint destination) {
        StringBuilder builder = new StringBuilder();

        //if (origin.getId() != null) {
            builder.append("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=place_id:");
            //builder.append(origin.getId());
            //Place ID for 17 Aikahi Loop
            builder.append("ChIJU7o1XzgVAHwRkAyT6IkAvFk");
            builder.append("&destinations=place_id:");
            builder.append(destination.getId());
            builder.append("&mode=");
            builder.append("bicycling");
            builder.append("&key=");
            builder.append(context.getString(R.string.places_api_key));
        //}
        return builder.toString();
    }

    /**
     * Calculates the radius to use for the NearbySearch query. The max radius is 50000m (~31 miles)
     * Dividing the distance by 2 and using that for the radius will ensure that no out-and-back route
     *     will be longer than what the user wants to bike.
     * @param distance Distance that the user selected
     * @return Search radius in meters
     */
    private int calculateRadius(int distance) {
        //Since the max radius is 31 miles, cap the distance at 60 miles
        int cappedDistance = distance % 60;
        return (cappedDistance/2) * 1600;
    }
}
