package com.example.reroute.repositories;


import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.reroute.models.Waypoint;
import com.google.android.libraries.places.api.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WaypointRepository {

    private final static String TAG = "[PLACE]";

    //Singleton class
    private static WaypointRepository instance;
    private ArrayList<Waypoint> waypointsWithoutDistance = new ArrayList<>();
    private ArrayList<Waypoint> waypointsWithDistance = new ArrayList<>();
    private VolleyController volleyController;

    public static WaypointRepository getInstance() {
        if (instance == null) {
            instance = new WaypointRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Waypoint>> getWaypoints(Context context, Place origin, int distance) {
        volleyController = VolleyController.getInstance(context);
        MutableLiveData<List<Waypoint>> data = new MutableLiveData<>();

        String placesRequestString = buildPlacesSearchRequest(origin, distance);
        JsonObjectRequest placesSearchRequest = new JsonObjectRequest(
                Request.Method.GET,
                placesRequestString,
                null,
                response -> {
                    //Parse the response
                    waypointsWithoutDistance = parseSearchResponse(response);
                    data.setValue(waypointsWithoutDistance);
                }, error -> {
                    //TODO: Handle the error
        });
        volleyController.addToRequestQueue(context, placesSearchRequest);

        return data;
    }

    public MutableLiveData<List<Waypoint>> getWaypointDistances(Context context, Place origin) {
        MutableLiveData<List<Waypoint>> data = new MutableLiveData<>();

        ArrayList<Double> distances = new ArrayList<>();
        //Iterate through the various addresses and calculate the distance from the starting point
        for (int i = 0; i < waypointsWithoutDistance.size(); i++) {
        //for (int i = 0; i < 1; i++) {
            int index = i;
            String distanceRequestString = buildDistanceMatrixRequest(context, origin, waypointsWithoutDistance.get(i));
            JsonObjectRequest distanceRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    distanceRequestString,
                    null,
                    response -> {
                        double distanceToWaypoint = parseDistanceResponse(response);
                        distances.add(index, distanceToWaypoint);
                        Log.i(TAG, "Distance " + distanceToWaypoint);
                        if (index == waypointsWithoutDistance.size() -  1) {
                            for (int ii = 0; ii < waypointsWithoutDistance.size(); ii++) {
                                Waypoint waypoint = new Waypoint(waypointsWithoutDistance.get(ii).getId(),
                                        waypointsWithoutDistance.get(ii).getAddress(),
                                        distances.get(ii));
                                waypointsWithDistance.add(ii, waypoint);
                            }
                            data.setValue(waypointsWithDistance);
                        }
                    }, error -> {
                        //TODO: Handle the error
                    }
            );
            volleyController.addToRequestQueue(context, distanceRequest);
        }

        return data;
    }

    /**
     * Builds the Places Search API request
     * @param place Route starting point
     * @param distance Desired route length
     * @return String representing the request in the form of an HTTP URL
     */
    private String buildPlacesSearchRequest(Place place, int distance) {
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
            //TODO: Hide this API key
            builder.append("AIzaSyD3HGrj_jZmd_OYlOaqfNiG0JyC61Gs9Fs");
        } else if (place.getName() == null) {
            Log.i(TAG, "Name is null");
        } else if (place.getLatLng() == null) {
            Log.i(TAG, "Lat Long is null");
        }
        Log.i(TAG, "HTTP Request: " + builder.toString());

        return builder.toString();*/

        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=21.424723,-157.7467421&radius=50000&key=AIzaSyD3HGrj_jZmd_OYlOaqfNiG0JyC61Gs9Fs";
    }

    /**
     * Builds the Distance Matrix API request
     * @param context Application context
     * @param origin Route starting point
     * @param destination Potential destination for the route
     * @return String representing the request in the form of an HTTP URL
     */
    private String buildDistanceMatrixRequest(Context context, Place origin, Waypoint destination) {
        StringBuilder builder = new StringBuilder();

/*        if (origin.getId() != null) {
            builder.append("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=place_id:");
            builder.append(origin.getId());
            builder.append("&destinations=place_id:");
            builder.append(destination.getId());
            builder.append("&mode=");
            builder.append("bicycling");
            builder.append("&key=");
            //TODO: Hide this API key
            builder.append("AIzaSyD3HGrj_jZmd_OYlOaqfNiG0JyC61Gs9Fs");
        }
        return builder.toString();*/
        return "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=place_id:ChIJU7o1XzgVAHwRkAyT6IkAvFk&destinations=place_id:ChIJTUbDjDsYAHwRbJen81_1KEs&mode=bicycling&key=AIzaSyD3HGrj_jZmd_OYlOaqfNiG0JyC61Gs9Fs";
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
                Log.d(TAG, "Waypoint parsed " + i + ": id = " + obj.getString("place_id") +
                        ", distance = " + obj.getString("vicinity"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return waypointList;
    }

    /**
     * Parses the JSON response from the Distance Matrix request
     * @param object Response from the Distance Matrix API
     * @return Double containing the distance from origin to destination by bike
     */
    private double parseDistanceResponse(JSONObject object) {
        double distance = 0;
        try {
            JSONArray rowsArray = object.getJSONArray("rows");
            JSONObject rowObject = rowsArray.getJSONObject(0);
            JSONArray elementArray = rowObject.getJSONArray("elements");
            JSONObject elementObject = elementArray.getJSONObject(0);
            JSONObject distanceObject = elementObject.getJSONObject("distance");
            //Distance comes in the form "XX.XX mi", need to remove after the whitespace
            String distanceWithUnits = distanceObject.getString("text");
            int spacePosition = distanceWithUnits.indexOf(" ");
            String distanceWithoutUnits = distanceWithUnits.substring(0, spacePosition);
            distance = Double.parseDouble(distanceWithoutUnits);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Distance parsed = " + distance);

        return distance;
    }
}
