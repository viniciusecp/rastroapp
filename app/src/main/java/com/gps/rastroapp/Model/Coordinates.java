package com.gps.rastroapp.Model;

import org.json.JSONArray;

public class Coordinates {

    private JSONArray coordinates;

    public JSONArray getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(JSONArray coordinates) {
        this.coordinates = coordinates;
    }

    public Coordinates(JSONArray coordinates) {
        this.coordinates = coordinates;
    }
}
