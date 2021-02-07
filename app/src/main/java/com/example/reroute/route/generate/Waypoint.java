package com.example.reroute.route.generate;

/**
 * This class represents waypoints in a route. It holds the Place ID and address of the waypoint
 */
public class Waypoint {

    private String id;
    private String address;

    Waypoint(String id, String address) {
        this.id = id;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }
}
