package com.sursulet.go4lunch.ui.map;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.model.Result;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;
import com.sursulet.go4lunch.ui.DetailPlaceActivity;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    @NonNull
    private final Application application;
    @NonNull
    private final CurrentLocationRepository currentLocationRepository;

    private final MutableLiveData<Boolean> isMapReadyLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> userQueryLiveData = new MutableLiveData<>();

    private final MediatorLiveData<List<MapUiModel>> uiModelsMediatorLiveData = new MediatorLiveData<>();

    public MapViewModel(
            @NonNull Application application,
            @NonNull CurrentLocationRepository currentLocationRepository,
            @NonNull NearbyPlacesRepository nearbyPlacesRepository
    ) {
        this.application = application;
        this.currentLocationRepository = currentLocationRepository;

        LiveData<List<Result>> nearbyPlacesDependingOnGps =
                Transformations.switchMap(currentLocationRepository.getLocationLiveData(),
                        new Function<Location, LiveData<List<Result>>>() {
                            @Override
                            public LiveData<List<Result>> apply(Location location) {
                                return nearbyPlacesRepository.getNearByPlaces(
                                        location.getLatitude(),
                                        location.getLongitude()
                                );
                            }
                        });

        uiModelsMediatorLiveData.addSource(nearbyPlacesDependingOnGps, new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> results) {
                combine(results, isMapReadyLiveData.getValue(), userQueryLiveData.getValue());
            }
        });

        uiModelsMediatorLiveData.addSource(isMapReadyLiveData, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isMapReady) {
                combine(nearbyPlacesDependingOnGps.getValue(), isMapReady, userQueryLiveData.getValue());
            }
        });

        uiModelsMediatorLiveData.addSource(userQueryLiveData, new Observer<String>() {
            @Override
            public void onChanged(String userQuery) {
                combine(nearbyPlacesDependingOnGps.getValue(), isMapReadyLiveData.getValue(), userQuery);
            }
        });
    }

    private void combine(@Nullable List<Result> resultsFromServer, @Nullable Boolean isMapReady, @Nullable String userQuery) {
        if (resultsFromServer == null || isMapReady == null) {
            return;
        }

        List<MapUiModel> results = new ArrayList<>();

        if (isMapReady) {
            for (Result result : resultsFromServer) {
                MapUiModel mapUiModel = new MapUiModel(
                        result.getName(),
                        result.getPlaceId(),
                        null,
                        result.getGeometry().getLocation().getLat(),
                        result.getGeometry().getLocation().getLng()
                );

                results.add(mapUiModel);
            }
        }

        uiModelsMediatorLiveData.setValue(results);
    }

    public LiveData<List<MapUiModel>> getMapUiModelLiveData() {
        return uiModelsMediatorLiveData;
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

    public void launchDetailPlaceActivity(String id) {
        application.startActivity(DetailPlaceActivity.getStartIntent(application, id));
    }
}
