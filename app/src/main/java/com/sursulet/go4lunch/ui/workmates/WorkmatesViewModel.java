package com.sursulet.go4lunch.ui.workmates;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.repository.DetailPlaceRepository;
import com.sursulet.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final DetailPlaceRepository detailPlaceRepository;

    public WorkmatesViewModel(
            UserRepository userRepository,
            DetailPlaceRepository detailPlaceRepository
    ) {
        this.userRepository = userRepository;
        this.detailPlaceRepository = detailPlaceRepository;
    }

    public LiveData<List<WorkmatesUiModel>> getWorkmatesUiModelLiveData() {
        return Transformations.map(userRepository.getUsers(), new Function<List<User>, List<WorkmatesUiModel>>() {
            @Override
            public List<WorkmatesUiModel> apply(List<User> users) {
                List<WorkmatesUiModel> results = new ArrayList<>();

                for (User user : users) {
                    Restaurant place = user.getRestaurant();

                    String sentence = (place != null) ?
                            user.getUsername() + " is eating (" + place.getName() + ")"
                            : user.getUsername() + " hasn't decided yet ";

                    WorkmatesUiModel workmatesUiModel = new WorkmatesUiModel(
                            user.getUid(),
                            sentence,
                            user.getAvatarUrl()
                    );

                    results.add(workmatesUiModel);
                }

                return results;
            }
        });
    }
}
