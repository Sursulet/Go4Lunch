package com.sursulet.go4lunch.ui.map;

import androidx.annotation.DrawableRes;

public class MapUiModel {

    private final String name;
    private final String placeId;
    @DrawableRes
    private final int icon;
    private final Double lat;
    private final Double lng;


    public MapUiModel(String name, String placeId, @DrawableRes int icon, Double lat, Double lng) {
        this.name = name;
        this.placeId = placeId;
        this.icon = icon;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public String getPlaceId() {
        return placeId;
    }

    @DrawableRes
    public int getIcon() { return icon; }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }
}
