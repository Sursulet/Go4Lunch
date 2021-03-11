package com.sursulet.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;
import com.sursulet.go4lunch.api.ActiveRestaurantHelper;
import com.sursulet.go4lunch.api.LikeRestaurantHelper;
import com.sursulet.go4lunch.api.UserHelper;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository {

    private static final String TAG = RestaurantRepository.class.getSimpleName();

    private final FirebaseAuth firebaseAuth;

    public RestaurantRepository(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    // --- GET ---

    public LiveData<Boolean> isFollowed(String restaurantId) {

        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        if (firebaseAuth.getCurrentUser() != null) {
            LikeRestaurantHelper.getFollowersCollection(restaurantId)
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .addSnapshotListener(
                            (value, e) -> {
                                if (e != null) {
                                    Log.w(TAG, "Listen failed.", e);
                                    return;
                                }

                                if (value != null && value.exists()) {
                                    Log.d(TAG, "Current data: " + value.getData());
                                    mutableLiveData.postValue(true);
                                } else {
                                    Log.d(TAG, "Current data: null");
                                    mutableLiveData.postValue(false);
                                }
                            });
        }

        return mutableLiveData;
    }

    public LiveData<Boolean> isBooked(String restaurantId) {

        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        if (firebaseAuth.getCurrentUser() != null) {
            ActiveRestaurantHelper.getBookingsCollection(restaurantId)
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .addSnapshotListener(
                            (value, e) -> {
                                if (e != null) {
                                    Log.w(TAG, "Listen failed.", e);
                                    return;
                                }

                                if (value != null && value.exists()) {
                                    Log.d(TAG, "Current data: " + value.getData());
                                    mutableLiveData.postValue(true);
                                } else {
                                    Log.d(TAG, "Current data: null");
                                    mutableLiveData.postValue(false);
                                }

                            });
        }

        return mutableLiveData;
    }

    public LiveData<Restaurant> getActiveRestaurant(String restaurantId) {
        MutableLiveData<Restaurant> mutableLiveData = new MutableLiveData<>();
        ActiveRestaurantHelper.getActiveRestaurant(restaurantId)
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

    public LiveData<Restaurant> getActiveRestaurantBooking(String restaurantId) {

        MutableLiveData<Restaurant> mutableLiveData = new MutableLiveData<>();
        if (firebaseAuth.getCurrentUser() != null) {
            ActiveRestaurantHelper.getActiveRestaurantBooking(restaurantId, firebaseAuth.getCurrentUser().getUid())
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
        }

        return mutableLiveData;
    }

    public LiveData<List<String>> getAllActiveRestaurantsIds() {

        MutableLiveData<List<String>> mutableLiveData = new MutableLiveData<>();
        UserHelper.getLockupCollection()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> ids = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Restaurant restaurant = document.toObject(Restaurant.class);
                            ids.add(restaurant.getId());
                        }
                        mutableLiveData.setValue(ids);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }

                });

        return mutableLiveData;
    }

    public LiveData<List<User>> getActiveRestaurantAllBookings(String id) {
        MutableLiveData<List<User>> mutableLiveData = new MutableLiveData<>();
        if (firebaseAuth.getCurrentUser() != null) {
            ActiveRestaurantHelper.getBookingsCollection(id)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<User> users = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                User user = document.toObject(User.class);
                                if (!(user.getUid().equals(firebaseAuth.getCurrentUser().getUid()))) {
                                    users.add(user);
                                }
                            }
                            mutableLiveData.setValue(users);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }

        return mutableLiveData;
    }

    public LiveData<Restaurant> getActiveRestaurantFromLockup(String userId) {
        MutableLiveData<Restaurant> mutableLiveData = new MutableLiveData<>();

        FirebaseFirestore.getInstance().collection(LocalDate.now() + "_UsersActiveRestaurants")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            Restaurant restaurant = document.toObject(Restaurant.class);
                            if (restaurant != null) {
                                mutableLiveData.postValue(restaurant);
                            }

                        } else {
                            Log.d(TAG, "No such document");
                        }

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }

                });

        return mutableLiveData;
    }


    public void onGoingButtonClick(
            String restaurantId,
            String restaurantName,
            String restaurantAddress
    ) {
        if (firebaseAuth.getCurrentUser() != null) {

            DocumentReference booDocRef = ActiveRestaurantHelper.getBookingsCollection(restaurantId)
                    .document(firebaseAuth.getCurrentUser().getUid());

            DocumentReference lockup = FirebaseFirestore.getInstance()
                    .collection(LocalDate.now() + "_UsersActiveRestaurants")
                    .document(firebaseAuth.getCurrentUser().getUid());

            FirebaseFirestore.getInstance().runTransaction(
                    (Transaction.Function<Void>) transaction -> {
                        DocumentSnapshot snapshot = transaction.get(lockup);

                        if (snapshot.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + snapshot.getData());

                            Restaurant restaurant = snapshot.toObject(Restaurant.class);
                            if (restaurant != null) {
                                String placeId = restaurant.getId();

                                DocumentReference DocRef = ActiveRestaurantHelper.getBookingsCollection(placeId)
                                        .document(firebaseAuth.getCurrentUser().getUid());

                                transaction.delete(DocRef);
                                transaction.delete(lockup);

                                if (!placeId.equals(restaurantId)) {
                                    Restaurant restaurantToCreate = new Restaurant(restaurantId, restaurantName, restaurantAddress);

                                    String photo = (firebaseAuth.getCurrentUser().getPhotoUrl() != null)
                                            ? firebaseAuth.getCurrentUser().getPhotoUrl().toString()
                                            : null;

                                    User userToCreate = new User(
                                            firebaseAuth.getCurrentUser().getUid(),
                                            firebaseAuth.getCurrentUser().getEmail(),
                                            photo);

                                    transaction.set(lockup, restaurantToCreate);
                                    transaction.set(booDocRef, userToCreate);
                                }
                            }
                        } else {
                            Log.d(TAG, "No such document");

                            Restaurant restaurantToCreate = new Restaurant(restaurantId, restaurantName, restaurantAddress);

                            String photo = (firebaseAuth.getCurrentUser().getPhotoUrl() != null)
                                    ? firebaseAuth.getCurrentUser().getPhotoUrl().toString()
                                    : null;

                            User userToCreate = new User(
                                    firebaseAuth.getCurrentUser().getUid(),
                                    firebaseAuth.getCurrentUser().getEmail(),
                                    photo);

                            transaction.set(lockup, restaurantToCreate);
                            transaction.set(booDocRef, userToCreate);
                        }

                        return null;
                    })
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Transaction success!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Transaction failure.", e));
        }

    }

    public void onLikeButtonClick(String restaurantId) {
        if (firebaseAuth.getCurrentUser() != null) {
            DocumentReference sfDocRef = LikeRestaurantHelper.getFollowersCollection(restaurantId)
                    .document(firebaseAuth.getCurrentUser().getUid());

            FirebaseFirestore.getInstance().runTransaction(
                    (Transaction.Function<Void>) transaction -> {
                        DocumentSnapshot snapshot = transaction.get(sfDocRef);

                        if (snapshot.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + snapshot.getData());
                            transaction.delete(sfDocRef);
                        } else {
                            Log.d(TAG, "No such document");
                            User userToCreate = new User(
                                    firebaseAuth.getCurrentUser().getUid(),
                                    firebaseAuth.getCurrentUser().getEmail(),
                                    null);
                            transaction.set(sfDocRef, userToCreate);
                        }

                        return null;
                    })
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Transaction success!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Transaction failure.", e));
        }
    }
}
