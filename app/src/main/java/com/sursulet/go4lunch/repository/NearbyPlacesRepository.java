package com.sursulet.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sursulet.go4lunch.model.GooglePlacesNearbySearchResult;
import com.sursulet.go4lunch.model.NearbyResult;
import com.sursulet.go4lunch.remote.IGoogleAPIService;
import com.sursulet.go4lunch.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyPlacesRepository {

    IGoogleAPIService mService = RetrofitClient.getClient("https://maps.googleapis.com/").create(IGoogleAPIService.class);

    /**
     * @return a LiveData containing the nearby restaurant around the GPS position
     */
    public LiveData<List<NearbyResult>> getNearByPlaces(double latitude, double longitude) {

        MutableLiveData<List<NearbyResult>> places = new MutableLiveData<>();
        mService.getNearByPlaces(
                "", //TODO : KEY
                latitude + "," + longitude,
                "restaurant",
                "500"
        ).enqueue(new Callback<GooglePlacesNearbySearchResult>() {
            @Override
            public void onResponse(
                    Call<GooglePlacesNearbySearchResult> call,
                    Response<GooglePlacesNearbySearchResult> response
            ) {

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getNearbyResults() != null) {
                        places.setValue(response.body().getNearbyResults());
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
