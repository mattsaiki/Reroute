package com.example.reroute.route.collect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.reroute.R;
import com.example.reroute.route.generate.GenerateRouteActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;

/**
 * This activity is collects the information necessary to generate a random route.
 * After all of the information is collected, start the next activity
 */
public class CollectRouteInfoActivity extends AppCompatActivity {

    private final static String TAG = "[PLACE]";
    private final static String EXTRA_PLACE = "EXTRA_PLACE";
    private final static String EXTRA_DISTANCE = "EXTRA_DISTANCE";
    private final static int MIN_DISTANCE = 0;
    private final static int MAX_DISTANCE = 200;

    private enum ErrorState {
        NONE,
        INVALID_DISTANCE,
        GENERAL_ERROR
    }

    private EditText distanceEditText;
    private Button goButton;
    private int routeDistance;
    private Place placeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        distanceEditText = findViewById(R.id.routeLength_editText);
        goButton = findViewById(R.id.go_button);

        initializePlaces();
        initializeDistanceEditText();
        initializeGoButton();
        goButton.setVisibility(View.VISIBLE);
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
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    distanceEditText.setVisibility(View.VISIBLE);
                    Log.i(TAG, "Place selected: " + place.getName());

                    //Use the Places client to get the latitude and longitude of the selected place
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                    FetchPlaceRequest request = FetchPlaceRequest.newInstance(place.getId(), placeFields);
                    placesClient.fetchPlace(request).addOnSuccessListener(response -> {
                        placeSelected = response.getPlace();
                        Log.i(TAG, "Full place details fetched: " + placeSelected.toString());
                    }).addOnFailureListener(exception -> {
                        setErrorState(ErrorState.GENERAL_ERROR);
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                    });
                }

                @Override
                public void onError(Status status) {
                    setErrorState(ErrorState.GENERAL_ERROR);
                    Log.i(TAG, "An error occurred selecting a Place: " + status);
                }
            });
        } else {
            Log.e(TAG, "Unable to initialize " + AutocompleteSupportFragment.class);
        }
    }

    /**
     * Initialize the edit text for entering route distance
     */
    private void initializeDistanceEditText() {
        distanceEditText.setOnKeyListener((v, keyCode, event) -> {
            boolean donePressed = false;
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                Log.i(TAG, "Distance entered, checking for valid distance");
                donePressed = true;
                boolean validDistance = verifyDistanceEntered(distanceEditText.getText().toString());
                if (validDistance) {
                    //Show the Generate Route button when a valid distance is entered
                    goButton.setVisibility(View.VISIBLE);
                    setErrorState(ErrorState.NONE);
                } else {
                    setErrorState(ErrorState.INVALID_DISTANCE);
                }
            }
            return donePressed;
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
        if (MIN_DISTANCE < distance && MAX_DISTANCE > distance) {
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
        goButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GenerateRouteActivity.class);
/*            intent.putExtra(EXTRA_PLACE, placeSelected);
            intent.putExtra(EXTRA_DISTANCE, routeDistance);*/
            startActivity(intent);
        });
    }



    /**
     * Sets the error icon and message according to the error state
     * @param error Current error state, NONE if no error
     */
    private void setErrorState(ErrorState error) {
        ImageView errorIcon = findViewById(R.id.error_icon);
        TextView errorText = findViewById(R.id.error_message);

        switch (error) {
            case NONE:
                errorIcon.setVisibility(View.GONE);
                errorText.setVisibility(View.GONE);
                break;
            case INVALID_DISTANCE:
                errorIcon.setVisibility(View.VISIBLE);
                errorText.setVisibility(View.VISIBLE);
                errorText.setText(R.string.label_distanceError);
                break;
            case GENERAL_ERROR:
                errorIcon.setVisibility(View.VISIBLE);
                errorText.setVisibility(View.VISIBLE);
                errorText.setText(R.string.label_generalError);
                break;
        }
    }
}
