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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        LiveData<GooglePlacesDetailResult> placesDetailResultLiveData = detailPlaceRepository.getDetailPlace(id);
        //LiveData<GooglePlacesDetailResult> placesDetailResultLiveData = detailPlaceRepository.init();
        LiveData<List<User>> usersLiveData = restaurantRepository.getAllBookings(restaurantId);
        LiveData<Boolean> isGoingLiveData = restaurantRepository.isBooking(restaurantId);
        LiveData<Boolean> isLikePlaceLiveData = restaurantRepository.isFollower(restaurantId);

        workmatesUiLiveData = Transformations.map(
                usersLiveData,
                userList -> {
                    List<WorkmatesUiModel> results = new ArrayList<>();
                    for (User user : userList) {
                        if(!(user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
                            String sentence = user.getUsername() + " is joining! ";
                            WorkmatesUiModel workmatesUiModel = new WorkmatesUiModel(
                                    user.getUid(),
                                    sentence,
                                    user.getAvatarUrl(),
                                    Typeface.BOLD
                            );

                            results.add(workmatesUiModel);
                        }
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

        restaurantName = resultFromServer.getDetailResult().getName();
        isGoingToRestaurant = isGoing;
        isLikeRestaurant = isLike;
        String photo = "";/*Utils.getPhotoOfPlace(
                resultFromServer.getDetailResult()
                        .getPhotos()
                        .get(0)
                        .getPhotoReference(),
                1000);*/
        int isGoingColor = (isGoing) ? R.color.primary : R.color.secondary;
        int isLikeColor = (isLike) ? R.color.primary : R.color.secondary;
        float rating = Utils.getRating(resultFromServer.getDetailResult().getRating());
        String opening = Utils.getOpeningHours(resultFromServer.getDetailResult().getOpeningHours());

        uiModelMediatorLiveData.setValue(
                new DetailPlaceUiModel(
                        restaurantName,
                        photo,
                        isGoingColor,
                        resultFromServer.getDetailResult().getFormattedAddress(),
                        rating,
                        resultFromServer.getDetailResult().getFormattedPhoneNumber(),
                        isLikeColor,
                        resultFromServer.getDetailResult().getWebsite(),
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

        if (!isGoingToRestaurant) {
            FirebaseUser userValue = FirebaseAuth.getInstance().getCurrentUser();
            if (userValue != null) {
                String urlPicture = (userValue.getPhotoUrl() != null) ? userValue.getPhotoUrl().toString() : null;
                String username = userValue.getDisplayName();
                String uid = userValue.getUid();

                Log.d("PEACH", "onGoingButtonClick not true: " + restaurantId);
                restaurantRepository.createActiveRestaurant(
                        restaurantId,
                        restaurantName,
                        uid,
                        username,
                        urlPicture
                );
            }

        } else {
            restaurantRepository.deleteBooking(restaurantId, FirebaseAuth.getInstance().getCurrentUser().getUid());
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
            restaurantRepository.deleteFollower(restaurantId, FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
    }

    public SingleLiveEvent<String> getEventOpenChatActivity() { return eventOpenChatActivity; }
    public void openChatActivity(String id) {
        eventOpenChatActivity.setValue(id);
    }

    public void setWorkmatesUiLiveData(List<WorkmatesUiModel> users) {
        MutableLiveData<List<WorkmatesUiModel>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(users);
        workmatesUiLiveData = mutableLiveData;
    }
}
