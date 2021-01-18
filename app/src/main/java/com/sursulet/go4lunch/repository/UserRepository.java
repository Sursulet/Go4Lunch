package com.sursulet.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sursulet.go4lunch.api.UserHelper;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserRepository {

    //private static final String TAG = UserRepository.class.getSimpleName();
    MutableLiveData<String> selectedQuery = new MutableLiveData<>();

    public LiveData<User> getUser(String uid) {
        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();
        UserHelper.getUser(uid).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        User user = document.toObject(User.class);
                        mutableLiveData.postValue(user);
                    }
                });
        return mutableLiveData;
    }

    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> mutableLiveData = new MutableLiveData<>();

        UserHelper.getUsersCollection()
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        User user = documentSnapshot.toObject(User.class);
                        if(!(user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
                            users.add(user);
                        }
                    }

                    mutableLiveData.setValue(users);
                });

        return mutableLiveData;
    }


    //TODO: A modifier
    public LiveData<Set<String>> getActiveRestaurants() {
        MutableLiveData<Set<String>> mutableLiveData = new MutableLiveData<>();

        UserHelper.getUsersCollection()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Set<String> activeRestaurants = new HashSet<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            User currentUser = documentSnapshot.toObject(User.class);
                            /*if (currentUser.getRestaurant() != null) {
                                activeRestaurants.add(currentUser.getRestaurant().getId());
                            }*/
                        }

                        mutableLiveData.setValue(activeRestaurants);
                    }
                });

        return mutableLiveData;
    }

    public void addRestaurant(String restaurantId, String restaurantName, String uid) {
        Restaurant restaurant = new Restaurant(restaurantId, restaurantName);
        UserHelper.updateRestaurant(restaurant, uid);
    }

    public void removeRestaurant(String uid) {
        UserHelper.deleteRestaurant(uid);
    }

    public void setSelectedQuery(String text) {
        selectedQuery.setValue(text);
    }

    public LiveData<String> getSelectedQuery() {
        return selectedQuery;
    }
}
