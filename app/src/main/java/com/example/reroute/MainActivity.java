package com.example.reroute;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "[Place]";

    private EditText distanceEditText;
    private Button goButton;
    private int routeDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        distanceEditText = findViewById(R.id.routeLength_editText);
        goButton = findViewById(R.id.go_button);

        initializePlaces();
        initializeDistanceEditText();
        initializeGoButton();
    }

    /**
     * Initialize Google Maps Places API
     * Initialize Autocomplete for searching for the starting location
     */
    private void initializePlaces() {
        String placesApiKey = getString(R.string.places_api_key);
        //Initialize Places
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), placesApiKey);
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                distanceEditText.setVisibility(View.VISIBLE);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred selecting a Place: " + status);
            }
        });
    }

    /**
     * Initialize the edit text for entering route distance
     */
    private void initializeDistanceEditText() {
        distanceEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean donePressed = false;
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    Log.i(TAG, "Distance entered, checking for valid distance");
                    donePressed = true;
                    boolean validDistance = verifyDistanceEntered(distanceEditText.getText().toString());
                    if (validDistance) {
                        //Show the Generate Route button when a valid distance is entered
                        goButton.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(MainActivity.this, R.string.toast_invalidDistance, Toast.LENGTH_SHORT).show();
                    }
                }
                return donePressed;
            }
        });
    }

    /**
     * Checks if the user entered a valid distance
     * @param distanceEntered a string that the user entered in the distance field
     * @return True if valid distance, false otherwise
     */
    private boolean verifyDistanceEntered(String distanceEntered) {
        boolean validInput = false;
        int distance = Integer.parseInt(distanceEntered);
        //Verify that the distance is a realistic distance
        if (0 < distance && 200 > distance) {
            validInput = true;
            routeDistance = distance;
            Log.i(TAG, "Valid distance was entered: " + distanceEntered);
        } else {
            Log.i(TAG, "Invalid distance was entered: " + distanceEntered);
        }
        return validInput;
    }

    /**
     * Initialize the button to generate the route
     */
    private void initializeGoButton() {
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Generate route!
            }
        });
    }
}
