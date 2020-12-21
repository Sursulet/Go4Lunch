package com.sursulet.go4lunch.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sursulet.go4lunch.api.UserHelper;
import com.sursulet.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public LiveData<List<User>> getUsers() {
        MutableLiveData<List<User>> mutableLiveData = new MutableLiveData<>();

        UserHelper.getUsersCollection()
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        users.add(documentSnapshot.toObject(User.class));
                    }

                    mutableLiveData.setValue(users);
                });

        return mutableLiveData;
    }

    public LiveData<List<User>> getUsersForRestaurant(String restaurantId) {
        MutableLiveData<List<User>> mutableLiveData = new MutableLiveData<>();

        UserHelper.getUsersCollection()
                .whereEqualTo("isGoingToRestaurant", restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            users.add(documentSnapshot.toObject(User.class));
                        }

                        mutableLiveData.setValue(users);
                    }
                });

        return mutableLiveData;
    }

    //TODO : isGoingToRestaurant && isLikeRestaurant
    public LiveData<Boolean> isGoingToRestaurant(String userId, String restaurantId) {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.postValue(false);

        UserHelper.getUser(userId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            User user = documentSnapshot.toObject(User.class);
                            if(user != null && user.getRestaurant().getId().equals(restaurantId)) {
                                mutableLiveData.postValue(true);
                            }
                        }
                    }
                });

        return mutableLiveData;
    }

    public LiveData<Boolean> isLikeRestaurant(String userId, String restaurantId) {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.postValue(false);
        UserHelper.getUsersCollection()
                .whereArrayContains("likeRestaurant", restaurantId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                User user = documentSnapshot.toObject(User.class);
                                if(user.getUid().equals(userId)) {
                                    mutableLiveData.postValue(true);
                                }
                            }
                        }
                    }
                });

        return mutableLiveData;
    }
}
