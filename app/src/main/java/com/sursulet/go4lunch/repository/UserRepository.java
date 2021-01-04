package com.sursulet.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sursulet.go4lunch.api.ActiveRestaurantHelper;
import com.sursulet.go4lunch.api.UserHelper;
import com.sursulet.go4lunch.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserRepository {

    private static final String TAG = UserRepository.class.getSimpleName();

    public User getUser(String uid) {
        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();
        UserHelper.getUser(uid).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        User user = document.toObject(User.class);
                        mutableLiveData.postValue(user);
                    }
                });
        return mutableLiveData.getValue();
    }

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
                            if (currentUser.getRestaurant() != null) {
                                activeRestaurants.add(currentUser.getRestaurant().getId());
                            }
                        }

                        mutableLiveData.setValue(activeRestaurants);
                    }
                });

        return mutableLiveData;
    }

    public LiveData<List<User>> getUsersForRestaurant(String restaurantId) {
        MutableLiveData<List<User>> mutableLiveData = new MutableLiveData<>();

        UserHelper.getUsersCollection()
                .whereEqualTo("isGoingToRestaurant", restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
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
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null && user.getRestaurant().getId().equals(restaurantId)) {
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
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                User user = documentSnapshot.toObject(User.class);
                                if (user.getUid().equals(userId)) {
                                    mutableLiveData.postValue(true);
                                }
                            }
                        }
                    }
                });

        return mutableLiveData;
    }

    public LiveData<Integer> getActiveRestaurantAsNumber(String userId) {
        MutableLiveData<Integer> mutableLiveData = new MutableLiveData<>();

        ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(userId).collection("restaurants").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mutableLiveData.postValue(task.getResult().size());
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }

                    }
                });

        return mutableLiveData;
    }

    public LiveData<List<User>> getUsersActiveRestaurant(String id) {
        MutableLiveData<List<User>> mutableLiveData = new MutableLiveData<>();
        ActiveRestaurantHelper.getActiveRestaurant(id)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                mutableLiveData.postValue(getUsersIsGoing(document));
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
        return mutableLiveData;
    }

    public List<User> getUsersIsGoing(DocumentSnapshot documentSnapshot) {
        List<User> users = new ArrayList<>();
        documentSnapshot.getReference().collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                users.add(document.toObject(User.class));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return users;
    }
}
