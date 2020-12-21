package com.sursulet.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.sursulet.go4lunch.MainApplication;
import com.sursulet.go4lunch.MainViewModel;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.DetailPlaceRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;
import com.sursulet.go4lunch.repository.UserRepository;
import com.sursulet.go4lunch.repository.WorkmatesRepository;
import com.sursulet.go4lunch.ui.DetailPlaceViewModel;
import com.sursulet.go4lunch.ui.list.ListViewModel;
import com.sursulet.go4lunch.ui.map.MapViewModel;
import com.sursulet.go4lunch.ui.workmates.WorkmatesViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory sFactory;

    private final UserRepository userRepository;
    private final CurrentLocationRepository currentLocationRepository;
    private final NearbyPlacesRepository nearbyPlacesRepository;
    private final DetailPlaceRepository detailPlaceRepository;
    private final WorkmatesRepository workmatesRepository;

    private ViewModelFactory(
            UserRepository userRepository,
            CurrentLocationRepository currentLocationRepository,
            NearbyPlacesRepository nearByPlacesRepository,
            DetailPlaceRepository detailPlaceRepository, WorkmatesRepository workmatesRepository
    ) {
        this.userRepository = userRepository;
        this.currentLocationRepository = currentLocationRepository;
        this.nearbyPlacesRepository = nearByPlacesRepository;
        this.detailPlaceRepository = detailPlaceRepository;
        this.workmatesRepository = workmatesRepository;
    }

    public static ViewModelFactory getInstance() {
        if (sFactory == null) {
            synchronized (ViewModelFactory.class) {
                if (sFactory == null) {
                    sFactory = new ViewModelFactory(
                            new UserRepository(),
                            new CurrentLocationRepository(
                                MainApplication.getApplication()
                            ),
                            new NearbyPlacesRepository(),
                            new DetailPlaceRepository(),
                            new WorkmatesRepository()
                    );
                }
            }
        }

        return sFactory;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(
                    MainApplication.getApplication(),
                    currentLocationRepository,
                    nearbyPlacesRepository,
                    userRepository);
        } else if (modelClass.isAssignableFrom(ListViewModel.class)) {
            return (T) new ListViewModel(
                    currentLocationRepository,
                    nearbyPlacesRepository,
                    detailPlaceRepository,
                    userRepository
            );
        } else if (modelClass.isAssignableFrom(WorkmatesViewModel.class)) {
            return (T) new WorkmatesViewModel(
                    userRepository,
                    detailPlaceRepository
            );
        } else if (modelClass.isAssignableFrom(DetailPlaceViewModel.class)) {
            return (T) new DetailPlaceViewModel(
                    detailPlaceRepository,
                    workmatesRepository,
                    userRepository
            );
        } else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(
                    MainApplication.getApplication(),
                    userRepository,
                    FirebaseAuth.getInstance()
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
