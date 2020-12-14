package com.sursulet.go4lunch.ui;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.repository.DetailPlaceRepository;
import com.sursulet.go4lunch.repository.UserRepository;
import com.sursulet.go4lunch.repository.WorkmatesRepository;
import com.sursulet.go4lunch.ui.workmates.WorkmatesUiModel;

import java.util.ArrayList;
import java.util.List;

public class DetailPlaceViewModel extends ViewModel {

    private final DetailPlaceRepository detailPlaceRepository;
    private final WorkmatesRepository workmatesRepository;
    private final UserRepository userRepository;

    private String restaurantId;
    private String restaurantName;

    MediatorLiveData<DetailPlaceUiModel> uiModelMediatorLiveData = new MediatorLiveData<>();
    LiveData<DetailPlaceUiModel> detailPlaceUiModelLiveData;
    LiveData<GooglePlacesDetailResult> placesDetailResultLiveData;
    LiveData<List<User>> usersLiveData;
    LiveData<List<WorkmatesUiModel>> workmatesUiLiveData;

    public DetailPlaceViewModel(
            DetailPlaceRepository detailPlaceRepository,
            WorkmatesRepository workmatesRepository,
            UserRepository userRepository
    ) {
        this.detailPlaceRepository = detailPlaceRepository;
        this.workmatesRepository = workmatesRepository;
        this.userRepository = userRepository;
    }

    public void startDetailPlace(String id) {
        restaurantId = id;
/*
        detailPlaceUiModelLiveData = Transformations.map(detailPlaceRepository.getDetailPlace(id),
                new Function<GooglePlacesDetailResult, DetailPlaceUiModel>() {
            @Override
            public DetailPlaceUiModel apply(GooglePlacesDetailResult result) {

                return new DetailPlaceUiModel(
                        result.getResult().getName(),
                        getPhotoOfPlace(result.getResult().getPhotos().get(0).getPhotoReference()),
                        result.getResult().getFormattedAddress(),
                        null
                );
            }
        });*/

        placesDetailResultLiveData = detailPlaceRepository.getDetailPlace(id);
        usersLiveData = userRepository.getUsersForRestaurant(restaurantId);
        workmatesUiLiveData = Transformations.map(usersLiveData, new Function<List<User>, List<WorkmatesUiModel>>() {
            @Override
            public List<WorkmatesUiModel> apply(List<User> input) {

                List<WorkmatesUiModel> results = new ArrayList<>();

                for (User user : input) {
                    String phrase = user.getUsername() + " is joining! ";

                    WorkmatesUiModel workmatesUiModel = new WorkmatesUiModel(
                            user.getUid(),
                            phrase,
                            user.getAvatarUrl()
                    );
                    results.add(workmatesUiModel);
                }

                return results;
            }
        });

        uiModelMediatorLiveData.addSource(workmatesUiLiveData, new Observer<List<WorkmatesUiModel>>() {
            @Override
            public void onChanged(List<WorkmatesUiModel> workmatesUiModels) {
                combine(placesDetailResultLiveData.getValue(), workmatesUiModels);
            }
        });

        uiModelMediatorLiveData.addSource(placesDetailResultLiveData, new Observer<GooglePlacesDetailResult>() {
            @Override
            public void onChanged(GooglePlacesDetailResult result) {
                combine(result, workmatesUiLiveData.getValue());
            }
        });
    }

    private void combine(GooglePlacesDetailResult resultFromServer,@Nullable List<WorkmatesUiModel> workmates) {
        if(resultFromServer == null || workmatesUiLiveData == null) {
            return;
        }

        restaurantName = resultFromServer.getResult().getName();

        uiModelMediatorLiveData.setValue(new DetailPlaceUiModel(
                restaurantName,
                getPhotoOfPlace(resultFromServer.getResult().getPhotos().get(0).getPhotoReference()),
                resultFromServer.getResult().getFormattedAddress(),
                workmates
        ));
    }

    public LiveData<DetailPlaceUiModel> getUiModelLiveData() {
        return uiModelMediatorLiveData;
    }

    public LiveData<DetailPlaceUiModel> getDetailPlaceUiModelLiveData() {
        return detailPlaceUiModelLiveData;
    }

    private String getPhotoOfPlace(String reference) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth="+"1000");
        url.append("&photoreference="+reference);
        url.append("&key="+"AIzaSyDvUeXTbuq87mNoavyfSj_1AWVOK_dMyiE");

        return url.toString();
    }

    public void onGoingButtonClick() {
        detailPlaceRepository.addRestaurantToUser(restaurantId, restaurantName, FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public void onLikeButtonClick() {
        detailPlaceRepository.addRestaurantToFavorite(restaurantId, FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public LiveData<List<WorkmatesUiModel>> getWorkmatesUiModelLiveData() {
        return Transformations.map(workmatesRepository.getWorkmates(), new Function<List<User>, List<WorkmatesUiModel>>() {
            @Override
            public List<WorkmatesUiModel> apply(List<User> input) {
                List<WorkmatesUiModel> results = new ArrayList<>();

                for (User user : input) {
                    WorkmatesUiModel workmatesUiModel = new WorkmatesUiModel(
                            user.getUid(),
                            user.getUsername() + " is eating ",
                            user.getAvatarUrl()
                    );
                    results.add(workmatesUiModel);
                }

                return results;
            }
        });
    }
}
