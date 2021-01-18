package com.sursulet.go4lunch.ui.workmates;

import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.SingleLiveEvent;
import com.sursulet.go4lunch.model.Restaurant;
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

    private final SingleLiveEvent<String> eventOpenChatActivity = new SingleLiveEvent<>();

    MediatorLiveData<List<WorkmatesUiModel>> uiModelMutableLiveData = new MediatorLiveData<>();
    MediatorLiveData<Map<String, String>> restaurantNameMediatorLiveData = new MediatorLiveData<>();

    private final List<String> alreadyRequiredIds = new ArrayList<>();

    public WorkmatesViewModel(
            @NonNull UserRepository userRepository,
            @NonNull RestaurantRepository restaurantRepository
    ) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;

        LiveData<List<User>> usersLiveData = userRepository.getAllUsers();
        restaurantNameMediatorLiveData.setValue(new HashMap<>());

        uiModelMutableLiveData.addSource(
                usersLiveData,
                users -> combine(users, restaurantNameMediatorLiveData.getValue()));

        uiModelMutableLiveData.addSource(
                restaurantNameMediatorLiveData,
                stringStringMap -> combine(usersLiveData.getValue(), stringStringMap));
    }

    public LiveData<List<WorkmatesUiModel>> getWorkmatesUiModelLiveData() {
        return uiModelMutableLiveData;
    }

    public void combine(List<User> users, Map<String, String> restaurantNameMap) {
        if (users == null) return;

        List<WorkmatesUiModel> results = new ArrayList<>();

        for (User user : users) {
            String existingRestaurantName = restaurantNameMap.get(user.getUid());
            if(existingRestaurantName == null) {
                if(!alreadyRequiredIds.contains(user.getUid())) {
                    alreadyRequiredIds.add(user.getUid());
                    restaurantNameMediatorLiveData.addSource(
                            restaurantRepository.getFakeName(user.getUid()),
                            name -> {
                                //TODO: remplacer cette section par setNameRestaurant ?
                                Map<String, String> existingMap = restaurantNameMediatorLiveData.getValue();
                                assert existingMap != null;
                                existingMap.put(user.getUid(), name);
                                restaurantNameMediatorLiveData.setValue(existingMap);
                            });
                }
            }
            //if(!(user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
            Restaurant place = null;//user.getRestaurant();

            String txt;
            int style;
            if (existingRestaurantName != null) {
                txt = " is eating (" + existingRestaurantName + ")";
                style = Typeface.BOLD;
            } else {
                txt = " hasn't decided yet";
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
            //}
        }

        uiModelMutableLiveData.setValue(results);

    }

    public SingleLiveEvent<String> getEventOpenChatActivity() {
        return eventOpenChatActivity;
    }

    public void openChatActivity(String id) {
        eventOpenChatActivity.setValue(id);
    }

    @VisibleForTesting
    public void setRestaurantNameMediatorLiveData(String uid, String name) {
        Map<String, String> nameMap = restaurantNameMediatorLiveData.getValue();
        //assert nameMap != null;
        nameMap.put(uid, name);
        this.restaurantNameMediatorLiveData.setValue(nameMap);
    }
}
