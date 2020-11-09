package com.sursulet.go4lunch.ui.map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.model.GooglePlacesNearbySearchResult;
import com.sursulet.go4lunch.remote.IGoogleAPIService;
import com.sursulet.go4lunch.remote.RetrofitClient;
import com.sursulet.go4lunch.ui.DetailsActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment {

    private static final int PERMISSION_CODE = 1000;

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location lastLocation;
    double currentLatitude = 0, currentLongitude = 0;
    private Marker mMarker;

    IGoogleAPIService mService;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;

            if(ContextCompat.checkSelfPermission(
                    getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                map.setMyLocationEnabled(true);
            }

            /*LatLng paris = new LatLng(48.8534, 2.3488);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(paris, 15));
            map.addMarker(new MarkerOptions().position(paris).title("ME"));*/
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(paris));

            map.setOnMarkerClickListener(marker -> {

                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("Marker", marker.getTag().toString());
                startActivity(intent);
                return true;
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }


        mService = RetrofitClient.getClient("https://maps.googleapis.com/")
                .create(IGoogleAPIService.class);
        //mService = Common.getGoogleAPIService();

        getLocationPermission();

        getNearByPlaces();

        createLocationCallback();
        createLocationRequest();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } catch (SecurityException e) {
            Log.e("Exception %s: ", e.getMessage());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_CODE);

        }
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                lastLocation = locationResult.getLastLocation();

                if(mMarker != null) mMarker.remove();

                currentLatitude = lastLocation.getLatitude();
                currentLongitude = lastLocation.getLongitude();

                LatLng latLng = new LatLng(currentLatitude, currentLongitude);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("ME")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMarker = map.addMarker(markerOptions);

                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                map.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        };
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getCurrentLocation() {
        try {
            Task<Location> task = fusedLocationClient.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    lastLocation = location;
                    mapFragment.getMapAsync(googleMap -> {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        MarkerOptions options = new MarkerOptions().position(latLng).title("ME");


                        Log.d("PEACH", "Location: " + latLng.latitude + " , " + latLng.longitude);
                    });
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception %s: ", e.getMessage());
        }
    }

    private void getNearByPlaces() {
        //map.clear();

        mService.getNearByPlaces(
                //"AIzaSyDvUeXTbuq87mNoavyfSj_1AWVOK_dMyiE",
                getResources().getString(R.string.google_api_key),
                "48.8534,2.3488",
                "restaurant",
                "500"
        ).enqueue(new Callback<GooglePlacesNearbySearchResult>() {
            @Override
            public void onResponse(Call<GooglePlacesNearbySearchResult> call, Response<GooglePlacesNearbySearchResult> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getResults() != null) {
                        for (int i = 0; i < response.body().getResults().size(); i++) {
                            Log.d("COURGETTE", "onResponse: " + response.body().getResults().get(i).getName());

                            MarkerOptions markerOptions = new MarkerOptions();
                            //Results place = response.body().getResults()[i];

                            double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                            double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();

                            String name = response.body().getResults().get(i).getName();
                            LatLng latlng = new LatLng(lat, lng);
                            markerOptions.position(latlng)
                                    .title(name)
                                    .icon(getVectorImage());

                            /*
                            if() { //is booked
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fork_and_knife_in_cross));
                            } else { //is not
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fork_and_knife_in_cross));
                            }*/

                            map.addMarker(markerOptions);
                            map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                            map.animateCamera(CameraUpdateFactory.zoomTo(15));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GooglePlacesNearbySearchResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private BitmapDescriptor getVectorImage() {
        VectorDrawable vectorDrawable = (VectorDrawable) ContextCompat.getDrawable(getContext(), R.drawable.ic_fork_and_knife_in_cross);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                    createLocationCallback();
                    createLocationRequest();

                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                }
            }
        }
    }
}