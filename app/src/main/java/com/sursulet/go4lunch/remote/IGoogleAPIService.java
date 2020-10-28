package com.sursulet.go4lunch.remote;

import com.sursulet.go4lunch.model.Places;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleAPIService {

    @GET
    Call<Places> getNearByPlaces(@Url String url);

    @GET
    Call<Places> getDetailsPlace(@Url String url);
}
