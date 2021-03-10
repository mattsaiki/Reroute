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

public class WaypointRepository {

    private final static String TAG = "[ROUTE]";

    //Singleton class
    private static WaypointRepository instance;

    private ArrayList<Waypoint> waypointsWithoutDistance = new ArrayList<>();
    private ArrayList<Waypoint> waypointsWithDistance = new ArrayList<>();
    private String directions;
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

        String placesRequestString = Util.buildPlacesSearchRequest(origin, distance);
        JsonObjectRequest placesSearchRequest = new JsonObjectRequest(
                Request.Method.GET,
                placesRequestString,
                null,
                response -> {
                    //Parse the response
                    waypointsWithoutDistance = Util.parseSearchResponse(response);
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
            String distanceRequestString = Util.buildDistanceMatrixRequest(context, origin, waypointsWithoutDistance.get(i));
            JsonObjectRequest distanceRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    distanceRequestString,
                    null,
                    response -> {
                        double distanceToWaypoint = Util.parseDistanceResponse(response);
                        distances.add(index, distanceToWaypoint);
                        Log.i(TAG, "Distance " + distanceToWaypoint);
                        if (index == waypointsWithoutDistance.size() - 1) {
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
            });
            volleyController.addToRequestQueue(context, distanceRequest);
        }

        return data;
    }

    public MutableLiveData<String> getWaypointDirections(Context context, Place origin, Waypoint waypoint) {
        MutableLiveData<String> data = new MutableLiveData<>();
        String directionsRequestString = Util.buildDirectionsRequest(context, origin, waypoint);
        JsonObjectRequest directionsRequest = new JsonObjectRequest(
                Request.Method.GET,
                directionsRequestString,
                null,
                response -> {
                    data.setValue(Util.parseDirectionsResponse(response));
                }, error -> {
            //TODO: Handle the error
        });
        volleyController.addToRequestQueue(context, directionsRequest);
        return data;
    }
}
