package com.sursulet.go4lunch.ui.workmates;

import android.graphics.Typeface;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.SingleLiveEvent;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesViewModel extends ViewModel {

    private final UserRepository userRepository;

    private final SingleLiveEvent<String> eventOpenChatActivity = new SingleLiveEvent<>();

    public WorkmatesViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<List<WorkmatesUiModel>> getWorkmatesUiModelLiveData() {
        return Transformations.map(userRepository.getUsers(), users -> {
            List<WorkmatesUiModel> results = new ArrayList<>();

            for (User user : users) {
                Restaurant place = user.getRestaurant();

                String txt; int style;
                if (place != null) {
                    txt = " is eating (" + place.getName() + ")";
                    style = Typeface.BOLD;
                } else {
                    txt = " hasn't decided yet ";
                    style = Typeface.ITALIC;
                }

                String sentence = user.getUsername() + txt;

                WorkmatesUiModel workmatesUiModel = new WorkmatesUiModel(
                        user.getUid(),
                        sentence,
                        user.getAvatarUrl(),
                        style
                );

                results.add(workmatesUiModel);
            }

            return results;
        });
    }

    public SingleLiveEvent<String> getEventOpenChatActivity() { return eventOpenChatActivity; }
    public void openChatActivity(String id) {
        eventOpenChatActivity.setValue(id);
    }
}
