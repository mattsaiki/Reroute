package com.example.reroute.data.models;

/**
 * This class represents waypoints in a route. It holds the Place ID and address of the waypoint
 * It can also hold the optional distance from origin field
 */
public class Waypoint {

    private String id;
    private String address;
    private double distance;

    /**
     * Constructor when the distance from the origin is unknown
     * @param id Waypoint Place ID
     * @param address Waypoint address
     */
    public Waypoint(String id, String address) {
        this.id = id;
        this.address = address;
    }

    /**
     * Constructor when the distance from the origin is known
     * @param id Waypoint Place ID
     * @param address Waypoint address
     * @param distance Distance from origin to the waypoint
     */
    public Waypoint(String id, String address, double distance) {
        this.id = id;
        this.address = address;
        this.distance = distance;
    }

    /**
     * @return Waypoint Place ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return Waypoint address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return Distance from address to waypoint
     */
    public double getDistance() {
        return distance;
    }
}