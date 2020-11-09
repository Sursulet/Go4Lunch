package com.sursulet.go4lunch.remote;

import com.sursulet.go4lunch.model.GooglePlacesNearbySearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGoogleAPIService {

    @GET("maps/api/place/nearbysearch/json")
    Call<GooglePlacesNearbySearchResult> getNearByPlaces(
            @Query("key") String key,
            @Query("location") String location,
            @Query("type") String type,
            @Query("radius") String radius
    );
}
