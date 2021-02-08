package com.sursulet.go4lunch.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.utils.Utils;
import com.sursulet.go4lunch.injection.ViewModelFactory;
import com.sursulet.go4lunch.ui.detail.DetailPlaceActivity;

import java.util.List;

public class MapFragment extends Fragment {

    private static final String TAG = MapFragment.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    //private final LatLng defaultLocation = new LatLng(48.8534, 2.3488); //Paris
    private static final int DEFAULT_ZOOM = 15;

    MapViewModel mapViewModel;
    GoogleMap map;
    LatLng latLng;
    double latitude;
    double longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mapViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapViewModel.class);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapViewModel.buildLocationRequest();
        mapViewModel.buildLocationCallback();

        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                map = googleMap;
                mapViewModel.onMapReady();

                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ) {
                    map.setMyLocationEnabled(true);
                }

                map.setOnMarkerClickListener(marker -> {
                    if (marker.getSnippet() != null && !marker.getSnippet().isEmpty()) {
                        mapViewModel.openDetailPlaceActivity(marker.getSnippet());
                    }
                    return true;
                });
            });
        }

        mapViewModel.getLastLocation().observe(getViewLifecycleOwner(), this::updatelastLocation);
        mapViewModel.getMapUiModelLiveData().observe(getViewLifecycleOwner(), this::updateUi);
        mapViewModel.getSingleLiveEventOpenDetailActivity().observe(getViewLifecycleOwner(), this::openDetailActivity);

        return v;
    }

    private void openDetailActivity(String id) {
        requireActivity().startActivity(DetailPlaceActivity.getStartIntent(requireActivity(), id));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            mapViewModel.getStartLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapViewModel.getStopLocationUpdates();
    }

    private void updatelastLocation(Location location) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if(map != null){
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        }
    }

    public void updateUi(List<MapUiModel> models) {
        latitude = 0;
        longitude = 0;
        for (MapUiModel model : models) {
            map.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(model.getLat(), model.getLng()))
                            .title(model.getName())
                            .icon(Utils.bitmapDescriptorFromVector(model.getIcon(), requireContext()))
                            .snippet(model.getPlaceId())
            );
        }
    }

    private boolean checkPermissions() {
        return (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
        );

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackBar(R.string.permission_rationale,
                    android.R.string.ok, view -> startLocationPermissionRequest());
        } else {
            Log.i(TAG, "Requesting permission");
            startLocationPermissionRequest();
        }
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
                requireActivity(),
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                REQUEST_PERMISSIONS_REQUEST_CODE
        );
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                    mapViewModel.buildLocationRequest();
                    mapViewModel.buildLocationCallback();
                    mapViewModel.getStartLocationUpdates();
                }
            }
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackBar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
}