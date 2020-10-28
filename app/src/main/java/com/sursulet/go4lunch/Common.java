package com.sursulet.go4lunch;

import com.sursulet.go4lunch.model.Places;
import com.sursulet.go4lunch.remote.IGoogleAPIService;
import com.sursulet.go4lunch.remote.RetrofitClient;

public class Common {

    private static final String GOOGLE_API_URL = "https://maps/googleapis.com/";

    public Places currentPlace;

    public static IGoogleAPIService getGoogleAPIService() {
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
}
