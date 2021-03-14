package com.example.reroute.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.reroute.R;
import com.example.reroute.utils.Util;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;

public class CollectRouteInfoActivity extends BaseActivity {

    private final static String TAG = "[ROUTE]";
    private final static String EXTRA_ORIGIN = "EXTRA_ORIGIN";
    private final static String EXTRA_DISTANCE = "EXTRA_DISTANCE";
    private final static String EXTRA_TRAVEL_MODE = "EXTRA_TRAVEL_MODE";

    private TextView distanceTextView;
    private Slider distanceSlider;
    private FloatingActionButton nextButton;
    private Place origin;
    private int routeDistance = 0;
    private String travelMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        distanceTextView = findViewById(R.id.distance_textView);
        distanceTextView.setText(getString(R.string.label_routeLength, routeDistance));
        distanceSlider = findViewById(R.id.distance_slider);
        nextButton = findViewById(R.id.fab);
        initializerSlider();

        Intent intent = getIntent();
        origin = intent.getParcelableExtra(EXTRA_ORIGIN);
        if (origin != null) {
            Log.i(TAG, "Received extras: Place = " + origin.toString());
        } else {
            setErrorState(getString(R.string.label_generalError));
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_collect;
    }

    private void initializerSlider() {
        distanceSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                routeDistance = (int) distanceSlider.getValue();
                distanceTextView.setText(getString(R.string.label_routeLength, routeDistance));
                checkIfDone();
            }
        });
    }

    /**
     * Show the Next button when the route distance and travel method have been selected
     */
    private void checkIfDone() {
        if (travelMode != null && routeDistance != 0) {
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when the Cycling radio button is selected
     */
    public void onCyclingSelected(View view) {
        travelMode = getString(R.string.string_cyclingTravelMode);
        checkIfDone();
    }

    /**
     * Called when the Running radio button is selected
     */
    public void onRunningSelected(View view) {
        travelMode = getString(R.string.string_walkingTravelMode);
        checkIfDone();
    }

    /**
     * Called when the Next button is clicked, go to the next activity
     */
    public void onNextClicked(View view) {
        Intent intent = new Intent(this, GenerateRouteActivity.class);
        intent.putExtra(EXTRA_ORIGIN, origin);
        intent.putExtra(EXTRA_DISTANCE, routeDistance);
        intent.putExtra(EXTRA_TRAVEL_MODE, travelMode);
        startActivity(intent);
    }
}
