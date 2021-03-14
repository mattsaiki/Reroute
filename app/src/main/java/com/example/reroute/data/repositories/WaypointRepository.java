package com.example.reroute.data.repositories;


import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.reroute.data.models.Waypoint;
import com.example.reroute.utils.Util;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * This class accesses the Google Maps API
 */
public class WaypointRepository {

    private final static String TAG = "[ROUTE]";

    //Singleton class
    private static WaypointRepository instance;
    private static VolleyController volleyController;

    private ArrayList<Waypoint> waypointsWithoutDistance = new ArrayList<>();
    private ArrayList<Waypoint> waypointsWithDistance = new ArrayList<>();

    private WaypointRepositoryCallback responseCallback;

    /**
     * Setup a single instance of this class
     */
    public static WaypointRepository getInstance(Context context) {
        if (instance == null) {
            instance = new WaypointRepository();
            volleyController = VolleyController.getInstance(context);
        }
        return instance;
    }

    /**
     * Initializes the callback to be used in case of errors
     * @param callback Callback used for request errors
     */
    public void initCallback(WaypointRepositoryCallback callback) {
        responseCallback = callback;
    }

    /**
     * Get a list of nearby places within a certain distance of the origin.
     * These places will be used as waypoints in the route.
     * @param origin Route starting point
     * @param distance Desired route distance
     * @return List of waypoints parsed from the API response. Each waypoint has a Place ID and address
     */
    public MutableLiveData<List<Waypoint>> getWaypoints(Place origin, int distance) {
        MutableLiveData<List<Waypoint>> data = new MutableLiveData<>();

        String placesRequestString = Util.buildPlacesSearchRequest(origin, distance);
        JsonObjectRequest placesSearchRequest = new JsonObjectRequest(
                Request.Method.GET,
                placesRequestString,
                null,
                response -> {
                    //Parse the response
                    waypointsWithoutDistance = Util.parseSearchResponse(response);
                    data.setValue(waypointsWithoutDistance);
                }, error -> responseCallback.onError());
        volleyController.addToRequestQueue(placesSearchRequest);

        return data;
    }

    /**
     * Get the distance from the origin to each of the nearby places.
     * @param origin Route starting point
     * @param travelMode Travel mode for the route
     * @return List of waypoints. Each waypoint has a Place ID, address and distance from the origin
     */
    public MutableLiveData<List<Waypoint>> getWaypointDistances(Place origin, String travelMode) {
        MutableLiveData<List<Waypoint>> data = new MutableLiveData<>();

        //Iterate through the various addresses and calculate the distance from the starting point
        for (int i = 0; i < waypointsWithoutDistance.size(); i++) {
            Log.i(TAG, "getting distance i = " + i);
            int index = i;

            Waypoint waypointWithoutDistance = waypointsWithoutDistance.get(i);
            String distanceRequestString = Util.buildDistanceMatrixRequest(origin, waypointWithoutDistance, travelMode);
            JsonObjectRequest distanceRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    distanceRequestString,
                    null,
                    response -> {
                        Log.i(TAG, "distance response index = " + index);
                        double distanceToWaypoint = Util.parseDistanceResponse(response);
                        Waypoint waypoint = new Waypoint(waypointWithoutDistance.getId(),
                                waypointWithoutDistance.getAddress(), distanceToWaypoint);
                        waypointsWithDistance.add(waypoint);
                        data.setValue(waypointsWithDistance);
                    }, error -> responseCallback.onError());
            volleyController.addToRequestQueue(distanceRequest);
        }

        return data;
    }

    /**
     * Get the directions from the origin, to the waypoint, back to the origin
     * @param origin Route starting and ending location
     * @param waypoint Waypoint in the route
     * @param travelMode Travel mode for the route
     * @return Encoded polyline that represents the route
     */
    public MutableLiveData<String> getWaypointDirections(Place origin, Waypoint waypoint, String travelMode) {
        MutableLiveData<String> data = new MutableLiveData<>();
        String directionsRequestString = Util.buildDirectionsRequest(origin, waypoint, travelMode);
        JsonObjectRequest directionsRequest = new JsonObjectRequest(
                Request.Method.GET,
                directionsRequestString,
                null,
                response -> data.setValue(Util.parseDirectionsResponse(response)),
                error -> responseCallback.onError());
        volleyController.addToRequestQueue(directionsRequest);
        return data;
    }
}
