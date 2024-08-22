package com.terminal.gclo.Models;

public class LocationUpdate {
    public String userId;
    public String latitude;
    public String longitude;
    public String distance;
    public String zone;


    public LocationUpdate(String userId, String latitude, String longitude, String distance, String zone) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.zone = zone;
    }

}
