package com.sursulet.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.sursulet.go4lunch.api.ActiveRestaurantHelper;
import com.sursulet.go4lunch.api.LikeRestaurantHelper;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository {

    private static final String TAG = RestaurantRepository.class.getSimpleName();

    private final FirebaseAuth firebaseAuth;

    public RestaurantRepository(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    // --- CREATE ---

    public void createActiveRestaurant(String restaurantId, String restaurantName, String uid, String username, String urlPicture) {
        ActiveRestaurantHelper.createActiveRestaurant(restaurantId, restaurantName, uid, username, urlPicture);
    }

    public void createLikeRestaurant(String restaurantId, String restaurantName, String uid, String username, String urlPicture) {
        //LikeRestaurantHelper.createLikeRestaurant(restaurantId, restaurantName, uid, username, urlPicture);
        LikeRestaurantHelper.getLikeRestaurantsCollection().document(restaurantId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        LikeRestaurantHelper.deleteLikeRestaurantBooking(restaurantId, uid);
                    } else {
                        Log.d(TAG, "Current data: null");
                        LikeRestaurantHelper.createLikeRestaurant(restaurantId, restaurantName, uid, username, urlPicture);
                    }

                });
    }

    // --- GET ---

    public LiveData<Boolean> isFollowing(String restaurantId) {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
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
        /*
        LikeRestaurantHelper.getLikeRestaurantFollower(restaurantId, firebaseAuth.getCurrentUser().getUid())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            mutableLiveData.postValue(true);
                        } else {
                            Log.d(TAG, "No such document");
                            mutableLiveData.setValue(false);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });

         */

        return mutableLiveData;
    }

    public LiveData<Boolean> isBooking(String restaurantId) {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
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
        /*
        ActiveRestaurantHelper.getActiveRestaurantBooking(restaurantId, firebaseAuth.getCurrentUser().getUid())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            mutableLiveData.setValue(true);
                        } else {
                            Log.d(TAG, "No such document");
                            mutableLiveData.setValue(false);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }

                });

         */

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
        return mutableLiveData;
    }

    public LiveData<List<String>> getAllActiveRestaurantsIds() {
        MutableLiveData<List<String>> mutableLiveData = new MutableLiveData<>();
        ActiveRestaurantHelper.getActiveRestaurantsCollection()
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

        return mutableLiveData;
    }

    public LiveData<String> getNameActiveRestaurant(String userId) {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

        FirebaseFirestore.getInstance()
                .collectionGroup("bookings")
                .whereEqualTo("uid", userId)
                .get()
                .continueWithTask((Continuation<QuerySnapshot, Task<List<DocumentSnapshot>>>) task -> {
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, "Continue" + document.getId() + " => " + document.getData());
                        tasks.add(document.getReference().getParent().getParent().get());
                    }
                    return Tasks.whenAllSuccess(tasks);
                })
                .addOnCompleteListener(task -> {
                    List<String> names = new ArrayList<>();
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        Log.d(TAG, snapshot.getId() + " => " + snapshot.getData().get("name"));
                        Restaurant restaurant = snapshot.toObject(Restaurant.class);
                        names.add(restaurant.getName());
                    }
                    if (names.size() != 0) {
                        mutableLiveData.postValue(names.get(0));
                    }
                });

        return mutableLiveData;
    }

    public LiveData<String> getActiveRestaurantId(String userId) {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

        ActiveRestaurantHelper.getActiveRestaurantId(userId)
                .addOnCompleteListener(task -> {
                    List<String> tasks = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, "onComplete" + document.getId() + " => " + document.getData());
                        Log.d(TAG, "getActiveRestaurantId: " + document.getReference().getParent().getParent().getId());
                        tasks.add(document.getReference().getParent().getParent().getId());
                    }

                    if (tasks.size() != 0) {
                        mutableLiveData.postValue(tasks.get(0));
                    }
                });

        return mutableLiveData;
    }

    // -- DELETE --
    public void deleteActiveRestaurantBooking(String id, String uid) {
        ActiveRestaurantHelper.deleteActiveRestaurantBooking(id, uid)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            } else {
                                Log.w(TAG, "Error deleting document", task.getException());
                            }
                        });
    }

    public void deleteLikeRestaurantBooking(String id, String uid) {
        LikeRestaurantHelper.deleteLikeRestaurantBooking(id, uid)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            } else {
                                Log.w(TAG, "Error deleting document", task.getException());
                            }
                        });
    }

    public void onGoingButtonClick(String restaurantId) {
        DocumentReference sfDocRef = ActiveRestaurantHelper.getBookingsCollection(restaurantId)
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
        /*
        ActiveRestaurantHelper.getBookingsCollection(restaurantId)
                .document(firebaseAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (value != null && value.exists()) {
                            Log.d(TAG, "Current data: " + value.getData());
                            ActiveRestaurantHelper.getBookingsCollection(restaurantId)
                                    .document(firebaseAuth.getCurrentUser().getUid()).delete();
                        } else {
                            Log.d(TAG, "Current data: null");
                            User userToCreate = new User(
                                    firebaseAuth.getCurrentUser().getUid(),
                                    firebaseAuth.getCurrentUser().getEmail(),
                                    null);
                            ActiveRestaurantHelper.getBookingsCollection(restaurantId)
                                    .document(firebaseAuth.getCurrentUser().getUid()).set(userToCreate);
                        }

                    }
                });

         */
    }

    public void onLikeButtonClick(String restaurantId) {
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
