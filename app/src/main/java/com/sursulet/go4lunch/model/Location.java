package com.sursulet.go4lunch.model;

public class Location {
    private String lng;
    private String lat;

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String toString() {
        return  "ClassPojo ["+
                "lng = " +lng+
                "lat = " +lat+
                "]";
    }
}
