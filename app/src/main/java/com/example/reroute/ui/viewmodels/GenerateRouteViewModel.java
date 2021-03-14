package com.example.reroute.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.reroute.data.models.Waypoint;
import com.example.reroute.data.repositories.WaypointRepository;
import com.example.reroute.data.repositories.WaypointRepositoryCallback;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

/**
 * This class requests nearby places, distances, directions and exposes the various data to be observed by the View.
 */
public class GenerateRouteViewModel extends ViewModel {

    private MutableLiveData<List<Waypoint>> mWaypoints;
    private MutableLiveData<List<Waypoint>> mWaypointsWithDistances;
    private MutableLiveData<String> mDirections;

    /**
     * Initializes the callback to be used in case of an error
     * @param context Application context
     * @param callback Error callback
     */
    public void init(Context context, WaypointRepositoryCallback callback) {
        WaypointRepository.getInstance(context).initCallback(callback);
    }

    /**
     * Requests a list of places within a certain distance from an origin
     * @param context Application context
     * @param origin Route starting place
     * @param distance Desired route distance
     */
    public void requestNearbyPlaces(Context context, Place origin, int distance) {
        mWaypoints = WaypointRepository.getInstance(context).getWaypoints(origin, distance);
    }

    /**
     * Requests distances for each of the places in the list of nearby places
     * @param context Application context
     * @param origin Route starting place
     * @param travelMode Travel mode for the route
     */
    public void requestDistances(Context context, Place origin, String travelMode) {
        mWaypointsWithDistances = WaypointRepository.getInstance(context).getWaypointDistances(origin, travelMode);
    }

    /**
     * Requests directions starting and ending at the origin that pass through the waypoint
     * @param context Application context
     * @param origin Start/end of the route
     * @param waypoint Waypoint to pass through in the route
     * @param travelMode Travel mode for the route
     */
    public void requestDirections(Context context, Place origin, Waypoint waypoint, String travelMode) {
        mDirections = WaypointRepository.getInstance(context).getWaypointDirections(origin, waypoint, travelMode);
    }

    /**
     * @return Random waypoint from the list of nearby places
     */
    public Waypoint getWaypoint() {
        Waypoint randomWaypoint = null;
        if (mWaypointsWithDistances.getValue() != null) {
            int numWaypoints = mWaypointsWithDistances.getValue().size();
            int randomIndex = (int) (Math.random() * (numWaypoints));
            randomWaypoint = mWaypointsWithDistances.getValue().get(randomIndex);
        }
        return randomWaypoint;
    }

    /**
     * @return LiveData object for the list of nearby places
     */
    public LiveData<List<Waypoint>> getWaypointList() {
        return mWaypoints;
    }

    /**
     * @return LiveData object for the list of nearby places with distances
     */
    public LiveData<List<Waypoint>> getWaypointsWithDistance() {
        return mWaypointsWithDistances;
    }

    /**
     * @return LiveData object for the route directions
     */
    public LiveData<String> getDirections() {
        return mDirections;
    }
}
