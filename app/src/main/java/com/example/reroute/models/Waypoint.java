package com.example.reroute.models;

/**
 * This class represents waypoints in a route. It holds the Place ID and address of the waypoint
 */
public class Waypoint {

    private String id;
    private String address;
    private double distance;

    public Waypoint(String id, String address) {
        this.id = id;
        this.address = address;
    }

    public Waypoint(String id, String address, double distance) {
        this.id = id;
        this.address = address;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public double getDistance() {
        return distance;
    }
}
