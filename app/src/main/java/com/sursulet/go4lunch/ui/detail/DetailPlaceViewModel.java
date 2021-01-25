package com.sursulet.go4lunch.ui.detail;

import android.graphics.Typeface;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.SingleLiveEvent;
import com.sursulet.go4lunch.Utils;
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
    private boolean isGoingToRestaurant;
    private boolean isLikeRestaurant;

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
        isGoingLiveData = restaurantRepository.isBooking(restaurantId);
        isLikePlaceLiveData = restaurantRepository.isFollowing(restaurantId);

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
        isGoingToRestaurant = isGoing;
        isLikeRestaurant = isLike;
        String photo = Utils.getPhotoOfPlace(
                resultFromServer.getResult()
                        .getPhotos()
                        .get(0)
                        .getPhotoReference(),
                1000);
        int isGoingColor = (isGoing) ? R.color.primary : R.color.secondary;
        int isLikeColor = (isLike) ? R.color.primary : R.color.secondary;
        float rating = Utils.getRating(resultFromServer.getResult().getRating());
        String opening = Utils.getOpeningHours(resultFromServer.getResult().getOpeningHours());

        Log.d("PEACH", "combine: " + isLikeColor);

        uiModelMediatorLiveData.setValue(
                new DetailPlaceUiModel(
                        restaurantName,
                        photo,
                        isGoingColor,
                        resultFromServer.getResult().getFormattedAddress(),
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
        String s = String.valueOf(isGoingToRestaurant);
        Log.d("PEACH", "onGoingButtonClick: is? : " + s + " / ");
        restaurantRepository.onGoingButtonClick(restaurantId);

        //Si il va déjà à un restaurant l'empecher d''activer un autre
        if (!isGoingToRestaurant) {
            //Toast.makeText(,"You going to", Toast.LENGTH_SHORT).show();
        }


    }

    public void onLikeButtonClick() {
        restaurantRepository.onLikeButtonClick(restaurantId);
    }

    public SingleLiveEvent<String> getEventOpenChatActivity() { return eventOpenChatActivity; }
    public void openChatActivity(String id) {
        eventOpenChatActivity.setValue(id);
    }

    public void setWorkmatesUiLiveData(List<WorkmatesUiModel> workmates) {
        MutableLiveData<List<WorkmatesUiModel>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(workmates);
        workmatesUiLiveData = mutableLiveData;
    }
}
