package com.sursulet.go4lunch.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;

public class MapViewModel extends ViewModel {

    private final CurrentLocationRepository currentLocationRepository;
    private final NearbyPlacesRepository nearByPlacesRepository;

    private MediatorLiveData<MapUiModel> mapUiModelMediatorLiveData;

    public MapViewModel(
            CurrentLocationRepository currentLocationRepository,
            NearbyPlacesRepository nearByPlacesRepository
    ) {
        this.currentLocationRepository = currentLocationRepository;
        this.nearByPlacesRepository = nearByPlacesRepository;
    }

    public LiveData<MapUiModel> getMapUiModelLiveData() { return mapUiModelMediatorLiveData; }
}
