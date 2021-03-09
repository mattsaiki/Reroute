package com.example.reroute.route.generate;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.reroute.R;
import com.example.reroute.models.Waypoint;
import com.example.reroute.repositories.VolleyController;
import com.google.android.libraries.places.api.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class is responsible for building the random route by using the Google Maps Places Search API.
 */
public class RouteBuilder {

    private final static String TAG = "[PLACE]";
    private final static int MAX_DISTANCE_DIFFERENCE = 3;

    private static RouteBuilder instance;
    private RouteBuilderCallback routeBuilderCallback;
    private VolleyController volleyController;
    private int routeDistanceSelected;

    private RouteBuilder() {
    }

    /**
     * Setup a single instance of this class
     */
    public static RouteBuilder getInstance() {
        if (instance == null) {
            instance = new RouteBuilder();
        }
        return instance;
    }

    public void generateRoute(Context context, RouteBuilderCallback callback, Place origin, int distance) {
        Log.i(TAG, "RouteBuilder called with DEBUG parameters, ORIGIN is null and distance is 20!!");
        volleyController = VolleyController.getInstance(context);
        this.routeBuilderCallback = callback;
        routeDistanceSelected = distance;

        routeBuilderCallback.onSuccess("}nwaCf{xa]~BgB~H~FbFnDfMaCtOwChBnG~OrKvF_JvVoPrYmNbi@yW|C{B~DuNxBkH~CyDjEfEjBxBzA{CxEp@`Ch@dBPvLeDvExCbDX@`Cp@~FpAbEbL|L|JlD|i@gHpRcIbGoDlHlOxG{A`MkHrRuLv^c[pI{GxFUzJSzPi[pAqPZaDbG`G`HIhGeC~GmSr]w{A|HmKxPsRLaDpH_MhKkZrMi_@`Wat@hLmZbZm\\lEsNByI|E{JbNsCxL_H`AeJpByDnVaBhOeAxGnNjFhLxGxFnFb@bS{DrH|@nKzPSbBsBhAaAbAqA|DvB|W}GnJmKxA}@B{BBDdCB|@FdAv@rEbI~Z|EbS_@|CYxDz@r@t@fAdFxLjJrQkA|G{HtHrEvEvKnGx@jD}@lBYzABpFp@dI`@|CdFbHd@`FnCzOdG|QtDdT~@jGrAz@kLfRcBnDgBzNvClUdPvb@zDf[fH`o@~I~j@xFzj@`Fxk@MpUaG~q@uDxl@yApb@aB|Rwc@bgAsThi@s@|LiJdOx@jGjAzJkPbZeKlI}BpI_FhRfGlA~@rA[nAkAvEoBbIkOnl@{XzgAgCtKUMcAuBoBhBS`AoDhJqBfA{@|A~ArBoBhBQD[M_@DAzBlCjB|CkErHsPfFsVd]}sAvKsb@`CsJlBqBlK`DzR|Fn@kApA{C|Ak@vBrAxF{GjBaC~A{BdB_ChEoEh@cAlAiMoBkOaCmCk@gCvYs]`F_GG}FuCqQ{CY_QmOgAk@cI{DaA}A^}@`AaCz@wBvGcPvRue@jJwTZeOnDql@rE{{@`E_c@kCub@yH}q@uLcz@iC}]mGi][{GgGwRgHwQ}CcTtBoR~D_HpB_A~EiJyDyD_AuNiKe]uD}VeFcHa@}C[sS|@mBy@kDwKoGsEwEzHuHjA}GkJsQeFyLu@gA{@s@XyDXkADqA}EcS{Jsa@KcCEeCzBC|@C~Da@lEw@fE}Cz@oP}A_MpA}D`AcArBiARcBsFuL{CeCsH}@cSzDoFc@yGyFeOy[ca@dC}FfBi@jHuFrIkP|CwHzFoB~Ja@nLaIvKcXv\\ii@d|AmKxZoEjMaKlM_m@hh@_c@lm@iInbA{AhP}IhOcG|HmD?{LGkZtXqu@|d@eHeM}Bc@cCxAwFbDqGnCil@vIiKwBmMiMi@kAgHlAgKnE}GlCeCsI{EwC_AUiCm@d@uBbAcF_As@yF_GiDtA{@lAaCtIaCnIwA|Cye@vUml@zYyLzK_DhFeI{EyFqEk@_AsC{B}MfCuCd@mFz@kFwDmIeF_CfB");

/*        //Create and send the request for places within a certain radius using Google Maps Places Search
        sendRequest(context, buildPlacesSearchRequest(context, origin, distance), response -> {
            Log.i(TAG, "Successfully received response from Places Search API");
            calculateDistances(response, context, origin);
        }, error -> {
            Log.e(TAG, "Error making Places Search API request " + error.getMessage());
            routeBuilderCallback.onError();
        });*/
    }

    private void calculateDistances(JSONObject addressResponse, Context context, Place origin) {
        //Parse the addresses and Place ID from the Nearby Search response
        ArrayList<Waypoint> waypoints = parseSearchResponse(addressResponse);

        //Iterate through the various addresses and calculate the distance from the starting point
        ArrayList<Double> distances = new ArrayList<>();
        //for (int i = 0; i < addresses.size(); i++) {
        for (int i = 0; i < 1; i++) {
            int index = i;
            //Send requests to calculate the distance
            sendRequest(context, buildDistanceMatrixRequest(context, origin, waypoints.get(i)), response -> {
                Log.i(TAG, "Distance Maxtrix Response: " + response.toString());
                double distanceToWaypoint = parseDistanceResponse(response);
                if (checkWaypoint(distanceToWaypoint)) {
                    getDirections(context, origin, waypoints.get(index));
                } else {
                    //Save the distance in case no good matches
                    distances.add(index, distanceToWaypoint);
                    //TODO: finish this
                }
            }, error -> {
                Log.e(TAG, "Error making Distance Matrix API request " + error.getMessage());
                routeBuilderCallback.onError();
            });
        }
    }

    private void getDirections(Context context, Place origin, Waypoint waypoint) {
        sendRequest(context, buildDirectionsRequest(context, origin, waypoint), response -> {
            String overviewPolyline = parseDirectionsResponse(response);
            Log.i(TAG, "Polyline = " + overviewPolyline);
            routeBuilderCallback.onSuccess(overviewPolyline);
        }, error -> {
            Log.e(TAG, "Error making Directions API request " + error.getMessage());
            routeBuilderCallback.onError();
        });
    }

    /**
     * Creates and adds various requests to the Request Queue
     * @param context Application context
     * @param request HTTP URL to add to the Request Queue
     * @param successListener Listener for success
     * @param errorListener Listener for errors
     */
    private void sendRequest(Context context,
                             String request,
                             Response.Listener<JSONObject> successListener,
                             Response.ErrorListener errorListener) {
        JsonObjectRequest placesSearchRequest = new JsonObjectRequest(Request.Method.GET,
                request, null, successListener, errorListener);
        volleyController.addToRequestQueue(context, placesSearchRequest);
    }

    /**
     * Builds the Places Search API request
     * @param context Application context
     * @param place Route starting point
     * @param distance Desired route length
     * @return String representing the request in the form of an HTTP URL
     */
    private String buildPlacesSearchRequest(Context context, Place place, int distance) {
        StringBuilder builder = new StringBuilder();

/*        if (place.getName() != null && place.getLatLng() != null) {
            builder.append("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            builder.append("&location=");
            builder.append(place.getLatLng().latitude);
            builder.append(",");
            builder.append(place.getLatLng().longitude);
            builder.append("&radius=");
            builder.append(calculateRadius(distance));
            builder.append("&key=");
            builder.append(context.getString(R.string.google_maps_api_key));
        } else if (place.getName() == null) {
            Log.i(TAG, "Name is null");
        } else if (place.getLatLng() == null) {
            Log.i(TAG, "Lat Long is null");
        }
        Log.i(TAG, "HTTP Request: " + builder.toString());

        return builder.toString();*/

        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=21.424723,-157.7467421&radius=50000&key=AIzaSyD3HGrj_jZmd_OYlOaqfNiG0JyC61Gs9Fs";
    }

    /**
     * Builds the Distance Matrix API request
     * @param context Application context
     * @param origin Route starting point
     * @param destination Potential destination for the route
     * @return String representing the request in the form of an HTTP URL
     */
    private String buildDistanceMatrixRequest(Context context, Place origin, Waypoint destination) {
        StringBuilder builder = new StringBuilder();

/*        if (origin.getId() != null) {
            builder.append("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=place_id:");
            builder.append(origin.getId());
            builder.append("&destinations=place_id:");
            builder.append(destination.getId());
            builder.append("&mode=");
            builder.append("bicycling");
            builder.append("&key=");
            builder.append(context.getString(R.string.google_maps_api_key));
        }
        return builder.toString();*/
        return "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=place_id:ChIJU7o1XzgVAHwRkAyT6IkAvFk&destinations=place_id:ChIJTUbDjDsYAHwRbJen81_1KEs&mode=bicycling&key=AIzaSyD3HGrj_jZmd_OYlOaqfNiG0JyC61Gs9Fs";
    }

    private String buildDirectionsRequest(Context context, Place origin, Waypoint waypoint) {
        StringBuilder builder = new StringBuilder();

        if (origin.getId() != null) {
            builder.append("https://maps.googleapis.com/maps/api/directions/json?origin=place_id:");
            builder.append(origin.getId());
            builder.append("&destination=place_id:");
            builder.append(origin.getId());
            builder.append("&waypoints=place_id:");
            builder.append(waypoint.getId());
            builder.append("&mode=");
            builder.append("bicycling");
            builder.append("&key=");
            builder.append(context.getString(R.string.google_maps_api_key));
        }
        Log.i(TAG, "Directions request: " + builder.toString());

        return builder.toString();
    }

    /**
     * Calculates the radius to use for the NearbySearch query. The max radius is 50000m (~31 miles)
     * Dividing the distance by 2 and using that for the radius will ensure that no out-and-back route
     *     will be longer than what the user wants to bike.
     * @param distance Distance that the user selected
     * @return Search radius in meters
     */
    private int calculateRadius(int distance) {
        //Since the max radius is 31 miles, cap the distance at 60 miles
        int cappedDistance = distance % 60;
        return (cappedDistance/2) * 1600;
    }

    /**
     * Parses the JSON response from the Places Search request
     * @param object Response from the Places Search API
     * @return ArrayList containing the Places returned
     */
    private ArrayList<Waypoint> parseSearchResponse(JSONObject object) {
        ArrayList<Waypoint> waypointList = new ArrayList<>();
        try {
            JSONArray array = object.getJSONArray("results");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                waypointList.add(new Waypoint(obj.getString("place_id"), obj.getString("vicinity")));
                Log.d(TAG, "Waypoint parsed " + i + ": id = " + obj.getString("place_id") +
                        ", distance = " + obj.getString("vicinity"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return waypointList;
    }

    /**
     * Parses the JSON response from the Distance Matrix request
     * @param object Response from the Distance Matrix API
     * @return Double containing the distance from origin to destination by bike
     */
    private double parseDistanceResponse(JSONObject object) {
        double distance = 0;
        try {
            JSONArray rowsArray = object.getJSONArray("rows");
            JSONObject rowObject = rowsArray.getJSONObject(0);
            JSONArray elementArray = rowObject.getJSONArray("elements");
            JSONObject elementObject = elementArray.getJSONObject(0);
            JSONObject distanceObject = elementObject.getJSONObject("distance");
            //Distance comes in the form "XX.XX mi", need to remove after the whitespace
            String distanceWithUnits = distanceObject.getString("text");
            int spacePosition = distanceWithUnits.indexOf(" ");
            String distanceWithoutUnits = distanceWithUnits.substring(0, spacePosition);
            distance = Double.parseDouble(distanceWithoutUnits);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Distance parsed = " + distance);

        return distance;
    }

    /**
     * Parses the JSON response from the Directions request
     * @param object Response from the Directions API
     * @return Encoded polyline that represents the route
     */
    private String parseDirectionsResponse(JSONObject object) {
        StringBuilder builder = new StringBuilder();
        try {
            JSONArray routes = object.getJSONArray("routes");
            JSONObject route = routes.getJSONObject(0);
            JSONObject overview = route.getJSONObject("overview_polyline");
            builder.append(overview.getString("points"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    /**
     * Checks if the distance to the waypoint is close enough to the desired length
     * @param distance Distance to the waypoint
     * @return True if close enough, false otherwise
     */
    private boolean checkWaypoint(double distance) {
        boolean closeEnough = true;
        if (distance + MAX_DISTANCE_DIFFERENCE > routeDistanceSelected ||
            distance - MAX_DISTANCE_DIFFERENCE < routeDistanceSelected) {
            closeEnough = false;
        }
        //return closeEnough;
        return true;
    }
}
