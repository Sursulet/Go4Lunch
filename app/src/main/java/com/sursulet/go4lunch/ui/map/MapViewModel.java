package com.sursulet.go4lunch.ui.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.utils.SingleLiveEvent;
import com.sursulet.go4lunch.model.nearby.Result;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;
import com.sursulet.go4lunch.repository.RestaurantRepository;
import com.sursulet.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    @NonNull
    private final CurrentLocationRepository currentLocationRepository;

    LiveData<Location> currentLocationLiveData;
    LiveData<List<Result>> nearbyPlacesDependingOnGps;
    private final MediatorLiveData<List<MapUiModel>> uiModelsMediatorLiveData = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> isMapReadyLiveData = new MutableLiveData<>();

    private final SingleLiveEvent<String> singleLiveEventLaunchDetailActivity = new SingleLiveEvent<>();

    public MapViewModel(
            @NonNull CurrentLocationRepository currentLocationRepository,
            @NonNull NearbyPlacesRepository nearbyPlacesRepository,
            @NonNull UserRepository userRepository,
            @NonNull RestaurantRepository restaurantRepository
    ) {
        this.currentLocationRepository = currentLocationRepository;

        currentLocationLiveData = currentLocationRepository.getLastLocationLiveData();

        nearbyPlacesDependingOnGps =
                Transformations.switchMap(
                        currentLocationLiveData,
                        location -> nearbyPlacesRepository.getNearByPlaces(
                                location.getLatitude(),
                                location.getLongitude()
                        ));

        LiveData<String> userQueryLiveData = userRepository.getSelectedQuery();
        LiveData<List<String>> activeRestaurantsLiveData = restaurantRepository.getAllActiveRestaurantsIds();

        uiModelsMediatorLiveData.addSource(
                activeRestaurantsLiveData,
                activeRestaurantIds -> combine(
                        nearbyPlacesDependingOnGps.getValue(),
                        isMapReadyLiveData.getValue(),
                        userQueryLiveData.getValue(),
                        activeRestaurantIds));

        uiModelsMediatorLiveData.addSource(
                nearbyPlacesDependingOnGps,
                results -> combine(
                        results,
                        isMapReadyLiveData.getValue(),
                        userQueryLiveData.getValue(),
                        activeRestaurantsLiveData.getValue()));

        uiModelsMediatorLiveData.addSource(
                isMapReadyLiveData,
                isMapReady -> combine(
                        nearbyPlacesDependingOnGps.getValue(),
                        isMapReady,
                        userQueryLiveData.getValue(),
                        activeRestaurantsLiveData.getValue()));

        uiModelsMediatorLiveData.addSource(
                userQueryLiveData,
                userQuery -> combine(
                        nearbyPlacesDependingOnGps.getValue(),
                        isMapReadyLiveData.getValue(),
                        userQuery,
                        activeRestaurantsLiveData.getValue()));
    }

    private void combine(
            @Nullable List<Result> resultsFromServer,
            @Nullable Boolean isMapReady,
            @Nullable String userQuery,
            @Nullable List<String> activeRestaurantIds
    ) {
        if (resultsFromServer == null || isMapReady == null) {
            return;
        }

        List<MapUiModel> results = new ArrayList<>();

        if (isMapReady) {
            for (Result result : resultsFromServer) {

                boolean isGoing = activeRestaurantIds != null && activeRestaurantIds.contains(result.getPlaceId());
                int icon;
                if (result.getName().equalsIgnoreCase(userQuery) || result.getVicinity().equalsIgnoreCase(userQuery)) {
                    icon = R.drawable.ic_baseline_add_circle_24;
                }

                MapUiModel mapUiModel = new MapUiModel(
                        result.getName(),
                        result.getPlaceId(),
                        isGoing ? R.drawable.ic_map_marker_24 : R.drawable.ic_map_marker_48dp, //TODO Taille marqueurs
                        result.getGeometry().getLocation().getLat(),
                        result.getGeometry().getLocation().getLng()
                );

                results.add(mapUiModel);
            }
        }

        uiModelsMediatorLiveData.setValue(results);
    }

    public void buildLocationRequest() { currentLocationRepository.buildLocationRequest(); }
    public void buildLocationCallback() { currentLocationRepository.buildLocationCallback(); }

    public LiveData<Location> getLastLocation() {
        return currentLocationLiveData;
    }

    public LiveData<List<MapUiModel>> getMapUiModelLiveData() {
        return uiModelsMediatorLiveData;
    }

    public SingleLiveEvent<String> getSingleLiveEventOpenDetailActivity() {
        return singleLiveEventLaunchDetailActivity;
    }

    public void onMapReady() {
        isMapReadyLiveData.setValue(true);
    }

    public void getStartLocationUpdates() {
        currentLocationRepository.startLocationUpdates();
    }

    public void getStopLocationUpdates() {
        currentLocationRepository.stopLocationUpdates();
    }

    public void openDetailPlaceActivity(String id) {
        singleLiveEventLaunchDetailActivity.setValue(id);
    }
}
