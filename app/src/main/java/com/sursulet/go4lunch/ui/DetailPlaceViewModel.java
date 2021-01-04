package com.sursulet.go4lunch.ui;

import android.graphics.Typeface;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.Utils;
import com.sursulet.go4lunch.model.Restaurant;
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

    public DetailPlaceViewModel(
            @NonNull DetailPlaceRepository detailPlaceRepository,
            @NonNull RestaurantRepository restaurantRepository) {
        this.detailPlaceRepository = detailPlaceRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public void startDetailPlace(String id) {
        restaurantId = id;

        LiveData<GooglePlacesDetailResult> placesDetailResultLiveData = detailPlaceRepository.getDetailPlace(id);

        LiveData<Restaurant> activeRestaurantLiveData = restaurantRepository.getActiveRestaurant(restaurantId);
        LiveData<Restaurant> likeRestaurantLiveData = restaurantRepository.getLikeRestaurant(restaurantId);
        LiveData<List<User>> usersLiveData = restaurantRepository.getUsersActiveRestaurant(restaurantId);
        LiveData<Boolean> isGoingLiveData = restaurantRepository.getUserActiveRestaurant(restaurantId, FirebaseAuth.getInstance().getCurrentUser().getUid());
        LiveData<Boolean> isLikePlaceLiveData = restaurantRepository.getUserLikeRestaurant(restaurantId, FirebaseAuth.getInstance().getCurrentUser().getUid());

        LiveData<List<WorkmatesUiModel>> workmatesUiLiveData = Transformations.map(
                usersLiveData,
                userList -> {
                    List<WorkmatesUiModel> results = new ArrayList<>();
                    for (User user : userList) {
                        String sentence = user.getUsername() + " is joining! ";
                        Log.d("PEACH", "list Work: " + user.getUid());
                        WorkmatesUiModel workmatesUiModel = new WorkmatesUiModel(
                                user.getUid(),
                                sentence,
                                user.getAvatarUrl(),
                                Typeface.BOLD
                        );
                        if (!(user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))) {
                            Log.d("PEACH", "I4M HERE: ");
                        }
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

        String s = String.valueOf(isGoing);
        String workmateId = workmatesUiModels.get(0).getUid();
        Log.d("PEACH", "combine: " + restaurantName + " " + s + " " + workmateId);
        /*
        String photo = Utils.getPhotoOfPlace(
                resultFromServer.getResult()
                        .getPhotos()
                        .get(0)
                        .getPhotoReference(),
                1000);

         */
        int isGoingColor = (isGoing) ? R.color.primary : R.color.secondary;
        int isLikeColor = (isLike) ? R.color.primary : R.color.secondary;
        String colorString = String.valueOf(isGoingColor);
        String isLikeColorString = String.valueOf(isLikeColor);
        Log.d("PEACH", "combine: " + isGoing.toString() + " " + colorString + " like :" + isLikeColorString);
        String rating = Utils.getRating(resultFromServer.getResult().getRating());
        String opening = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            opening = Utils.getOpeningHours(resultFromServer.getResult().getOpeningHours());
        }

        uiModelMediatorLiveData.setValue(
                new DetailPlaceUiModel(
                        restaurantName,
                        null,//photo,
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
        Log.d("PEACH", "onGoingButtonClick: " + s + " / ");

        if (!isGoingToRestaurant) {
            FirebaseUser userValue = FirebaseAuth.getInstance().getCurrentUser();
            if (userValue != null) {
                String urlPicture = (userValue.getPhotoUrl() != null) ? userValue.getPhotoUrl().toString() : null;
                String username = userValue.getDisplayName();
                String uid = userValue.getUid();

                Log.d("PEACH", "onGoingButtonClick: " + restaurantId);
                restaurantRepository.createActiveRestaurant(
                        restaurantId,
                        restaurantName,
                        uid,
                        username,
                        urlPicture
                );
            }

        } else {
            restaurantRepository.deleteUserActiveRestaurant(restaurantId, FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
    }

    public void onLikeButtonClick() {
        if(!isLikeRestaurant){
            FirebaseUser userValue = FirebaseAuth.getInstance().getCurrentUser();
            if (userValue != null) {
                String urlPicture = (userValue.getPhotoUrl() != null) ? userValue.getPhotoUrl().toString() : null;
                String username = userValue.getDisplayName();
                String uid = userValue.getUid();

                Log.d("PEACH", "onLikeButtonClick: Not true" + restaurantId);
                restaurantRepository.createLikeRestaurant(
                        restaurantId,
                        restaurantName,
                        uid,
                        username,
                        urlPicture
                );
            }
        } else {
            restaurantRepository.deleteUserLikeRestaurant(restaurantId, FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
    }
}
