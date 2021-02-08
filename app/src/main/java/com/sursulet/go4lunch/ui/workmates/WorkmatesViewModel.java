package com.sursulet.go4lunch.ui.workmates;

import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.utils.SingleLiveEvent;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.repository.RestaurantRepository;
import com.sursulet.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkmatesViewModel extends ViewModel {

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
        this.restaurantRepository = restaurantRepository;

        nameRestaurantLiveData.setValue(new HashMap<>());
        usersLiveData = userRepository.getAllUsers();

        uiModelMutableLiveData.addSource(
                usersLiveData,
                userList -> combine(
                        userList,
                        nameRestaurantLiveData.getValue()));

        uiModelMutableLiveData.addSource(
                nameRestaurantLiveData,
                stringStringMap -> combine(
                        usersLiveData.getValue(),
                        stringStringMap));

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
                            restaurantRepository.getActiveRestaurantId(user.getUid()),
                            nameRestaurant -> {
                                Map<String, String> existingMap = nameRestaurantLiveData.getValue();
                                assert existingMap != null;
                                existingMap.put(user.getUid(), nameRestaurant);
                                nameRestaurantLiveData.setValue(existingMap);
                            }
                    );
                }
            } else {

                String txt;
                int style;

                if (!existingNameRestaurant.isEmpty()) {
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

                //uiStateList.sort(Comparator.comparing(WorkmatesUiModel::getTxtStyle));
                Collections.sort(uiStateList, (o1, o2) -> Integer.compare(o1.getTxtStyle(), o2.getTxtStyle()));


            }
        }

        if (!uiStateList.isEmpty()) {
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
