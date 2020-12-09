package com.sursulet.go4lunch.repository;

import android.app.Application;
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

    private final Application application;

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();

    private boolean initialized;
    FusedLocationProviderClient client;
    LocationCallback locationCallback;

    // Inject application
    public CurrentLocationRepository(Application application) {
        this.application = application;
    }

    public void startLocationUpdates() {
        if (!initialized) {
            initialized = true;

            client = LocationServices.getFusedLocationProviderClient(application);

            LocationRequest locationRequest = new LocationRequest()
                .setInterval(10_000)
                .setFastestInterval(5_000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    locationMutableLiveData.postValue(locationResult.getLastLocation());
                }
            };

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
    }

    public void stopLocationUpdates() {
        client.removeLocationUpdates(locationCallback);
    }

    public LiveData<Location> getLocationLiveData() {
        return locationMutableLiveData;
    }
}
