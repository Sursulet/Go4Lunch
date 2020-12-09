package com.sursulet.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sursulet.go4lunch.api.UserHelper;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.remote.IGoogleAPIService;
import com.sursulet.go4lunch.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPlaceRepository {

    IGoogleAPIService mService = RetrofitClient
            .getClient("https://maps.googleapis.com/")
            .create(IGoogleAPIService.class);

    public LiveData<GooglePlacesDetailResult> getDetailPlace(String place_id) {

        MutableLiveData<GooglePlacesDetailResult> placeMutableLiveData = new MutableLiveData<>();

        mService.getDetailPlace(
                place_id,
                "AIzaSyDvUeXTbuq87mNoavyfSj_1AWVOK_dMyiE" //getResources().getString(R.string.google_api_key),
        ).enqueue(new Callback<GooglePlacesDetailResult>() {
            @Override
            public void onResponse(
                    Call<GooglePlacesDetailResult> call,
                    Response<GooglePlacesDetailResult> response
            ) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        placeMutableLiveData.postValue(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<GooglePlacesDetailResult> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return placeMutableLiveData;
    }


    public void addRestaurantToFavorite(String restaurantId, String userId) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId).get().continueWithTask(new Continuation<DocumentSnapshot, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                        return FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(userId)
                                .update("likeRestaurant", FieldValue.arrayUnion(restaurantId));
                    }
                });
    }

    public void addLikeToRestaurant(String userId, String restaurantId) {
        UserHelper.getUsersCollection()
                .document(userId)
                .collection("Likes")
                .add(new Restaurant(restaurantId, userId));
    }

    public CollectionReference getLikeRestaurant(String userId) {
        return UserHelper.getUsersCollection()
                .document(userId)
                .collection("Likes");
    }

    // --- DELETE ---
    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}
