package com.example.reroute.utils;

import android.util.Log;

import com.example.reroute.BuildConfig;
import com.example.reroute.data.models.Waypoint;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains util methods
 */
public class Util {

    private final static String TAG = "[ROUTE]";

    /**
     * Builds the Places Search API request
     * @param place Route starting point
     * @param distance Desired route length
     * @return String representing the request in the form of an HTTP URL
     */
    public static String buildPlacesSearchRequest(Place place, int distance) {
        StringBuilder builder = new StringBuilder();

        if (place.getName() != null && place.getLatLng() != null) {
            builder.append("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            builder.append("&location=");
            builder.append(place.getLatLng().latitude);
            builder.append(",");
            builder.append(place.getLatLng().longitude);
            builder.append("&radius=");
            builder.append(calculateRadius(distance));
            builder.append("&key=");
            builder.append(BuildConfig.GOOGLE_MAPS_API_KEY);
        } else if (place.getName() == null) {
            Log.i(TAG, "Name is null");
        } else if (place.getLatLng() == null) {
            Log.i(TAG, "Lat Long is null");
        }

        return builder.toString();
    }

    /**
     * Calculates the radius to use for the NearbySearch query. The max radius is 50000m (~31 miles)
     * Dividing the distance by 2 and using that for the radius will ensure that no out-and-back route
     *     will be longer than what the user wants to bike.
     * @param distance Distance that the user selected
     * @return Search radius in meters
     */
    private static int calculateRadius(int distance) {
        //Since the max radius is 31 miles, cap the distance at 60 miles
        int cappedDistance = distance % 60;
        return (cappedDistance/2) * 1600;
    }

    /**
     * Builds the Distance Matrix API request
     * @param origin Route starting point
     * @param destination Potential destination for the route
     * @param travelMode Travel mode for the route
     * @return String representing the request in the form of an HTTP URL
     */
    public static String buildDistanceMatrixRequest(Place origin, Waypoint destination, String travelMode) {
        StringBuilder builder = new StringBuilder();

        if (origin.getId() != null) {
            builder.append("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=place_id:");
            builder.append(origin.getId());
            builder.append("&destinations=place_id:");
            builder.append(destination.getId());
            builder.append("&mode=");
            builder.append(travelMode);
            builder.append("&key=");
            builder.append(BuildConfig.GOOGLE_MAPS_API_KEY);
        }
        return builder.toString();
    }

    /**
     * Builds the Directions API request
     * @param origin Route starting/ending point
     * @param waypoint Waypoint for the route
     * @param travelMode Travel mode for the route
     * @return String representing the request in the form of an HTTP url
     */
    public static String buildDirectionsRequest(Place origin, Waypoint waypoint, String travelMode) {
        StringBuilder builder = new StringBuilder();

        if (origin.getId() != null) {
            builder.append("https://maps.googleapis.com/maps/api/directions/json?origin=place_id:");
            builder.append(origin.getId());
            builder.append("&destination=place_id:");
            builder.append(origin.getId());
            builder.append("&waypoints=place_id:");
            builder.append(waypoint.getId());
            builder.append("&mode=");
            builder.append(travelMode);
            builder.append("&key=");
            builder.append(BuildConfig.GOOGLE_MAPS_API_KEY);
        }

        return builder.toString();
    }


    /**
     * Parses the JSON response from the Places Search request
     * @param object Response from the Places Search API
     * @return ArrayList containing the Places returned
     */
    public static ArrayList<Waypoint> parseSearchResponse(JSONObject object) {
        ArrayList<Waypoint> waypointList = new ArrayList<>();
        try {
            Log.d(TAG, object.toString());
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
    public static double parseDistanceResponse(JSONObject object) {
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
    public static String parseDirectionsResponse(JSONObject object) {
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
     * Decodes the encoded polyline into a list of places along the route
     * @param encoded Polyline to be decoded
     * @return List of LatLng objects that represent the route
     */
    public static List<LatLng> decodePolyline(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int decodedLatitude = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += decodedLatitude;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int decodedLongitude = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += decodedLongitude;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
