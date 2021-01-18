package com.sursulet.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sursulet.go4lunch.api.ActiveRestaurantHelper;
import com.sursulet.go4lunch.api.LikeRestaurantHelper;
import com.sursulet.go4lunch.model.Geometry;
import com.sursulet.go4lunch.model.NearbyResult;
import com.sursulet.go4lunch.model.OpeningHours;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestaurantRepository {

    private static final String TAG = RestaurantRepository.class.getSimpleName();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = auth.getCurrentUser();

    // --- CREATE ---

    public void createActiveRestaurant(String restaurantId, String restaurantName, String uid, String username, String urlPicture) {
        ActiveRestaurantHelper.createActiveRestaurant(restaurantId, restaurantName, uid, username, urlPicture)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    public void createLikeRestaurant(String restaurantId, String restaurantName, String uid, String username, String urlPicture) {
        LikeRestaurantHelper.createLikeRestaurant(restaurantId, restaurantName, uid, username, urlPicture)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.d(TAG, "DocumentSnapshot successfully written!"));
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

    public LiveData<Boolean> isBooking(String id) {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        ActiveRestaurantHelper.getBooking(id, firebaseUser.getUid())
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

    public LiveData<List<Restaurant>> getAllActiveRestaurants() {
        MutableLiveData<List<Restaurant>> mutableLiveData = new MutableLiveData<>();
        ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Restaurant> restaurants = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Restaurant restaurant = document.toObject(Restaurant.class);
                            restaurants.add(restaurant);
                        }
                        mutableLiveData.postValue(restaurants);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
        return mutableLiveData;
    }

    public LiveData<Set<String>> getAllActiveRestaurantsIds() {
        MutableLiveData<Set<String>> mutableLiveData = new MutableLiveData<>();
        ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Set<String> activeRestaurantsIds = new HashSet<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Restaurant restaurant = document.toObject(Restaurant.class);
                            activeRestaurantsIds.add(restaurant.getId());
                        }
                        mutableLiveData.postValue(activeRestaurantsIds);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
        return mutableLiveData;
    }

    public LiveData<List<User>> getAllBookings(String id) {
        MutableLiveData<List<User>> mutableLiveData = new MutableLiveData<>();
        ActiveRestaurantHelper.getBookingsCollection(id)
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

    public LiveData<Restaurant> hasActiveRestaurantBooking(String uid) {
        MutableLiveData<Restaurant> mutableLiveData = new MutableLiveData<>();
        ActiveRestaurantHelper.getAllBookingsCollection()
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> ids = new ArrayList<>();

                    for(QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        Log.d(TAG, snap.getId() + " => " + snap.getData()  + " => " + snap.getReference().getPath());
                    }
                    mutableLiveData.postValue(new Restaurant("1", "WHAT"));
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mutableLiveData.postValue(new Restaurant("0", "FAKE"));
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

    //Get Like button status
    public LiveData<Boolean> isFollower(String id) {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();

        LikeRestaurantHelper.getLikeRestaurantsCollection()
                .document(id)
                .addSnapshotListener((snapshot, e) -> {
                    if(e != null) {
                        Log.w(TAG, "onEvent: ", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        mutableLiveData.postValue(true);
                    } else {
                        Log.d(TAG, "Current data: null");
                        mutableLiveData.postValue(false);
                    }
                });
        /*
                .getFollower(id, firebaseUser.getUid())
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

         */

        return mutableLiveData;
    }

    // -- DELETE --
    public void deleteBooking(String id, String uid) {
        ActiveRestaurantHelper.deleteBooking(id, uid)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            } else {
                                Log.w(TAG, "Error deleting document", task.getException());
                            }
                        });
    }

    public void deleteFollower(String id, String uid) {
        LikeRestaurantHelper.deleteFollower(id, uid)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            } else {
                                Log.w(TAG, "Error deleting document", task.getException());
                            }
                        });
    }

    public LiveData<List<NearbyResult>> init() {
        MutableLiveData<List<NearbyResult>> mutableLiveData = new MutableLiveData<>();
        List<NearbyResult> nearbyResults = new ArrayList<>();
        NearbyResult nearbyResult = new NearbyResult();
        nearbyResult.setBusinessStatus("OPERATIONAL");
        Geometry geometry = new Geometry();
        com.sursulet.go4lunch.model.Location location = new com.sursulet.go4lunch.model.Location();
        location.setLat(48.858397);
        location.setLng(2.3501027);
        geometry.setLocation(location);
        nearbyResult.setGeometry(geometry);
        nearbyResult.setName("Benoit Paris");
        nearbyResult.setIcon("https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/restaurant-71.png");
        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenNow(false);
        nearbyResult.setOpeningHours(openingHours);
        nearbyResult.setPlaceId("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");
        nearbyResult.setPriceLevel(4);
        nearbyResult.setRating(4.1);
        nearbyResult.setReference("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");
        nearbyResult.setVicinity("20 Rue Saint-Martin, Paris");

        nearbyResults.add(nearbyResult);
        mutableLiveData.postValue(nearbyResults);
        return mutableLiveData;
    }

    public LiveData<String> getFakeName(String uid) {
        MutableLiveData<String > mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue("Benoit Paris");
        return mutableLiveData;
    }
}
