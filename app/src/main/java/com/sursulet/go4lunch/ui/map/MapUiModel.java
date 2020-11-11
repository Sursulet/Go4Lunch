package com.sursulet.go4lunch.ui.map;

import com.sursulet.go4lunch.model.Result;

import java.util.List;

public class MapUiModel {

    private final String name;
    private final String placeId;
    private final String imageUrl;
    //private int icon;
    //private final Marker marker;
    private final Double lat;
    private final Double lng;


    public MapUiModel(String name, String placeId, String imageUrl, Double lat, Double lng) {
        this.name = name;
        this.placeId = placeId;
        this.imageUrl = imageUrl;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }
}
