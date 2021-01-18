package com.sursulet.go4lunch.utils;

import com.sursulet.go4lunch.model.Geometry;
import com.sursulet.go4lunch.model.Location;
import com.sursulet.go4lunch.model.Northeast;
import com.sursulet.go4lunch.model.OpeningHours;
import com.sursulet.go4lunch.model.NearbyResult;
import com.sursulet.go4lunch.model.Southwest;
import com.sursulet.go4lunch.model.Viewport;

public class DataUtils {

    public static NearbyResult buildResult(
            double latitude, double longitude,
            double northLatitude, double northLongitude,
            double southLatitude, double southLongitude,
            String name, boolean openNow, String placeId,
            double rating, String reference, String vicinity
    ) {
        NearbyResult nearbyResult = new NearbyResult();

        Location location = new Location();
        location.setLat(latitude); location.setLng(longitude);

        Northeast northeast = new Northeast();
        northeast.setLat(northLatitude); northeast.setLng(northLongitude);

        Southwest southwest = new Southwest();
        southwest.setLat(southLatitude); southwest.setLng(southLongitude);

        Viewport viewport = new Viewport();
        viewport.setNortheast(northeast);
        viewport.setSouthwest(southwest);

        Geometry geometry = new Geometry();
        geometry.setLocation(location);
        geometry.setViewport(viewport);

        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenNow(openNow);

        nearbyResult.setGeometry(geometry);
        nearbyResult.setName(name);
        nearbyResult.setOpeningHours(openingHours);
        nearbyResult.setPlaceId(placeId);
        nearbyResult.setRating(rating);
        nearbyResult.setReference(reference);
        nearbyResult.setVicinity(vicinity);

        return nearbyResult;
    }
}
