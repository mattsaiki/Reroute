package com.example.reroute.route.generate;


public interface RouteBuilderCallback {
    void onSuccess(String polyline);
    void onError();
}
