package com.sursulet.go4lunch.repository;

import android.location.Location;

import com.sursulet.go4lunch.BuildConfig;
import com.sursulet.go4lunch.model.autocomplete.GooglePlacesAutocompleteResult;
import com.sursulet.go4lunch.model.autocomplete.Prediction;
import com.sursulet.go4lunch.remote.IGoogleAPIService;
import com.sursulet.go4lunch.remote.RetrofitClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class AutocompleteRepository {

    final IGoogleAPIService mService = RetrofitClient.getClient("https://maps.googleapis.com/")
            .create(IGoogleAPIService.class);

    public List<Prediction> getAutocompleteByLocation(String input, Location location) {

        List<Prediction> predictions = null;
        try {
            Response<GooglePlacesAutocompleteResult> response = mService.getAutocompletePlaces(
                    BuildConfig.GOOGLE_PLACES_KEY,
                     input,
                    location.getLatitude() + "," + location.getLongitude(),
                    "50",
                    "establishment"
            ).execute();
            if(response.isSuccessful()) {
                predictions = response.body().getPredictions();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return predictions;
    }

}
