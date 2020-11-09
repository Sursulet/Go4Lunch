package com.sursulet.go4lunch.ui;

//import android.location.Location;

import androidx.lifecycle.LiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sursulet.go4lunch.model.Location;

public class LocationLiveData extends LiveData<Location> {

    private FusedLocationProviderClient client; //= LocationServices.getFusedLocationProviderClient(this);

    protected void onInactive() {
        super.onInactive();
        client.removeLocationUpdates(callback);
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
