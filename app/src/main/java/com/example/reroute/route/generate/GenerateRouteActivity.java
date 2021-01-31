package com.example.reroute.route.generate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reroute.R;
import com.google.android.libraries.places.api.model.Place;

/**
 * This activity builds the random route. It displays a progress bar and waits until the route has
 * been created.
 */
public class GenerateRouteActivity extends AppCompatActivity {

    private final static String TAG = "[PLACE]";
    private final static String EXTRA_PLACE = "EXTRA_PLACE";
    private final static String EXTRA_DISTANCE = "EXTRA_DISTANCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        Intent intent = getIntent();
        Place placeSelected = intent.getParcelableExtra(EXTRA_PLACE);
        int distance = intent.getIntExtra(EXTRA_DISTANCE, 0);
        if (placeSelected != null && distance != 0) {
            Log.i(TAG, "Received extras: Distance = " + distance +
                    " Place = " + placeSelected.toString());

            RouteBuilder builder = RouteBuilder.getInstance();
            builder.generateRoute(this.getApplicationContext(), placeSelected, distance);
        }
    }
}
