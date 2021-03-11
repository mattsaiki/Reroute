package com.example.reroute.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.reroute.data.models.Waypoint;
import com.example.reroute.data.repositories.WaypointRepository;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public class GenerateRouteViewModel extends ViewModel {

    private MutableLiveData<List<Waypoint>> mWaypoints;
    private MutableLiveData<List<Waypoint>> mWaypointsWithDistances;
    private MutableLiveData<String> mDirections;

    public void requestNearbyPlaces(Context context, Place origin, int distance) {
        mWaypoints = WaypointRepository.getInstance(context).getWaypoints(origin, distance);
    }

    public void requestDistances(Context context, Place origin) {
        mWaypointsWithDistances = WaypointRepository.getInstance(context).getWaypointDistances(origin);
    }

    public void requestDiretions(Context context, Place origin, Waypoint waypoint) {
        mDirections = WaypointRepository.getInstance(context).getWaypointDirections(origin, waypoint);
    }

    public Waypoint getWaypoint() {
        //TODO: choose the right waypoint
        return mWaypointsWithDistances.getValue().get(0);
    }

    public LiveData<List<Waypoint>> getWaypointList() {
        return mWaypoints;
    }

    public LiveData<List<Waypoint>> getWaypointsWithDistance() {
        return mWaypointsWithDistances;
    }

    public LiveData<String> getDiretions() {
        return mDirections;
    }
}
