package com.sursulet.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.remote.IGoogleAPIService;
import com.sursulet.go4lunch.remote.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPlaceRepository {

    GooglePlacesDetailResult googlePlacesDetailResult;

    IGoogleAPIService mService = RetrofitClient
            .getClient("https://maps.googleapis.com/")
            .create(IGoogleAPIService.class);

    public LiveData<GooglePlacesDetailResult> getDetailPlace(String place_id) {

        MutableLiveData<GooglePlacesDetailResult> placeMutableLiveData = new MutableLiveData<>();

        mService.getDetailPlace(
                place_id,
                "" //TODO : KEY
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

    public Response<GooglePlacesDetailResult> getDetailPlaceSync(String place_id) throws IOException {

        return mService.getDetailPlace(
                place_id,
                "" //TODO : KEY
        ).execute();

    }

}
