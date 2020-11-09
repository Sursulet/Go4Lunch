package com.sursulet.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sursulet.go4lunch.MainViewModel;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;
import com.sursulet.go4lunch.repository.UserRepository;
import com.sursulet.go4lunch.ui.map.MapViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory sFactory;

    private final UserRepository userRepository;
    private final CurrentLocationRepository currentLocationRepository;
    private final NearbyPlacesRepository nearByPlacesRepository;

    private ViewModelFactory(
            UserRepository userRepository,
            CurrentLocationRepository currentLocationRepository,
            NearbyPlacesRepository nearByPlacesRepository
    ) {
        this.userRepository = userRepository;
        this.currentLocationRepository = currentLocationRepository;
        this.nearByPlacesRepository = nearByPlacesRepository;
    }

    public static ViewModelFactory getInstance() {
        if (sFactory == null) {
            synchronized (ViewModelFactory.class) {
                if (sFactory == null) {
                    sFactory = new ViewModelFactory(
                            new UserRepository(),
                            new CurrentLocationRepository(),
                            new NearbyPlacesRepository()
                    );
                }
            }
        }

        return sFactory;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(
                    currentLocationRepository,
                    nearByPlacesRepository);
        } else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(
                    userRepository
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
