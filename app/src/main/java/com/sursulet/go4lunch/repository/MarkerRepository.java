package com.sursulet.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.Marker;

public class MarkerRepository {

    private final MutableLiveData<Marker> markerLiveData = new MutableLiveData<>();

    public LiveData<Marker> getMarkerLiveData() { return markerLiveData; }

    public void displayMarker() { }
}
