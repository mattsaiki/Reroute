package com.example.reroute.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.reroute.R;
import com.example.reroute.utils.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;

/**
 * This activity displays the random route on a map
 */
public class DisplayRouteActivity extends BaseActivity implements OnMapReadyCallback {

    private final static String TAG = "[ROUTE]";
    private final static String EXTRA_ORIGIN = "EXTRA_ORIGIN";
    private final static String EXTRA_POLYLINE = "EXTRA_POLYLINE";
    private final static int ZOOM_LEVEL_CITY = 10;

    private Place origin;
    private String polyline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Implement the OnMapReadyCallback interface to setup the map when it's available
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        origin = intent.getParcelableExtra(EXTRA_ORIGIN);
        polyline = intent.getStringExtra(EXTRA_POLYLINE);
        Log.i(TAG, "Polyline received: " + polyline);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_display;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (origin.getLatLng() != null) {
            //When the map is ready, move the camera to the route and zoom
            LatLng originLocation = new LatLng(origin.getLatLng().latitude, origin.getLatLng().longitude);
            googleMap.addMarker(new MarkerOptions().position(originLocation));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, ZOOM_LEVEL_CITY));
        } else {
            setErrorState(getString(R.string.label_generalError));
        }
        //Add the route line to the map
        googleMap.addPolyline(new PolylineOptions().color(getResources().getColor(R.color.blue)).addAll(Util.decodePolyline(polyline)));
    }
}
