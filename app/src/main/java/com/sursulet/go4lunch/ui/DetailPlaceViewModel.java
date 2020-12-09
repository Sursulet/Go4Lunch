package com.sursulet.go4lunch.ui;

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
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

    LiveData<DetailPlaceUiModel> detailPlaceUiModelLiveData;

    public DetailPlaceViewModel(DetailPlaceRepository detailPlaceRepository, WorkmatesRepository workmatesRepository, UserRepository userRepository) {
        this.detailPlaceRepository = detailPlaceRepository;
        this.workmatesRepository = workmatesRepository;
        this.userRepository = userRepository;
    }

    public void startDetailPlace(String id) {
        restaurantId = id;
        Log.d("PEACH", "startDetailPlace: " + id);

        detailPlaceUiModelLiveData = Transformations.map(detailPlaceRepository.getDetailPlace(id),
                new Function<GooglePlacesDetailResult, DetailPlaceUiModel>() {
            @Override
            public DetailPlaceUiModel apply(GooglePlacesDetailResult result) {
                Log.d("PEACH", "apply: " + result.getResult().getPhotos().get(0).getPhotoReference());

                return new DetailPlaceUiModel(
                        result.getResult().getName(),
                        getPhotoOfPlace(result.getResult().getPhotos().get(0).getPhotoReference()),
                        result.getResult().getFormattedAddress()
                );
            }
        });
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

    public void onLikeButtonClick() {
        //detailPlaceRepository.addRestaurantToFavorite(restaurantId, FirebaseAuth.getInstance().getCurrentUser().getUid());
        detailPlaceRepository.addLikeToRestaurant(FirebaseAuth.getInstance().getCurrentUser().getUid(), restaurantId);
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
