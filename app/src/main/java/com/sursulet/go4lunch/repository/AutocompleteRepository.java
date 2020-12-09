package com.sursulet.go4lunch.repository;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sursulet.go4lunch.model.GooglePlacesNearbySearchResult;
import com.sursulet.go4lunch.model.Result;
import com.sursulet.go4lunch.model.autocomplete.GooglePlacesAutocompleteResult;
import com.sursulet.go4lunch.model.autocomplete.Prediction;
import com.sursulet.go4lunch.remote.IGoogleAPIService;
import com.sursulet.go4lunch.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutocompleteRepository {

    IGoogleAPIService mService = RetrofitClient.getClient("https://maps.googleapis.com/")
            .create(IGoogleAPIService.class);

    public LiveData<List<Prediction>> getAutocompleteByLocation(String input, Location location) {

        MutableLiveData<List<Prediction>> places = new MutableLiveData<>();

        mService.getAutocompletePlaces(
                "AIzaSyDvUeXTbuq87mNoavyfSj_1AWVOK_dMyiE",
                input,
                location.getLatitude() + "," + location.getLongitude(),
                "500",
                "establishment"
        ).enqueue(new Callback<GooglePlacesAutocompleteResult>() {
            @Override
            public void onResponse(Call<GooglePlacesAutocompleteResult> call, Response<GooglePlacesAutocompleteResult> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getPredictions() != null) {
                        places.setValue(response.body().getPredictions());
                    }
                }
            }

            @Override
            public void onFailure(Call<GooglePlacesAutocompleteResult> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return places;
    }
}
