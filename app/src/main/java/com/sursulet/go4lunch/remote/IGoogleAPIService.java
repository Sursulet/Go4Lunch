package com.sursulet.go4lunch.remote;

import com.sursulet.go4lunch.model.nearby.GooglePlacesNearbySearchResult;
import com.sursulet.go4lunch.model.autocomplete.GooglePlacesAutocompleteResult;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;

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

    @GET("maps/api/place/details/json")
    Call<GooglePlacesDetailResult> getDetailPlace(
            @Query("place_id") String place_id,
            //@Query("fields") String fields,
            @Query("key") String key
    );

    @GET("maps/api/place/autocomplete/json")
    Call<GooglePlacesAutocompleteResult> getAutocompletePlaces(
            @Query("key") String key,
            @Query("input") String input,
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("types") String types
    );
}
