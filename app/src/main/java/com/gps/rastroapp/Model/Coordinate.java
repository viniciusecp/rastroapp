package com.gps.rastroapp.Model;

import java.io.Serializable;

public class Coordinate implements Serializable {

    private String date;
    private String latitude;
    private String longitude;
    private String speed;

    public Coordinate(String date, String latitude, String longitude, String speed) {
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }
}
