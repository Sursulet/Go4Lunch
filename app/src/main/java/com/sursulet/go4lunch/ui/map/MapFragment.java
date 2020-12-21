package com.sursulet.go4lunch.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.sursulet.go4lunch.BuildConfig;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.injection.ViewModelFactory;
import com.sursulet.go4lunch.ui.DetailPlaceActivity;

import java.util.List;

public class MapFragment extends Fragment {

    private static final String TAG = MapFragment.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    // A default location (Paris, France) and default zoom to use when location permission is not granted.
    private final LatLng defaultLocation = new LatLng(48.8534, 2.3488);
    private static final int DEFAULT_ZOOM = 15;

    MapViewModel mapViewModel;
    GoogleMap map;
    double latitude;
    double longitude;

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

        mapViewModel.getStartLocationUpdates();

        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                map = googleMap;
                /*map.setMinZoomPreference(6.0f);
                map.setMaxZoomPreference(14.0f);

                //TODO : Put a default location
                mapViewModel.getLastLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
                    @Override
                    public void onChanged(Location location) {
                        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                        map.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
                    }
                });*/

                //map.setMyLocationEnabled(true);
                mapViewModel.onMapReady();
                map.setOnMarkerClickListener(marker -> {
                    if(marker.getSnippet() != null && !marker.getSnippet().isEmpty()){
                        mapViewModel.launchDetailPlaceActivity(marker.getSnippet());
                    }
                    return true;
                });
            });
        }

        /* invoquer pour obtenir l'emplacement du mobile : currentLocation */
        mapViewModel.getMapUiModelLiveData().observe(getViewLifecycleOwner(), new Observer<List<MapUiModel>>() {
            @Override
            public void onChanged(List<MapUiModel> models) {
                updateUi(models);
            }
        });

        mapViewModel.getSingleLiveEventLaunchDetailActivity().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String id) {
                requireActivity().startActivity(DetailPlaceActivity.getStartIntent(requireActivity(), id));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            mapViewModel.getStartLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Remove location updates to save battery.
        mapViewModel.getStopLocationUpdates();
    }

    public void updateUi(List<MapUiModel> models) {
        for (MapUiModel model : models) {
            map.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(model.getLat(), model.getLng()))
                            .title(model.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .snippet(model.getPlaceId())
            );

            latitude = latitude + model.getLat();
            longitude = longitude + model.getLng();
        }
        //TODO : J'ai fait la moyenne de la latitude et de la longitude
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude/models.size(), longitude/models.size())));
        map.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
    }

    private boolean checkPermissions() {
        //TODO: Modification add setMyLocationEnabled
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
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, view -> {
                        // Request permission
                        startLocationPermissionRequest();
                    });
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
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                mapViewModel.getStartLocationUpdates();
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, view -> {
                            // Build intent that displays the App settings screen.
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
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
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
}