package com.sursulet.go4lunch.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.injection.ViewModelFactory;

import java.util.List;

public class MapFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    MapViewModel mapViewModel;

    GoogleMap map;

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
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;

                    // TODO Stephanie à déplacer dans le ViewModel : il faut considérer le "map ready" comme une LiveData
                    //  qui va déclencher "l'écoute" du LocationRepository qui va déclencher... (etc, etc)
                    mapViewModel.onMapReady();

                    map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(48.8534,2.3488)));
                    map.animateCamera(CameraUpdateFactory.zoomTo(15));
                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            mapViewModel.launchDetailPlaceActivity(marker.getSnippet());
                            return true;
                        }
                    });
                }
            });
        }

        /* invoquer pour obtenir l'emplacement du mobile : currentLocation */
        mapViewModel.getMapUiModelLiveData().observe(getViewLifecycleOwner(), new Observer<List<MapUiModel>>() {
            @Override
            public void onChanged(List<MapUiModel> models) {
                updateUi(models);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mapViewModel.checkPermission();
    }

    public void updateUi(List<MapUiModel> models) {
        for (MapUiModel model : models) {
            map.addMarker(
                new MarkerOptions()
                    .position(new LatLng(model.getLat(), model.getLng()))
                    .title(model.getName())
                    .snippet(model.getPlaceId())
            );
        }
    }
}