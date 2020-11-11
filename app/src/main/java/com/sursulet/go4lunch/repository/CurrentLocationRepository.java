package com.sursulet.go4lunch.repository;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class CurrentLocationRepository {

    private Context context;

    private final MutableLiveData<Location> mutableLiveData = new MutableLiveData<>();

    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public LiveData<Location> getLocation() { return mutableLiveData; }

    //public CurrentLocationRepository(Context context) { this.context = context; }
    public void init () {
        createLocationRequest();
        createLocationCallback();

        client = LocationServices.getFusedLocationProviderClient(context);

        try {
            client.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );

        } catch (SecurityException e) {
            Log.e("Exception %s: ", e.getMessage());
        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        };
    }

}
