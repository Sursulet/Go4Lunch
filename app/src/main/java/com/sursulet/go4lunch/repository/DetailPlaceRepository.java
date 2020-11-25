package com.sursulet.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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


}
