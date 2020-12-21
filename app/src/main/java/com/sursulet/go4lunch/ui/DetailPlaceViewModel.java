package com.sursulet.go4lunch.ui;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.model.details.Result;
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

        //placesDetailResultLiveData = detailPlaceRepository.getDetailPlace(id);
        placesDetailResultLiveData = getDPlace(id);
        LiveData<Integer> isGoingToPlaceLiveData = Transformations.switchMap(
                userRepository.isGoingToRestaurant(FirebaseAuth.getInstance().getCurrentUser().getUid(), restaurantId),
                new Function<Boolean, LiveData<Integer>>() {
                    @Override
                    public LiveData<Integer> apply(Boolean isGoing) {
                        MutableLiveData<Integer> mutableLiveData = new MutableLiveData<>();
                        int color = (isGoing) ? R.color.primary : R.color.secondary;
                        mutableLiveData.setValue(color);
                        return mutableLiveData;
                    }
                }
        );

        LiveData<Integer> isLikePlaceLiveData = Transformations.switchMap(
                userRepository.isLikeRestaurant(FirebaseAuth.getInstance().getCurrentUser().getUid(), restaurantId),
                new Function<Boolean, LiveData<Integer>>() {
                    @Override
                    public LiveData<Integer> apply(Boolean isLike) {
                        MutableLiveData<Integer> mutableLiveData = new MutableLiveData<>();
                        int color = (isLike) ? R.color.primary : R.color.secondary;
                        mutableLiveData.setValue(color);
                        return mutableLiveData;
                    }
                });
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

        uiModelMediatorLiveData.addSource(isGoingToPlaceLiveData, new Observer<Integer>() {
            @Override
            public void onChanged(Integer isGoing) {
                combine(placesDetailResultLiveData.getValue(), isGoing, isLikePlaceLiveData.getValue(), workmatesUiLiveData.getValue());
            }
        });

        uiModelMediatorLiveData.addSource(isLikePlaceLiveData, new Observer<Integer>() {
            @Override
            public void onChanged(Integer isLike) {
                combine(placesDetailResultLiveData.getValue(), isGoingToPlaceLiveData.getValue(), isLike, workmatesUiLiveData.getValue());
            }
        });

        uiModelMediatorLiveData.addSource(workmatesUiLiveData, new Observer<List<WorkmatesUiModel>>() {
            @Override
            public void onChanged(List<WorkmatesUiModel> workmatesUiModels) {
                combine(placesDetailResultLiveData.getValue(), isGoingToPlaceLiveData.getValue(), isLikePlaceLiveData.getValue(), workmatesUiModels);
            }
        });

        uiModelMediatorLiveData.addSource(placesDetailResultLiveData, new Observer<GooglePlacesDetailResult>() {
            @Override
            public void onChanged(GooglePlacesDetailResult result) {
                combine(result, isGoingToPlaceLiveData.getValue(), isLikePlaceLiveData.getValue(), workmatesUiLiveData.getValue());
            }
        });
    }

    private void combine(GooglePlacesDetailResult resultFromServer, Integer isGoing, Integer isLike,@Nullable List<WorkmatesUiModel> workmates) {
        if(resultFromServer == null || isGoing == null || isLike == null || workmatesUiLiveData == null) {
            return;
        }

        restaurantName = resultFromServer.getResult().getName();

        uiModelMediatorLiveData.setValue(new DetailPlaceUiModel(
                restaurantName,
                null,//getPhotoOfPlace(resultFromServer.getResult().getPhotos().get(0).getPhotoReference()),
                isGoing,
                resultFromServer.getResult().getFormattedAddress(),
                getRating(resultFromServer.getResult().getRating()),
                resultFromServer.getResult().getFormattedPhoneNumber(),
                isLike,
                resultFromServer.getResult().getWebsite(),
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
        url.append("&key="+ null); //TODO : KEY

        return url.toString();
    }

    private String getRating(double rating) {
        rating = Math.round(rating);

        return String.valueOf(rating);
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

    private MutableLiveData<GooglePlacesDetailResult> getDPlace(String id) {
        MutableLiveData<GooglePlacesDetailResult> mutableLiveData = new MutableLiveData<>();

        Result result = new Result();
        result.setName("Toto");
        result.setFormattedAddress("16 rue");
        result.setRating(1.1);
        result.setFormattedPhoneNumber("0606");
        result.setWebsite("www.google.com");

        GooglePlacesDetailResult googlePlacesDetailResult = new GooglePlacesDetailResult();
        googlePlacesDetailResult.setResult(result);

        mutableLiveData.postValue(googlePlacesDetailResult);
        return mutableLiveData;
    }
}
