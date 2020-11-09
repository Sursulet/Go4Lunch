package com.sursulet.go4lunch.ui.map;

public class MapUiModel {

    private final String name;
    private final String placeId;
    private final String imageUrl;
    //private int icon;

    public MapUiModel(String name, String placeId, String imageUrl) {
        this.name = name;
        this.placeId = placeId;
        this.imageUrl = imageUrl;
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
}
