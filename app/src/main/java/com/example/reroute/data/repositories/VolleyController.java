package com.example.reroute.data.repositories;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

/**
 * This class provides RequestQueue functionality. Setup a single instance of RequestQueue
 * with the Application context to ensure that the RequestQueue will last the entire lifetime of the app
 */
class VolleyController {

    private static VolleyController instance;
    //RequestQueue manages the threads that run the network operations
    private RequestQueue requestQueue;

    /**
     * Initializes the RequestQueue
     * @param context Application context
     */
    private VolleyController(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Setup a single instance of this class
     */
    static VolleyController getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyController(context);
        }
        return instance;
    }

    /**
     * @return Single instance of RequestQueue
     */
    private RequestQueue getRequestQueue() {
        return requestQueue;
    }

    /**
     * Adds the request to the RequestQueue
     * @param req Request to be added
     */
    void addToRequestQueue(JsonObjectRequest req) {
        getRequestQueue().add(req);
    }
}
