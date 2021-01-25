package com.sursulet.go4lunch.ui.workmates;

import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.SingleLiveEvent;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.repository.RestaurantRepository;
import com.sursulet.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkmatesViewModel extends ViewModel {

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final RestaurantRepository restaurantRepository;

    MediatorLiveData<List<WorkmatesUiModel>> uiModelMutableLiveData = new MediatorLiveData<>();
    MediatorLiveData<Map<String, String>> nameRestaurantLiveData = new MediatorLiveData<>();
    LiveData<List<User>> usersLiveData;

    private final List<String> alreadyRequiredIds = new ArrayList<>();

    private final SingleLiveEvent<String> eventOpenChatActivity = new SingleLiveEvent<>();

    public WorkmatesViewModel(
            @NonNull UserRepository userRepository,
            @NonNull RestaurantRepository restaurantRepository
    ) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;

        nameRestaurantLiveData.setValue(new HashMap<>());

        usersLiveData = userRepository.getAllUsers();

        uiModelMutableLiveData.addSource(
                usersLiveData,
                userList -> combine(userList, nameRestaurantLiveData.getValue()));

        uiModelMutableLiveData.addSource(
                nameRestaurantLiveData,
                stringStringMap -> combine(usersLiveData.getValue(), stringStringMap));
    }

    private void combine(
            @Nullable List<User> users,
            @NonNull Map<String, String> nameRestaurantMap
    ) {
        if (users == null) return;

        List<WorkmatesUiModel> uiStateList = new ArrayList<>();

        for (User user : users) {
            String existingNameRestaurant = nameRestaurantMap.get(user.getUid());

            if (existingNameRestaurant == null) {
                if (!alreadyRequiredIds.contains(user.getUid())) {
                    alreadyRequiredIds.add(user.getUid());
                    nameRestaurantLiveData.addSource(
                            restaurantRepository.getNameActiveRestaurant(user.getUid()),
                            nameRestaurant -> {
                                Map<String, String> existingMap = nameRestaurantLiveData.getValue();
                                existingMap.put(user.getUid(), nameRestaurant);
                                nameRestaurantLiveData.setValue(existingMap);
                            });
                }
            }

            String txt;
            int style;
            if (existingNameRestaurant != null) {
                txt = " is eating (" + existingNameRestaurant + ")";
                style = Typeface.BOLD;
            } else {
                txt = " hasn't decided yet";
                style = Typeface.ITALIC;
            }

            String sentence = user.getUsername() + txt;

            uiStateList.add(
                    new WorkmatesUiModel(
                            user.getUid(),
                            sentence,
                            user.getAvatarUrl(),
                            style
                    ));
        }

        if(!uiStateList.isEmpty()) {
            uiModelMutableLiveData.setValue(uiStateList);
        }
    }

    public LiveData<List<WorkmatesUiModel>> getWorkmatesUiModelLiveData() {
        return uiModelMutableLiveData;
    }

    public SingleLiveEvent<String> getEventOpenChatActivity() {
        return eventOpenChatActivity;
    }

    public void openChatActivity(String id) {
        eventOpenChatActivity.setValue(id);
    }
}
