package com.example.reroute.data.repositories;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

/**
 * This class provides RequestQueue functionality. Setup a single instance of RequestQueue
 * with the Application context to ensure that the RequestQueue will last the entire lifetime of the app
 */
public class VolleyController {

    private final static String TAG = "[ROUTE]";

    private static VolleyController instance;
    private RequestQueue requestQueue;

    private VolleyController(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Setup a single instance of this class
     */
    public static VolleyController getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyController(context);
        }
        return instance;
    }

    private RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void addToRequestQueue(JsonObjectRequest req) {
        getRequestQueue().add(req);
    }
}
