package com.sursulet.go4lunch.ui;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationLiveData extends LiveData<Location> {

    private String TAG = "LocationLiveData";

    private Context context;
    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location currentLocation;

    LocationLiveData(Context context) {
        this.context = context;
    }

    protected void onInactive() {
        super.onInactive();
        client.removeLocationUpdates(callback);
    }

    private void createLocationCallback(Context ctx) {
        client = LocationServices.getFusedLocationProviderClient(ctx);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.e(TAG, "onLocationResult: " + locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };
    }

    protected void onActive() {
        super.onActive();/*
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

            }
        });*/

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        //client.requestLocationUpdates(locationRequest, callback, null);
    }

    private LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
/*
            for (location in locationResult.getLocations()) {
                setLocationData(location);
            }*/
        }
    };

    private void setLocationData(Location location) {
        //return LoDe;
    }
}
