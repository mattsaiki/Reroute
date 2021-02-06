package com.example.reroute.route.generate;


public interface RouteBuilderCallback {
    void onSuccess(Waypoint waypoint);
    void onError();
}
