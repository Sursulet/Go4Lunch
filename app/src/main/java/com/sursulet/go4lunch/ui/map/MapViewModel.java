package com.sursulet.go4lunch.ui.map;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.model.Result;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;

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

    public LiveData<MapUiModel> getMapUiModelLiveData() { return mapUiModelMediatorLiveData; }

    public void getNearbyPlaces() { nearbyPlacesRepository.getNearByPlaces(); }
}
