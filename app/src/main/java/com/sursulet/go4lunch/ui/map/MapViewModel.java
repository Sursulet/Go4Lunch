package com.sursulet.go4lunch.ui.map;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.SingleLiveEvent;
import com.sursulet.go4lunch.model.Result;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;
import com.sursulet.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    @NonNull
    private final Application application;
    @NonNull
    private final CurrentLocationRepository currentLocationRepository;
    @NonNull
    private final UserRepository userRepository;

    private final MutableLiveData<Boolean> isMapReadyLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> userQueryLiveData = new MutableLiveData<>();

    private final MediatorLiveData<List<MapUiModel>> uiModelsMediatorLiveData = new MediatorLiveData<>();

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

                //TODO: Doit-on mettre une MutableLiveData/MediatorLiveData ?
                List<User> usersGoingToRestaurant = userRepository.getUsersForRestaurant(result.getPlaceId()).getValue();
                Boolean isGoing = usersGoingToRestaurant != null && usersGoingToRestaurant.size() > 0;

                MapUiModel mapUiModel = new MapUiModel(
                        result.getName(),
                        result.getPlaceId(),
                        bitmapDescriptorFromVector(application, isGoing),
                        result.getGeometry().getLocation().getLat(),
                        result.getGeometry().getLocation().getLng()
                );

                results.add(mapUiModel);
            }
        }

        uiModelsMediatorLiveData.setValue(results);
    }

    //TODO:
    public LiveData<Location> getLastLocation() {
        return currentLocationRepository.getLocationLiveData();
    }

    //TODO : Custom map marker
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, Boolean isGoing) {
        int color = (isGoing) ? R.drawable.ic_map_marker_24 : R.drawable.ic_map_marker_default_24;
        Drawable vectorDrawable = ContextCompat.getDrawable(context, color);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public LiveData<List<MapUiModel>> getMapUiModelLiveData() {
        return uiModelsMediatorLiveData;
    }

    public SingleLiveEvent<String> getSingleLiveEventLaunchDetailActivity() {
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

    public void launchDetailPlaceActivity(String id) {
        singleLiveEventLaunchDetailActivity.setValue(id);
    }
}
