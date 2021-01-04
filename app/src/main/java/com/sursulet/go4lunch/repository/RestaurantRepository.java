package com.sursulet.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sursulet.go4lunch.api.ActiveRestaurantHelper;
import com.sursulet.go4lunch.api.LikeRestaurantHelper;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository {

    private static final String TAG = RestaurantRepository.class.getSimpleName();

    // --- CREATE ---
    public void createActiveRestaurant(String restaurantId, String restaurantName, String uid, String username, String urlPicture) {
        ActiveRestaurantHelper.createActiveRestaurant(restaurantId, restaurantName, uid, username, urlPicture);
    }

    public void createLikeRestaurant(String restaurantId, String restaurantName, String uid, String username, String urlPicture) {
        LikeRestaurantHelper.createLikeRestaurant(restaurantId, restaurantName, uid, username, urlPicture);
    }

    // --- GET ---
    public LiveData<Restaurant> getActiveRestaurant(String id) {
        MutableLiveData<Restaurant> mutableLiveData = new MutableLiveData<>();
        ActiveRestaurantHelper.getActiveRestaurant(id)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            Restaurant restaurant = document.toObject(Restaurant.class);
                            mutableLiveData.postValue(restaurant);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });

        return mutableLiveData;
    }

    public LiveData<Restaurant> getLikeRestaurant(String id) {
        MutableLiveData<Restaurant> mutableLiveData = new MutableLiveData<>();
        LikeRestaurantHelper.getLikeRestaurant(id)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            Restaurant restaurant = document.toObject(Restaurant.class);
                            mutableLiveData.postValue(restaurant);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });

        return mutableLiveData;
    }

    public LiveData<Boolean> getUserActiveRestaurant(String id, String uid) {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        ActiveRestaurantHelper.getUserActiveRestaurant(id, uid)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            mutableLiveData.postValue(true);
                        } else {
                            Log.d(TAG, "No such document");
                            mutableLiveData.postValue(false);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
        return mutableLiveData;
    }

    public LiveData<Boolean> getUserLikeRestaurant(String id, String uid) {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();

        LikeRestaurantHelper.getUserLikeRestaurant(id, uid)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    mutableLiveData.postValue(true);
                                } else {
                                    Log.d(TAG, "No such document");
                                    mutableLiveData.postValue(false);
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        });

        return mutableLiveData;
    }

    public LiveData<List<User>> getUsersActiveRestaurant(String id) {
        MutableLiveData<List<User>> mutableLiveData = new MutableLiveData<>();
        ActiveRestaurantHelper.getUsersCollectionActiveRestaurant(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            User user = document.toObject(User.class);
                            users.add(user);
                        }
                        mutableLiveData.setValue(users);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        return mutableLiveData;
    }

    // -- DELETE --
    public void deleteUserActiveRestaurant(String id, String uid) {
        ActiveRestaurantHelper.deleteUserActiveRestaurant(id, uid)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            } else {
                                Log.w(TAG, "Error deleting document", task.getException());
                            }
                        });
    }

    public void deleteUserLikeRestaurant(String id, String uid) {
        LikeRestaurantHelper.deleteUserLikeRestaurant(id, uid)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            } else {
                                Log.w(TAG, "Error deleting document", task.getException());
                            }
                        });
    }
}
