package com.example.reroute.route.display;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.reroute.R;
import com.example.reroute.route.BaseActivity;
import com.example.reroute.route.generate.Waypoint;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;

public class DisplayRouteActivity extends BaseActivity implements OnMapReadyCallback {

    private final static String TAG = "[PLACE]";
    private final static String EXTRA_ORIGIN = "EXTRA_ORIGIN";
    private final static String EXTRA_WAYPOINT = "EXTRA_WAYPOINT";

    private Place origin;
    private Waypoint waypoint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Implement the OnMapReadyCallback interface to setup the map when it's available
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        origin = intent.getParcelableExtra(EXTRA_ORIGIN);
        waypoint = intent.getParcelableExtra(EXTRA_WAYPOINT);
        Log.i(TAG, "Origin " + origin.toString());
        Log.i(TAG, "Waypoint " + waypoint.toString());
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_display;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng originLocation = new LatLng(origin.getLatLng().latitude, origin.getLatLng().longitude);
        googleMap.addMarker(new MarkerOptions().position(originLocation));
    }
}
