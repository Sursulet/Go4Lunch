package com.sursulet.go4lunch.ui.map;

import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.model.Result;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    //private final CurrentLocationRepository currentLocationRepository;
    private final NearbyPlacesRepository nearbyPlacesRepository;

    private MediatorLiveData<MapUiModel> mapUiModelMediatorLiveData;

    public MapViewModel(
            //CurrentLocationRepository currentLocationRepository,
            NearbyPlacesRepository nearbyPlacesRepository
    ) {
        //this.currentLocationRepository = currentLocationRepository;
        this.nearbyPlacesRepository = nearbyPlacesRepository;

        LiveData<List<Result>> placesLiveData = nearbyPlacesRepository.getNearByPlaces();
    }

    public LiveData<List<MapUiModel>> getMapUiModelLiveData() {
        return Transformations.map(nearbyPlacesRepository.getNearByPlaces(), new Function<List<Result>, List<MapUiModel>>() {
            @Override
            public List<MapUiModel> apply(List<Result> input) {
                List<MapUiModel> results = new ArrayList<>();

                for (Result result : input) {
                    MapUiModel mapUiModel = new MapUiModel(
                            result.getName(),
                            result.getPlaceId(),
                            null,
                            result.getGeometry().getLocation().getLat(),
                            result.getGeometry().getLocation().getLng()
                    );

                    results.add(mapUiModel);
                }

                return results;
            }
        });
    }

    public void getNearbyPlaces() {
        nearbyPlacesRepository.getNearByPlaces();
    }
}
