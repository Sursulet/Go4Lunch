package com.sursulet.go4lunch.ui.list;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.SingleLiveEvent;
import com.sursulet.go4lunch.Utils;
import com.sursulet.go4lunch.model.NearbyResult;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.DetailPlaceRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;
import com.sursulet.go4lunch.repository.RestaurantRepository;
import com.sursulet.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewModel extends ViewModel {

    @NonNull
    private final CurrentLocationRepository currentLocationRepository;
    @NonNull
    private final NearbyPlacesRepository nearbyPlacesRepository;
    @NonNull
    private final DetailPlaceRepository detailPlaceRepository;
    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final RestaurantRepository restaurantRepository;

    private final MediatorLiveData<List<ListUiModel>> uiModelMediator = new MediatorLiveData<>();
    private final MediatorLiveData<Map<String, GooglePlacesDetailResult>> detailPlaceMediator = new MediatorLiveData<>();
    private final MediatorLiveData<Map<String, Integer>> workmatesMediator = new MediatorLiveData<>();
    LiveData<List<NearbyResult>> nearbyPlacesDependingOnGps;

    private final List<String> alreadyRequiredUIds = new ArrayList<>();
    private final List<String> alreadyRequiredIds = new ArrayList<>();

    private final SingleLiveEvent<String> singleLiveEventOpenDetailActivity = new SingleLiveEvent<>();

    public ListViewModel(
            @NonNull CurrentLocationRepository currentLocationRepository,
            @NonNull NearbyPlacesRepository nearbyPlacesRepository,
            @NonNull DetailPlaceRepository detailPlaceRepository,
            @NonNull UserRepository userRepository,
            @NonNull RestaurantRepository restaurantRepository) {
        this.currentLocationRepository = currentLocationRepository;
        this.nearbyPlacesRepository = nearbyPlacesRepository;
        this.detailPlaceRepository = detailPlaceRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;

        LiveData<Location> currentLocationLiveData = currentLocationRepository.getLastLocationLiveData();
        nearbyPlacesDependingOnGps = Transformations.switchMap(
                currentLocationRepository.getLastLocationLiveData(),
                location -> nearbyPlacesRepository.getNearByPlaces(
                        location.getLatitude(),
                        location.getLongitude()
                )
        );

        detailPlaceMediator.setValue(new HashMap<>());
        workmatesMediator.setValue(new HashMap<>());

        uiModelMediator.addSource(
                currentLocationLiveData,
                location -> combine(
                        location,
                        nearbyPlacesDependingOnGps.getValue(),
                        detailPlaceMediator.getValue(),
                        workmatesMediator.getValue()
                ));

        uiModelMediator.addSource(
                nearbyPlacesDependingOnGps,
                nearbyPlaces -> combine(
                        currentLocationLiveData.getValue(),
                        nearbyPlaces,
                        detailPlaceMediator.getValue(),
                        workmatesMediator.getValue()
                ));

        uiModelMediator.addSource(
                detailPlaceMediator,
                nearbyPlace -> combine(
                        currentLocationLiveData.getValue(),
                        nearbyPlacesDependingOnGps.getValue(),
                        nearbyPlace,
                        workmatesMediator.getValue()
                ));

        uiModelMediator.addSource(
                workmatesMediator,
                users -> combine(
                        currentLocationLiveData.getValue(),
                        nearbyPlacesDependingOnGps.getValue(),
                        detailPlaceMediator.getValue(),
                        users
                ));

    }

    private void combine(
            @Nullable Location location,
            @Nullable List<NearbyResult> nearbyPlaces,
            @Nullable Map<String, GooglePlacesDetailResult> mapDetail,
            @Nullable Map<String, Integer> numberWorkmatesMap
    ) {
        if (location == null || nearbyPlaces == null) return;

        List<ListUiModel> uiStateList = new ArrayList<>();
        for (NearbyResult nearbyPlace : nearbyPlaces) {
            String placeId = nearbyPlace.getPlaceId();

            GooglePlacesDetailResult existingPlaceDetail = mapDetail.get(nearbyPlace.getPlaceId());
            if (existingPlaceDetail == null) {
                if (!alreadyRequiredUIds.contains(nearbyPlace.getPlaceId())) {
                    alreadyRequiredUIds.add(nearbyPlace.getPlaceId());
                    detailPlaceMediator.addSource(
                            detailPlaceRepository.getDetailPlace(nearbyPlace.getPlaceId()),
                            detailPlace -> {
                                Map<String, GooglePlacesDetailResult> existingMap = detailPlaceMediator.getValue();
                                assert existingMap != null;
                                existingMap.put(nearbyPlace.getPlaceId(), detailPlace);
                                detailPlaceMediator.setValue(existingMap);
                            });
                }
            } else if (numberWorkmatesMap.get(placeId) == null) {
                if (!alreadyRequiredIds.contains(placeId)) {
                    alreadyRequiredIds.add(placeId);
                    workmatesMediator.addSource(
                            restaurantRepository.getAllBookings(placeId),
                            users -> {
                                int size = users.size();
                                Map<String, Integer> existingMap = workmatesMediator.getValue();
                                assert existingMap != null;
                                existingMap.put(placeId, size);
                                workmatesMediator.setValue(existingMap);
                            });
                }
            } else {
                String url = "https://unsplash.com/photos/gjlMT52gy5M";//Utils.getPhotoOfPlace(nearbyPlace.getPhotos().get(0).getPhotoReference(), 500);
                String sentence = nearbyPlace.getVicinity();
                String opening = Utils.getOpeningHours(existingPlaceDetail.getDetailResult().getOpeningHours());
                float rating = Utils.getRating(nearbyPlace.getRating());
                String distance = Utils.getDistance(
                        48, 2,//location.getLatitude(), location.getLongitude(),
                        nearbyPlace.getGeometry().getLocation().getLat(), nearbyPlace.getGeometry().getLocation().getLng()
                );
                String numberWorkmates = "(" + numberWorkmatesMap.get(placeId) + ")";

                ListUiModel listUiModel = new ListUiModel(
                        nearbyPlace.getPlaceId(),
                        nearbyPlace.getName(),
                        url,
                        sentence,
                        opening,
                        distance,
                        rating,
                        numberWorkmates
                );

                uiStateList.add(listUiModel);
            }

        }

        uiModelMediator.setValue(uiStateList);
    }

    public LiveData<List<ListUiModel>> getUiModelMediator() {
        return uiModelMediator;
    }

    public SingleLiveEvent<String> getSingleLiveEventOpenDetailActivity() {
        return singleLiveEventOpenDetailActivity;
    }

    public void openDetailPlaceActivity(String id) {
        singleLiveEventOpenDetailActivity.setValue(id);
    }

    public LiveData<String> getSelectedQuery() {
        return userRepository.getSelectedQuery();
    }

    public void setNearbyPlacesDependingOnGps(List<NearbyResult> nearbyResults) {
        MutableLiveData<List<NearbyResult>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.postValue(nearbyResults);

        nearbyPlacesDependingOnGps = mutableLiveData;
    }

    public void setDetailPlaceMediator(
            String nearbyPlaceId,
            GooglePlacesDetailResult detailPlace
    ) {
        Map<String, GooglePlacesDetailResult> existingMap = detailPlaceMediator.getValue();
        //assert existingMap != null;
        existingMap.put(nearbyPlaceId, detailPlace);
        detailPlaceMediator.setValue(existingMap);
    }

    public void setWorkmatesMediator(
            String placeId,
            List<User> users
    ) {
        int size = users.size();
        Map<String, Integer> existingMap = workmatesMediator.getValue();
        //assert existingMap != null;
        existingMap.put(placeId, size);
        workmatesMediator.setValue(existingMap);
    }
}
