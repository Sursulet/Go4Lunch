package com.sursulet.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sursulet.go4lunch.model.GooglePlacesNearbySearchResult;
import com.sursulet.go4lunch.model.Result;
import com.sursulet.go4lunch.remote.IGoogleAPIService;
import com.sursulet.go4lunch.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyPlacesRepository {

    IGoogleAPIService mService = RetrofitClient.getClient("https://maps.googleapis.com/")
            .create(IGoogleAPIService.class);

    private final MutableLiveData<Integer> nearbyPlacesLiveData = new MutableLiveData<>();

    public LiveData<List<Result>> getNearByPlaces() {

        MutableLiveData<List<Result>> places = new MutableLiveData<>();
        mService.getNearByPlaces(
                "AIzaSyDvUeXTbuq87mNoavyfSj_1AWVOK_dMyiE", //getResources().getString(R.string.google_api_key),
                "48.8534,2.3488",
                "restaurant",
                "500"
        ).enqueue(new Callback<GooglePlacesNearbySearchResult>() {
            @Override
            public void onResponse(Call<GooglePlacesNearbySearchResult> call, Response<GooglePlacesNearbySearchResult> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getResults() != null) {
                        places.setValue(response.body().getResults());
                    }
                }
            }

            @Override
            public void onFailure(Call<GooglePlacesNearbySearchResult> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return places;
    }
}
