package com.example.reroute.ui;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.reroute.BuildConfig;
import com.example.reroute.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;

/**
 * This activity collects the starting location and displays it on a map
 */
public class MainActivity extends BaseActivity implements OnMapReadyCallback{

    private final static String TAG = "[ROUTE]";
    private final static String EXTRA_ORIGIN = "EXTRA_ORIGIN";
    private final static int ZOOM_LEVEL_STREETS = 15;

    private FloatingActionButton nextButton;
    private Place originSelected;
    private GoogleMap map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nextButton = findViewById(R.id.fab);

        initializePlaces();

        //Implement the OnMapReadyCallback interface to setup the map when it's available
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    /**
     * Initialize Google Maps Places API
     * Initialize Autocomplete for searching for the starting location
     */
    private void initializePlaces() {
        //Initialize Places
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_MAPS_API_KEY);
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
            autocompleteFragment.setHint(getString(R.string.hint_routeOrigin));
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    Log.i(TAG, "Place selected: " + place.getName());

                    //Use the Places client to get the latitude and longitude of the selected place
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                    FetchPlaceRequest request = FetchPlaceRequest.newInstance(place.getId(), placeFields);
                    placesClient.fetchPlace(request).addOnSuccessListener(response -> {
                        originSelected = response.getPlace();
                        Log.i(TAG, "Full place details fetched: " + originSelected.toString());
                        if (originSelected.getLatLng() != null) {
                            LatLng originLocation = new LatLng(originSelected.getLatLng().latitude, originSelected.getLatLng().longitude);
                            map.addMarker(new MarkerOptions().position(originLocation));
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, ZOOM_LEVEL_STREETS));
                            nextButton.setVisibility(View.VISIBLE);
                        } else {
                            setErrorState(getString(R.string.label_generalError));
                        }
                    }).addOnFailureListener(exception -> {
                        setErrorState(getString(R.string.label_generalError));
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                    });
                }

                @Override
                public void onError(Status status) {
                    if (!status.isCanceled()) {
                        Log.i(TAG, "An error occurred selecting a Place: " + status);
                        setErrorState(getString(R.string.label_generalError));
                    }
                }
            });
        } else {
            Log.e(TAG, "Unable to initialize " + AutocompleteSupportFragment.class);
        }
    }

    /**
     * Go to the next activity when the Next button is clicked
     */
    public void onNextClicked(View view) {
        Intent intent = new Intent(this, CollectRouteInfoActivity.class);
        intent.putExtra(EXTRA_ORIGIN, originSelected);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
}
