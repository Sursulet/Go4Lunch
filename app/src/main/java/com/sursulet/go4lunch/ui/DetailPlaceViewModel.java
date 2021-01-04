package com.sursulet.go4lunch.ui;

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
import com.sursulet.go4lunch.Utils;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.model.details.Close;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.model.details.Open;
import com.sursulet.go4lunch.model.details.OpeningHours;
import com.sursulet.go4lunch.model.details.Period;
import com.sursulet.go4lunch.model.details.Result;
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

        //LiveData<GooglePlacesDetailResult> placesDetailResultLiveData = detailPlaceRepository.getDetailPlace(id);
        LiveData<GooglePlacesDetailResult> placesDetailResultLiveData = init();

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
        String opening = Utils.getOpeningHours(resultFromServer.getResult().getOpeningHours());

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

    private LiveData<GooglePlacesDetailResult> init() {
        MutableLiveData<GooglePlacesDetailResult> mutableLiveData = new MutableLiveData<>();
        Result result = new Result();
        result.setFormattedAddress("20 Rue Saint-Martin, 75004 Paris, France");
        result.setFormattedPhoneNumber("01 42 72 25 76");
        result.setName("Benoit Paris");
        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenNow(false);

        Period period0 = new Period();
        Close close0 = new Close();
        close0.setDay(0);
        close0.setTime("1400");
        Open open0 = new Open();
        open0.setDay(0);
        open0.setTime("1200");
        period0.setClose(close0);
        period0.setOpen(open0);

        Period period0B = new Period();
        Close close0B = new Close();
        close0B.setDay(0);
        close0B.setTime("2130");
        Open open0B = new Open();
        open0B.setDay(0);
        open0B.setTime("1900");
        period0B.setClose(close0B);
        period0B.setOpen(open0B);

        Period period3A = new Period();
        Close close3A = new Close();
        close3A.setDay(3);
        close3A.setTime("1400");
        Open open3A = new Open();
        open3A.setDay(3);
        open3A.setTime("1200");
        period3A.setClose(close3A);
        period3A.setOpen(open3A);

        Period period3B = new Period();
        Close close3B = new Close();
        close3B.setDay(3);
        close3B.setTime("2130");
        Open open3B = new Open();
        open3B.setDay(3);
        open3B.setTime("1900");
        period3B.setClose(close3B);
        period3B.setOpen(open3B);

        Period period4A = new Period();
        Close close4A = new Close();
        close4A.setDay(4);
        close4A.setTime("1400");
        Open open4A = new Open();
        open4A.setDay(4);
        open4A.setTime("1200");
        period4A.setClose(close4A);
        period4A.setOpen(open4A);

        Period period4B = new Period();
        Close close4B = new Close();
        close4B.setDay(4);
        close4B.setTime("2130");
        Open open4B = new Open();
        open4B.setDay(4);
        open4B.setTime("1900");
        period4B.setClose(close4B);
        period4B.setOpen(open4B);

        Period period5A = new Period();
        Close close5A = new Close();
        close5A.setDay(5);
        close5A.setTime("1400");
        Open open5A = new Open();
        open5A.setDay(5);
        open5A.setTime("1200");
        period5A.setClose(close5A);
        period5A.setOpen(open5A);

        Period period5B = new Period();
        Close close5B = new Close();
        close5B.setDay(5);
        close5B.setTime("2130");
        Open open5B = new Open();
        open5B.setDay(5);
        open5B.setTime("1900");
        period5B.setClose(close5B);
        period5B.setOpen(open5B);

        Period period6A = new Period();
        Close close6A = new Close();
        close6A.setDay(6);
        close6A.setTime("1400");
        Open open6A = new Open();
        open6A.setDay(6);
        open6A.setTime("1200");
        period6A.setClose(close6A);
        period6A.setOpen(open6A);

        Period period6B = new Period();
        Close close6B = new Close();
        close6B.setDay(6);
        close6B.setTime("2130");
        Open open6B = new Open();
        open6B.setDay(6);
        open6B.setTime("1900");
        period6B.setClose(close6B);
        period6B.setOpen(open6B);

        List<Period> periods = new ArrayList<>();
        periods.add(period0);
        periods.add(period0B);
        periods.add(period3A);
        periods.add(period3B);
        periods.add(period4A);
        periods.add(period4B);
        periods.add(period5A);
        periods.add(period5B);
        periods.add(period6A);
        periods.add(period6B);

        openingHours.setPeriods(periods);

        List<String> weekday = new ArrayList<>();
        weekday.add("Monday: Closed");
        weekday.add("Tuesday: Closed");
        weekday.add("Wednesday: 12:00 – 2:00 PM, 7:00 – 9:30 PM");
        weekday.add("Thursday: 12:00 – 2:00 PM, 7:00 – 9:30 PM");
        weekday.add("Friday: 12:00 – 2:00 PM, 7:00 – 9:30 PM");
        weekday.add("Saturday: 12:00 – 2:00 PM, 7:00 – 9:30 PM");
        weekday.add("Sunday: 12:00 – 2:00 PM, 7:00 – 9:30 PM");

        openingHours.setWeekdayText(weekday);
        result.setOpeningHours(openingHours);

        result.setPlaceId("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");
        result.setPriceLevel(4);
        result.setRating(4.1);
        result.setVicinity("20 Rue Saint-Martin, Paris");
        result.setWebsite("http://www.benoit-paris.com/");
        result.setReference("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");

        GooglePlacesDetailResult googlePlacesDetailResult = new GooglePlacesDetailResult();
        googlePlacesDetailResult.setResult(result);
        mutableLiveData.postValue(googlePlacesDetailResult);
        return mutableLiveData;
    }
}
