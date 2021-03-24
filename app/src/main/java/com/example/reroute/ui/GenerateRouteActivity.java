package com.example.reroute.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import com.example.reroute.R;
import com.example.reroute.data.models.Waypoint;
import com.example.reroute.data.repositories.WaypointRepositoryCallback;
import com.example.reroute.ui.viewmodels.GenerateRouteViewModel;
import com.google.android.libraries.places.api.model.Place;

/**
 * This activity builds the random route. It displays a progress bar and waits until the route has
 * been created.
 */
public class GenerateRouteActivity extends BaseActivity implements WaypointRepositoryCallback {

    private final static String TAG = "[ROUTE]";
    private final static String EXTRA_ORIGIN = "EXTRA_ORIGIN";
    private final static String EXTRA_DISTANCE = "EXTRA_DISTANCE";
    private final static String EXTRA_TRAVEL_MODE = "EXTRA_TRAVEL_MODE";
    private final static String EXTRA_POLYLINE = "EXTRA_POLYLINE";

    private ProgressBar progressBar;
    private TextView progressMessage;

    private GenerateRouteViewModel generateRouteViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressBar = findViewById(R.id.progressBar);
        progressMessage = findViewById(R.id.progressText);

        Intent intent = getIntent();
        Place origin = intent.getParcelableExtra(EXTRA_ORIGIN);
        int desiredDistance = intent.getIntExtra(EXTRA_DISTANCE, 0);
        String travelMode = intent.getStringExtra(EXTRA_TRAVEL_MODE);
        if (origin != null && desiredDistance != 0 && travelMode != null) {
            Log.i(TAG, "Received extras: Distance = " + desiredDistance +
                    " Place = " + origin.toString());
            generateRandomRoute(origin, desiredDistance, travelMode);
        } else {
            progressBar.setVisibility(View.GONE);
            progressMessage.setVisibility(View.GONE);
            setErrorState(getString(R.string.label_generalError));
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_generate;
    }

    @Override
    public void onError() {
        progressBar.setVisibility(View.GONE);
        progressMessage.setVisibility(View.GONE);
        setErrorState(getString(R.string.label_generalError));
        Log.e(TAG, "Something went wrong while generating a route");
    }

    private void generateRandomRoute(Place origin, int distance, String travelMode) {
        generateRouteViewModel = ViewModelProviders.of(this).get(GenerateRouteViewModel.class);
        generateRouteViewModel.init(this.getApplicationContext(), this);
        //Request a list of nearby places
        generateRouteViewModel.requestNearbyPlaces(this.getApplicationContext(), origin, distance);
        generateRouteViewModel.getWaypointList().observe(this, waypointList -> {

            //When the nearby places are fetched, get the distances to each of the places.
            generateRouteViewModel.requestDistances(this.getApplicationContext(), origin, travelMode);
            generateRouteViewModel.getWaypointsWithDistance().observe(this, listWithDistances -> {
                Log.i(TAG, "Got a distance");
                //When the all of the distances to the nearby places are fetched, choose one of the waypoints
                if (listWithDistances.size() == waypointList.size()) {
                    Waypoint chosenWaypoint = generateRouteViewModel.getWaypoint();
                    Log.i(TAG, "Final Waypoint: " + chosenWaypoint.getId() + " " + chosenWaypoint.getAddress());

                    //When the final waypoint is chosen, get the directions from origin -> waypoint -> origin
                    generateRouteViewModel.requestDirections(this.getApplicationContext(), origin, chosenWaypoint, travelMode);
                    generateRouteViewModel.getDirections().observe(this, directions -> {
                        //Move to the next activity after the directions have been fetched
                        Log.i(TAG, "Directions are updated");
                        Intent displayRouteIntent = new Intent(this, DisplayRouteActivity.class);
                        displayRouteIntent.putExtra(EXTRA_ORIGIN, origin);
                        displayRouteIntent.putExtra(EXTRA_POLYLINE, directions);
                        displayRouteIntent.putExtra(EXTRA_DISTANCE, chosenWaypoint.getDistance());
                        startActivity(displayRouteIntent);
                    });
                } else {
                    Log.i(TAG, "withoutDistanceSize = " + waypointList.size() + " withDistanceSize = " +listWithDistances.size());
                }
            } );
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GenerateRouteActivity.this,
                MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
