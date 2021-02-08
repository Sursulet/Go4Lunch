package com.sursulet.go4lunch.ui.detail;

import android.graphics.Typeface;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.utils.SingleLiveEvent;
import com.sursulet.go4lunch.utils.Utils;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.repository.DetailPlaceRepository;
import com.sursulet.go4lunch.repository.RestaurantRepository;
import com.sursulet.go4lunch.ui.workmates.WorkmatesUiModel;

import java.util.ArrayList;
import java.util.List;

public class DetailPlaceViewModel extends ViewModel {

    @NonNull
    private final DetailPlaceRepository detailPlaceRepository;

    @NonNull
    private final RestaurantRepository restaurantRepository;

    private String restaurantId;
    private String restaurantName;
    private String restaurantAddress;

    private final MediatorLiveData<DetailPlaceUiModel> uiModelMediatorLiveData = new MediatorLiveData<>();

    LiveData<GooglePlacesDetailResult> placesDetailResultLiveData;
    LiveData<List<User>> usersLiveData;
    LiveData<Boolean> isGoingLiveData;
    LiveData<Boolean> isLikePlaceLiveData;
    LiveData<List<WorkmatesUiModel>> workmatesUiLiveData;

    private final SingleLiveEvent<String> eventOpenChatActivity = new SingleLiveEvent<>();

    public DetailPlaceViewModel(
            @NonNull DetailPlaceRepository detailPlaceRepository,
            @NonNull RestaurantRepository restaurantRepository
    ) {
        this.detailPlaceRepository = detailPlaceRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public void startDetailPlace(String id) {
        restaurantId = id;

        placesDetailResultLiveData = detailPlaceRepository.getDetailPlace(id);
        usersLiveData = restaurantRepository.getActiveRestaurantAllBookings(restaurantId);
        isGoingLiveData = restaurantRepository.isBooked(restaurantId);
        isLikePlaceLiveData = restaurantRepository.isFollowed(restaurantId);

        workmatesUiLiveData = Transformations.map(
                usersLiveData,
                userList -> {
                    List<WorkmatesUiModel> results = new ArrayList<>();
                    for (User user : userList) {
                            String sentence = user.getUsername() + " is joining!";
                            WorkmatesUiModel workmatesUiModel = new WorkmatesUiModel(
                                    user.getUid(),
                                    sentence,
                                    user.getAvatarUrl(),
                                    Typeface.BOLD
                            );

                            results.add(workmatesUiModel);
                    }

                    return results;
                });


        uiModelMediatorLiveData.addSource(
                isGoingLiveData,
                isGoing -> combine(
                        placesDetailResultLiveData.getValue(),
                        isGoing,
                        isLikePlaceLiveData.getValue(),
                        workmatesUiLiveData.getValue()
                ));

        uiModelMediatorLiveData.addSource(
                isLikePlaceLiveData,
                isLike -> combine(
                        placesDetailResultLiveData.getValue(),
                        isGoingLiveData.getValue(),
                        isLike,
                        workmatesUiLiveData.getValue()));


        uiModelMediatorLiveData.addSource(
                workmatesUiLiveData,
                workmatesUiModels -> combine(
                        placesDetailResultLiveData.getValue(),
                        isGoingLiveData.getValue(),
                        isLikePlaceLiveData.getValue(),
                        workmatesUiModels
                ));

        uiModelMediatorLiveData.addSource(
                placesDetailResultLiveData,
                result -> combine(
                        result,
                        isGoingLiveData.getValue(),
                        isLikePlaceLiveData.getValue(),
                        workmatesUiLiveData.getValue()
                )
        );
    }

    private void combine(
            @Nullable GooglePlacesDetailResult resultFromServer,
            @Nullable Boolean isGoing,
            @Nullable Boolean isLike,
            @Nullable List<WorkmatesUiModel> workmatesUiModels
    ) {
        if (resultFromServer == null || isGoing == null ||
                isLike == null || workmatesUiModels == null) {
            return;
        }

        restaurantName = resultFromServer.getResult().getName();
        restaurantAddress = resultFromServer.getResult().getFormattedAddress();

        String photo = Utils.getPhotoOfPlace(
                resultFromServer.getResult()
                        .getPhotos()
                        .get(0)
                        .getPhotoReference(),
                1000);
        int isGoingColor = (isGoing) ? R.color.secondary : R.color.primary;
        int isLikeColor = (isLike) ? R.color.secondary : R.color.primary;
        float rating = Utils.getRating(resultFromServer.getResult().getRating());
        String opening = Utils.getOpeningHours(resultFromServer.getResult().getOpeningHours());

        uiModelMediatorLiveData.setValue(
                new DetailPlaceUiModel(
                        restaurantName,
                        photo,
                        isGoingColor,
                        restaurantAddress,
                        rating,
                        resultFromServer.getResult().getFormattedPhoneNumber(),
                        isLikeColor,
                        resultFromServer.getResult().getWebsite(),
                        workmatesUiModels,
                        opening
                ));
    }

    public LiveData<DetailPlaceUiModel> getUiModelLiveData() {
        return uiModelMediatorLiveData;
    }

    public void onGoingButtonClick() {
        restaurantRepository.onGoingButtonClick(restaurantId, restaurantName, restaurantAddress);
    }

    public void onLikeButtonClick() {
        restaurantRepository.onLikeButtonClick(restaurantId);
    }

    public SingleLiveEvent<String> getEventOpenChatActivity() { return eventOpenChatActivity; }
    public void openChatActivity(String id) {
        eventOpenChatActivity.setValue(id);
    }

}
