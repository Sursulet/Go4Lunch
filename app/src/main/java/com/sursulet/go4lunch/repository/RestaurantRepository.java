package com.sursulet.go4lunch.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.api.ActiveRestaurantHelper;
import com.sursulet.go4lunch.api.LikeRestaurantHelper;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantRepository {

    private static final String TAG = RestaurantRepository.class.getSimpleName();

    private final Application application;
    private final FirebaseAuth firebaseAuth;

    public RestaurantRepository(Application application, FirebaseAuth firebaseAuth) {
        this.application = application;
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

    public LiveData<Boolean> hasBooking() {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        if (firebaseAuth.getCurrentUser() != null) {
            ActiveRestaurantHelper.getBooking(firebaseAuth.getCurrentUser().getUid())
                    .addOnCompleteListener(task -> {
                        List<User> tasks = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, "onComplete" + document.getId() + " => " + document.getData());
                            tasks.add(document.toObject(User.class));
                        }

                        if (tasks.size() != 0 && tasks.get(0) != null) {
                            mutableLiveData.postValue(true);
                        }
                    });
        }

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

        FirebaseFirestore.getInstance().collection(LocalDate.now() + "_UsersActiveRestaurants")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            Map<String, Object> map = document.getData();
                            assert map != null;
                            String placeId = map.get("restaurantId").toString();

                            ActiveRestaurantHelper.getActiveRestaurant(placeId)
                                    .addOnCompleteListener(snapshotTask -> {
                                        if (snapshotTask.isSuccessful()) {
                                            DocumentSnapshot snapshot = snapshotTask.getResult();
                                            if (snapshot.exists()) {
                                                Restaurant restaurant = snapshot.toObject(Restaurant.class);
                                                mutableLiveData.postValue(restaurant.getName());
                                            }
                                        }
                                    });

                        } else {
                            Log.d(TAG, "No such document");
                            mutableLiveData.postValue("");
                        }

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }

                });



        /*
        ActiveRestaurantHelper.getBooking(userId)
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

         */

        return mutableLiveData;
    }

    public LiveData<String> getDetailPlaceName(String placeId) {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

        Places.initialize(
                application.getApplicationContext(),
                application.getResources().getString(R.string.google_maps_key));

        PlacesClient placesClient = Places.createClient(application.getApplicationContext());

        // Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Log.i(TAG, "Place found: " + place.getName());
            mutableLiveData.postValue(place.getName());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });

        return mutableLiveData;
    }

    public LiveData<String> getNamePlace(String userId) {
        MediatorLiveData<String> mediatorLiveData = new MediatorLiveData<>();
        MediatorLiveData<String> restaurantNameLiveData = new MediatorLiveData<>();
        LiveData<String> restaurantIdLiveData = getActiveRestaurantId(userId);

        mediatorLiveData.addSource(restaurantIdLiveData, new Observer<String>() {
            @Override
            public void onChanged(String placeId) {

                Log.d("PEACH", "onChanged: " + placeId);

                Places.initialize(
                        application.getApplicationContext(),
                        application.getResources().getString(R.string.desc_restaurant));

                PlacesClient placesClient = Places.createClient(application.getApplicationContext());

                // Specify the fields to return.
                final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

                // Construct a request object, passing the place ID and fields array.
                final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place place = response.getPlace();
                    Log.i("PEACH", "Place found: " + place.getName());
                    mediatorLiveData.postValue(place.getName());
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e("PEACH", "Place not found: " + exception.getMessage());
                        final int statusCode = apiException.getStatusCode();
                        // TODO: Handle error with given status code.
                    }
                });

            }
        });

        return mediatorLiveData;
    }


    public void onGoingButtonClick(
            String restaurantId,
            String restaurantName,
            String restaurantAddress
    ) {
        if (firebaseAuth.getCurrentUser() != null) {

            DocumentReference arDocRef = ActiveRestaurantHelper.getActiveRestaurantsCollection()
                    .document(restaurantId);

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

                            Map<String, Object> map = snapshot.getData();
                            assert map != null;
                            String placeId = map.get("restaurantId").toString();

                            DocumentReference DocRef = ActiveRestaurantHelper.getBookingsCollection(placeId)
                                    .document(firebaseAuth.getCurrentUser().getUid());

                            transaction.delete(DocRef);
                            transaction.delete(lockup);

                            if (!placeId.equals(restaurantId)) {
                                Restaurant restaurantToCreate = new Restaurant(restaurantId, restaurantName, restaurantAddress);

                                String photo = (firebaseAuth.getCurrentUser().getPhotoUrl() != null) ? firebaseAuth.getCurrentUser().getPhotoUrl().toString() : null;
                                User userToCreate = new User(
                                        firebaseAuth.getCurrentUser().getUid(),
                                        firebaseAuth.getCurrentUser().getEmail(),
                                        photo);

                                Map<String, Object> fields = new HashMap<>();
                                fields.put("restaurantId", restaurantId);

                                transaction.set(lockup, fields);
                                transaction.set(booDocRef, userToCreate);
                                transaction.set(arDocRef, restaurantToCreate);
                            }
                        } else {
                            Log.d(TAG, "No such document");

                            Restaurant restaurantToCreate = new Restaurant(restaurantId, restaurantName, restaurantAddress);

                            String photo = (firebaseAuth.getCurrentUser().getPhotoUrl() != null) ? firebaseAuth.getCurrentUser().getPhotoUrl().toString() : null;
                            User userToCreate = new User(
                                    firebaseAuth.getCurrentUser().getUid(),
                                    firebaseAuth.getCurrentUser().getEmail(),
                                    photo);

                            Map<String, Object> fields = new HashMap<>();
                            fields.put("restaurantId", restaurantId);

                            transaction.set(lockup, fields);
                            transaction.set(booDocRef, userToCreate);
                            transaction.set(arDocRef, restaurantToCreate);
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
