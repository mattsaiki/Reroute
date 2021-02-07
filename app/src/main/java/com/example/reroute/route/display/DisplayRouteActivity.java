package com.example.reroute.route.display;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.reroute.R;
import com.example.reroute.route.BaseActivity;
import com.example.reroute.route.generate.Waypoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.List;

public class DisplayRouteActivity extends BaseActivity implements OnMapReadyCallback {

    private final static String TAG = "[PLACE]";
    private final static String EXTRA_ORIGIN = "EXTRA_ORIGIN";
    private final static String EXTRA_POLYLINE = "EXTRA_POLYLINE";

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
        //origin = intent.getParcelableExtra(EXTRA_ORIGIN);
        polyline = intent.getStringExtra(EXTRA_POLYLINE);
        Log.i(TAG, "Polyline received: " + polyline);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_display;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //21.424723,-157.7467421)
        LatLng originLocation = new LatLng(21.424723,-157.7467421);
        googleMap.addMarker(new MarkerOptions().position(originLocation));
        //LatLngBounds bounds = new LatLngBounds.Builder().include(originLocation).build();
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));

/*        if (origin.getLatLng() != null) {
            LatLng originLocation = new LatLng(origin.getLatLng().latitude, origin.getLatLng().longitude);
            googleMap.addMarker(new MarkerOptions().position(originLocation));
        } else {
            setErrorState(getString(R.string.label_generalError));
        }*/

        googleMap.addPolyline(new PolylineOptions().addAll(decodePoly(polyline)));
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
