package com.example.reroute.route.generate;

public class Waypoint {
    private String id;
    private String address;

    public Waypoint(String placeId, String placeAddress) {
        id = placeId;
        address = placeAddress;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }
}
