package com.example.reroute.route.generate;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents waypoints in a route. It holds the Place ID and address of the waypoint
 */
public class Waypoint implements Parcelable {

    private String id;
    private String address;

    /**
     * Implement the Parcelable.Creator interface
     */
    public static final Creator<Waypoint> CREATOR = new Creator<Waypoint>() {
        @Override
        public Waypoint createFromParcel(Parcel in) {
            return new Waypoint(in);
        }

        @Override
        public Waypoint[] newArray(int size) {
            return new Waypoint[size];
        }
    };

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

    private Waypoint(Parcel in) {
        this.id = in.readString();
        this.address = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.address);
    }
}
