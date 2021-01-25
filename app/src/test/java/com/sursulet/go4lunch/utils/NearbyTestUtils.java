package com.sursulet.go4lunch.utils;

import com.sursulet.go4lunch.model.nearby.Geometry;
import com.sursulet.go4lunch.model.nearby.Location;
import com.sursulet.go4lunch.model.nearby.Northeast;
import com.sursulet.go4lunch.model.nearby.OpeningHours;
import com.sursulet.go4lunch.model.nearby.Photo;
import com.sursulet.go4lunch.model.nearby.Result;
import com.sursulet.go4lunch.model.nearby.Southwest;
import com.sursulet.go4lunch.model.nearby.Viewport;

import java.util.ArrayList;
import java.util.List;

public class NearbyTestUtils {
    public static Result buildResult(
            double latitude, double longitude,
            double northLatitude, double northLongitude,
            double southLatitude, double southLongitude,
            String name, boolean openNow, String placeId,
            double rating, String reference, String vicinity
    ) {
        Result nearbyResult = new Result();

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

        List<Photo> photos = new ArrayList<>();
        Photo photo = new Photo();
        photo.setPhotoReference("photoRef");
        photos.add(photo);

        nearbyResult.setPhotos(photos);
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
