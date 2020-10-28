package com.sursulet.go4lunch.ui.map;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.model.GooglePlacesNearbySearchResult;
import com.sursulet.go4lunch.remote.IGoogleAPIService;
import com.sursulet.go4lunch.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment {

    private static final int PERMISSION_CODE = 1000;
    SupportMapFragment mapFragment;

    FusedLocationProviderClient fusedLocationClient;
    double currentLatitude = 0, currentLongitude = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialization
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        //Initialize fused location provider client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        IGoogleAPIService mService = RetrofitClient.getClient("https://maps.googleapis.com/")
                .create(IGoogleAPIService.class);
        mService.getNearByPlaces(
                "AIzaSyCrjCEYFwsO2biAp-GJw8MO6HbpjC5Tbzw",
                "48.8534,2.3488",
                "restaurant",
                "500"
        ).enqueue(new Callback<GooglePlacesNearbySearchResult>() {
            @Override
            public void onResponse(Call<GooglePlacesNearbySearchResult> call, Response<GooglePlacesNearbySearchResult> response) {
                Log.d("COURGETTE", "onResponse: " + response.body().getResults().get(0).getName());
            }

            @Override
            public void onFailure(Call<GooglePlacesNearbySearchResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
            return;
        }

        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    if (mapFragment != null) {
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                LatLng latLng = new LatLng(currentLatitude, currentLongitude);
                                MarkerOptions options = new MarkerOptions().position(latLng)
                                        .title("I am here");
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                googleMap.addMarker(options);
                            }
                        });
                    }
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}