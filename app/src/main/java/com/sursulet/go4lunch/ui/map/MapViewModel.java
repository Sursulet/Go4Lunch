package com.sursulet.go4lunch.ui.map;

import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.MainApplication;
import com.sursulet.go4lunch.model.Result;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    private CurrentLocationRepository currentLocationRepository;
    private final NearbyPlacesRepository nearbyPlacesRepository;

    private boolean mapReady;
    private boolean hasPermissions;

    public MapViewModel(
            CurrentLocationRepository currentLocationRepository,
            NearbyPlacesRepository nearbyPlacesRepository
    ) {
        this.currentLocationRepository = currentLocationRepository;
        this.nearbyPlacesRepository = nearbyPlacesRepository;
    }

    public LiveData<List<MapUiModel>> getMapUiModelLiveData() {
        // TODO Stephanie Transformation.switchMap() permet de "regénérer" une nouvelle LiveData quand la valeur initiale change.
        //  Ici, quand la LiveData de Location change, on "redemande" au NearbyPlaceRepository quels sont les restaurants à proximité
        //  de la nouvelle position GPS.
        LiveData<List<Result>> nearbyPlacesDependingOnGps = Transformations.switchMap(currentLocationRepository.getLocationLiveData(), new Function<Location, LiveData<List<Result>>>() {
            @Override
            public LiveData<List<Result>> apply(Location location) {
                return nearbyPlacesRepository.getNearByPlaces(location.getLatitude(), location.getLongitude());
            }
        });

        // TODO Stephanie Transformation.map() permet de simplement changer le type encapsulé d'une LiveData
        //  (ici, de List<PlaceDetailResult> en List<MapUiModel>)
        //  On voit qu'on "chaine" les map et switchMap, ça permet de "dessiner" les liens entre les flux :
        //  1/ quand la Location change, on réémet une LiveData qui plus tard va se pleupler avec les nouvelles "nearby" trouvées
        //  2/ on transforme les POJO "PlaceDetailResult" en objets utilisable par la vue (MapUiModel)
        return Transformations.map(nearbyPlacesDependingOnGps, new Function<List<Result>, List<MapUiModel>>() {
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

    public void getGps(boolean permissions) {
        hasPermissions = permissions;
        if(hasPermissions) currentLocationRepository = new CurrentLocationRepository(MainApplication.getApplication());
    }
}
