package com.sursulet.go4lunch.ui.map;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.SingleLiveEvent;
import com.sursulet.go4lunch.model.Result;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;
import com.sursulet.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MapViewModel extends ViewModel {

    @NonNull
    private final Application application;
    @NonNull
    private final CurrentLocationRepository currentLocationRepository;
    @NonNull
    private final UserRepository userRepository;

    private final MediatorLiveData<List<MapUiModel>> uiModelsMediatorLiveData = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> isMapReadyLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> userQueryLiveData = new MutableLiveData<>();

    private final SingleLiveEvent<String> singleLiveEventLaunchDetailActivity = new SingleLiveEvent<>();

    public MapViewModel(
            @NonNull Application application,
            @NonNull CurrentLocationRepository currentLocationRepository,
            @NonNull NearbyPlacesRepository nearbyPlacesRepository,
            @NonNull UserRepository userRepository) {
        this.application = application;
        this.currentLocationRepository = currentLocationRepository;
        this.userRepository = userRepository;

        LiveData<List<Result>> nearbyPlacesDependingOnGps =
                Transformations.switchMap(
                        currentLocationRepository.getLastLocationLiveData(),
                        location -> nearbyPlacesRepository.getNearByPlaces(
                                location.getLatitude(),
                                location.getLongitude()
                        ));

        LiveData<Set<String>> activeRestaurantsLiveData = userRepository.getActiveRestaurants();

        uiModelsMediatorLiveData.addSource(
                activeRestaurantsLiveData,
                activeRestaurants -> combine(
                        nearbyPlacesDependingOnGps.getValue(),
                        isMapReadyLiveData.getValue(),
                        userQueryLiveData.getValue(),
                        activeRestaurants));

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
            @Nullable Set<String> activeRestaurants
    ) {
        if (resultsFromServer == null || isMapReady == null) {
            return;
        }

        List<MapUiModel> results = new ArrayList<>();

        if (isMapReady) {
            for (Result result : resultsFromServer) {

                boolean isGoing = activeRestaurants != null && activeRestaurants.contains(result.getPlaceId());

                MapUiModel mapUiModel = new MapUiModel(
                        result.getName(),
                        result.getPlaceId(),
                        isGoing ? R.drawable.ic_map_marker_24 : R.drawable.ic_map_marker_48dp,
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
        return currentLocationRepository.getLastLocationLiveData();
    }

    public LiveData<List<MapUiModel>> getMapUiModelLiveData() {
        return uiModelsMediatorLiveData;
    }

    public SingleLiveEvent<String> getSingleLiveEventOpenDetailActivity() {
        return singleLiveEventLaunchDetailActivity;
    }

    public void onUserQueryChanged(String query) {
        userQueryLiveData.setValue(query);
    }

    public void onMapReady() {
        isMapReadyLiveData.setValue(true);
    }

    public void checkPermission(boolean hasGpsPermissions) {
        if (hasGpsPermissions) {
            currentLocationRepository.startLocationUpdates();
        }
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
