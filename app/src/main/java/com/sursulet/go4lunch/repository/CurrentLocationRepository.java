package com.sursulet.go4lunch.repository;

import android.app.Application;
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

/**
 * This class is the entry point to be notified about GPS change
 */
public class CurrentLocationRepository {

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();

    public LiveData<Location> getLocationLiveData() {
        return locationMutableLiveData;
    }

    // Inject application
    public CurrentLocationRepository(Application application) {
        LocationRequest locationRequest = new LocationRequest()
            .setInterval(10_000)
            .setFastestInterval(5_000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(application);

        try {
            client.requestLocationUpdates(
                locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        locationMutableLiveData.postValue(locationResult.getLastLocation());
                    }
                },
                Looper.getMainLooper()
            );
        } catch (SecurityException e) {
            Log.e("Exception %s: ", e.getMessage());
        }
    }
}
