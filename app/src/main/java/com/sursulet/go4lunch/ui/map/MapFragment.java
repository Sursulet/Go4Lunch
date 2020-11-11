package com.sursulet.go4lunch.ui.map;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.injection.ViewModelFactory;
import com.sursulet.go4lunch.model.GooglePlacesNearbySearchResult;
import com.sursulet.go4lunch.model.Result;

import java.util.List;

public class MapFragment extends Fragment {

    MapViewModel mapViewModel;

    GoogleMap map;
    Boolean mapReady = false;
    List<Result> places;
    LatLng latLng;
    String name;
    Marker marker;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            mapReady = true;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mapViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapViewModel.class);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        /* invoquer pour obtenir l'emplacement du mobile : currentLocation */
        mapViewModel.getMapUiModelLiveData().observe(this, new Observer<MapUiModel>() {
            @Override
            public void onChanged(MapUiModel mapUiModel) {
                places = mapUiModel.getPlaces();

                //double lat = mapUiModel.getLat(); double lng = mapUiModel.getLng();
            }
        });

        for(Result place : places) {
            MarkerOptions markerOptions = new MarkerOptions();

            double lat = place.getGeometry().getLocation().getLat();
            double lng = place.getGeometry().getLocation().getLng();

            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng)
                    .title(name);

            map.addMarker(markerOptions);
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(15));

        }

    }
}