package com.example.reroute;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import com.example.reroute.models.Waypoint;
import com.example.reroute.route.generate.RouteBuilderCallback;
import com.example.reroute.viewmodels.GenerateRouteViewModel;
import com.google.android.libraries.places.api.model.Place;

/**
 * This activity builds the random route. It displays a progress bar and waits until the route has
 * been created.
 */
public class GenerateRouteActivity extends BaseActivity implements RouteBuilderCallback {

    private final static String TAG = "[PLACE]";
    private final static String EXTRA_ORIGIN = "EXTRA_ORIGIN";
    private final static String EXTRA_DISTANCE = "EXTRA_DISTANCE";
    private final static String EXTRA_POLYLINE = "EXTRA_POLYLINE";

    private ProgressBar progressBar;
    private TextView progressMessage;
    private Place origin;

    private GenerateRouteViewModel mGenerateRouteViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView progressMessage = findViewById(R.id.progressText);

        Intent intent = getIntent();
        origin = intent.getParcelableExtra(EXTRA_ORIGIN);
        int distance = intent.getIntExtra(EXTRA_DISTANCE, 0);
        if (origin != null && distance != 0) {
            Log.i(TAG, "Received extras: Distance = " + distance +
                    " Place = " + origin.toString());
            mGenerateRouteViewModel = ViewModelProviders.of(this).get(GenerateRouteViewModel.class);
            //Get a list of nearby places
            mGenerateRouteViewModel.requestNearbyPlaces(this.getApplicationContext(), origin, distance);
            mGenerateRouteViewModel.getWaypointList().observe(this, waypointList -> {
                Log.i(TAG, "Waypoint list updated");
                //When the nearby places are fetched, get the distances to each of the places
                mGenerateRouteViewModel.requestDistances(this.getApplicationContext(), origin);

                mGenerateRouteViewModel.getWaypointsWithDistance().observe(this, listWithDistances -> {
                    Log.i(TAG, "Waypoint list with distances updated");
                    //When the distances to the nearby places are fetched, choose one of the waypoints
                    Waypoint chosenWaypoint = mGenerateRouteViewModel.getWaypoint();
                    Log.i(TAG, "Final Waypoint! " + chosenWaypoint.getId() + " " + chosenWaypoint.getAddress());
                } );
            });

            //RouteBuilder builder = RouteBuilder.getInstance();
            //builder.generateRoute(this.getApplicationContext(), this, origin, distance);
        } else {
            progressBar.setVisibility(View.GONE);
            progressMessage.setVisibility(View.GONE);
            setErrorState(getString(R.string.label_generalError));
        }
        //RouteBuilder builder = RouteBuilder.getInstance();
        //builder.generateRoute(this.getApplicationContext(), this, null, 20);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_generate;
    }

    @Override
    public void onSuccess(String polyline) {
        Log.i(TAG, "Successfully found a route");
        Intent intent = new Intent(this, DisplayRouteActivity.class);
        intent.putExtra(EXTRA_ORIGIN, origin);
        intent.putExtra(EXTRA_POLYLINE, polyline);
        startActivity(intent);
    }

    @Override
    public void onError() {
        progressBar.setVisibility(View.GONE);
        progressMessage.setVisibility(View.GONE);
        setErrorState(getString(R.string.label_generalError));
        Log.e(TAG, "Something went wrong while generating a route");
    }
}