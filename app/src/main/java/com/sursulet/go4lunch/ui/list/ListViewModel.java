package com.sursulet.go4lunch.ui.list;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.SingleLiveEvent;
import com.sursulet.go4lunch.Utils;
import com.sursulet.go4lunch.model.Geometry;
import com.sursulet.go4lunch.model.Location;
import com.sursulet.go4lunch.model.OpeningHours;
import com.sursulet.go4lunch.model.Result;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.DetailPlaceRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;
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

    private final MediatorLiveData<List<ListUiModel>> uiModelMediator = new MediatorLiveData<>();
    private final MediatorLiveData<Map<String, GooglePlacesDetailResult>> detailPlaceMediator = new MediatorLiveData<>();
    private final MediatorLiveData<Map<String, Integer>> workmatesMediator = new MediatorLiveData<>();

    private final List<String> alreadyRequiredIds = new ArrayList<>();

    private final SingleLiveEvent<String> singleLiveEventOpenDetailActivity = new SingleLiveEvent<>();

    public ListViewModel(
            @NonNull CurrentLocationRepository currentLocationRepository,
            @NonNull NearbyPlacesRepository nearbyPlacesRepository,
            @NonNull DetailPlaceRepository detailPlaceRepository,
            @NonNull UserRepository userRepository
    ) {
        this.currentLocationRepository = currentLocationRepository;
        this.nearbyPlacesRepository = nearbyPlacesRepository;
        this.detailPlaceRepository = detailPlaceRepository;
        this.userRepository = userRepository;

        LiveData<List<Result>> nearbyPlacesDependingOnGps = init();/*
        LiveData<List<Result>> nearbyPlacesDependingOnGps = Transformations.switchMap(
                currentLocationRepository.getLocationLiveData(),
                location -> nearbyPlacesRepository.getNearByPlaces(
                        location.getLatitude(),
                        location.getLongitude()
                )
        );*/

        workmatesMediator.setValue(new HashMap<>());

        uiModelMediator.addSource(
                nearbyPlacesDependingOnGps,
                nearbyPlaces -> combine(
                        nearbyPlaces,
                        //detailPlaceMediator.getValue(),
                        workmatesMediator.getValue()
                ));
/*
        uiModelMediator.addSource(
                detailPlaceMediator,
                nearbyPlace -> combine(
                        nearbyPlacesDependingOnGps.getValue(),
                        nearbyPlace,
                        workmatesMediator.getValue()
                ));

 */

        uiModelMediator.addSource(
                workmatesMediator,
                users -> combine(
                        nearbyPlacesDependingOnGps.getValue(),
                        //detailPlaceMediator.getValue(),
                        users
                ));

    }

    private void combine(
            @Nullable List<Result> nearbyPlaces,
            //@Nullable Map<String, GooglePlacesDetailResult> mapDetail,
            @Nullable Map<String,Integer> numberWorkmatesMap
    ) {
        if (nearbyPlaces == null /*|| usersList == null*/) return;

        List<ListUiModel> listUiModels = new ArrayList<>();
        for (Result nearbyPlace : nearbyPlaces) {
            String placeId = nearbyPlace.getPlaceId();
            /*
            assert mapDetail != null;
            GooglePlacesDetailResult existingPlaceDetail = mapDetail.get(nearbyPlace.getPlaceId());

            if (existingPlaceDetail == null) {
                if (!alreadyRequiredIds.contains(nearbyPlace.getPlaceId())) {
                    alreadyRequiredIds.add(nearbyPlace.getPlaceId());

                    detailPlaceMediator.addSource(
                            detailPlaceRepository.getDetailPlace(nearbyPlace.getPlaceId()),
                            detailPlace -> {
                                Map<String, GooglePlacesDetailResult> existingMap = detailPlaceMediator.getValue();
                                assert existingMap != null;
                                existingMap.put(nearbyPlace.getPlaceId(), detailPlace);
                                detailPlaceMediator.setValue(existingMap);
                            });
                }
            }

             */
            assert numberWorkmatesMap != null;
            if(numberWorkmatesMap.get(placeId) == null) {
                if(!alreadyRequiredIds.contains(placeId)) {
                    alreadyRequiredIds.add(placeId);
                    workmatesMediator.addSource(userRepository.getActiveRestaurantAsNumber(placeId), new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            Map<String, Integer> existingMap = workmatesMediator.getValue();
                            assert existingMap != null;
                            existingMap.put(placeId,integer);
                            workmatesMediator.setValue(existingMap);
                        }
                    });
                }
            } else {
                //String url = Utils.getPhotoOfPlace(nearbyPlace.getPhotos().get(0).getPhotoReference(), 500);
                String sentence = nearbyPlace.getVicinity();
                String rating = Utils.getRating(nearbyPlace.getRating());

                ListUiModel listUiModel = new ListUiModel(
                        nearbyPlace.getPlaceId(),
                        nearbyPlace.getName(),
                        nearbyPlace.getIcon(),
                        sentence,
                        null,
                        null,
                        null,
                        null
                );

                listUiModels.add(listUiModel);
            }

        }

        uiModelMediator.setValue(listUiModels);
    }

    public MediatorLiveData<List<ListUiModel>> getUiModelMediator() {
        return uiModelMediator;
    }

    public SingleLiveEvent<String> getSingleLiveEventOpenDetailActivity() {
        return singleLiveEventOpenDetailActivity;
    }

    public void openDetailPlaceActivity(String id) {
        singleLiveEventOpenDetailActivity.setValue(id);
    }

    public LiveData<List<ListUiModel>> getUiModels () {
        LiveData<List<Result>> nearbyPlacesDependingOnGps = init();
        return Transformations.map(nearbyPlacesDependingOnGps, new Function<List<Result>, List<ListUiModel>>() {
            @Override
            public List<ListUiModel> apply(List<Result> input) {
                List<ListUiModel> models = new ArrayList<>();
                for(Result result : input) {
                    ListUiModel listUiModel = new ListUiModel(
                            result.getPlaceId(),
                            result.getName(),
                            null,
                            result.getVicinity(),
                            null,
                            null,
                            null,
                            null
                    );
                    models.add(listUiModel);
                }
                return models;
            }
        });
    }

    private LiveData<List<Result>> init() {
        MutableLiveData<List<Result>> mutableLiveData = new MutableLiveData<>();
        List<Result> results = new ArrayList<>();
        Result result = new Result();
        result.setBusinessStatus("OPERATIONAL");
        Geometry geometry = new Geometry();
        Location location = new Location();
        location.setLat(48.858397); location.setLng(2.3501027);
        geometry.setLocation(location);
        result.setGeometry(geometry);
        result.setName("Benoit Paris");
        result.setIcon("https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/restaurant-71.png");
        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenNow(false);
        result.setOpeningHours(openingHours);
        result.setPlaceId("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");
        result.setPriceLevel(4);
        result.setRating(4.1);
        result.setReference("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");
        result.setVicinity("20 Rue Saint-Martin, Paris");

        results.add(result);
        mutableLiveData.postValue(results);
        return mutableLiveData;
    }
}
