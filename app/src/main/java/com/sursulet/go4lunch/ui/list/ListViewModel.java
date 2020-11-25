package com.sursulet.go4lunch.ui.list;

import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.model.Result;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ListViewModel extends ViewModel {

    private final CurrentLocationRepository currentLocationRepository;
    private final NearbyPlacesRepository nearbyPlacesRepository;

    public ListViewModel(
            CurrentLocationRepository currentLocationRepository,
            NearbyPlacesRepository nearbyPlacesRepository) {
        this.currentLocationRepository = currentLocationRepository;
        this.nearbyPlacesRepository = nearbyPlacesRepository;
    }

    public LiveData<List<ListUiModel>> getListUiModelLiveData() {
        LiveData<List<Result>> nearbyPlacesDependingOnGps = Transformations.switchMap(
                currentLocationRepository.getLocationLiveData(),
                new Function<Location, LiveData<List<Result>>>() {
                    @Override
                    public LiveData<List<Result>> apply(Location location) {
                        return nearbyPlacesRepository.getNearByPlaces(location.getLatitude(), location.getLongitude());
                    }
                }
        );

        return Transformations.map(nearbyPlacesDependingOnGps, new Function<List<Result>, List<ListUiModel>>() {
            @Override
            public List<ListUiModel> apply(List<Result> input) {
                List<ListUiModel> results = new ArrayList<>();

                for (Result result : input) {
                    ListUiModel listUiModel = new ListUiModel(
                            result.getPlaceId(),
                            result.getName(),
                            getPhotoPlaceUrl(result.getPhotos().get(0).getPhotoReference(), "1000"),
                            result.getVicinity(),
                            null, //result.getOpeningHours().getOpenNow().toString(),
                            getDistance(
                                    currentLocationRepository.getLocationLiveData().getValue().getLatitude(),
                                    currentLocationRepository.getLocationLiveData().getValue().getLongitude(),
                                    //48.8511334,2.34837,
                                    result.getGeometry().getLocation().getLat(),
                                    result.getGeometry().getLocation().getLng()
                            ) + " m",
                            getRating(result.getRating()),
                            null
                    );
                    results.add(listUiModel);
                }

                return results;
            }
        });
    }

    private String getPhotoPlaceUrl(String photo_reference, String max_width) {
        String url = "https://maps.googleapis.com/maps/api/place/photo" +
                "?maxwidth=" + max_width +
                "&photoreference=" + photo_reference +
                "&key=" + "AIzaSyDvUeXTbuq87mNoavyfSj_1AWVOK_dMyiE";
        return url;
    }

    private String getDistance(double lat1, double lng1, double lat2, double lng2) {
        /*double theta = lng1 - lng2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515 * 1.609344 * 1000;

        return String.valueOf(dist);*/

        float[] results = new float[10];
        Location.distanceBetween(lat1,lng1,lat2,lng2,results);
        float distance =  results[0];
        DecimalFormat df = new DecimalFormat("###.#");
        String distanceString = df.format(distance);
        return String.valueOf(distanceString);
    }

    private String getRating(double rating) {
        rating = Math.round(rating);

        return String.valueOf(rating);
    }

}
