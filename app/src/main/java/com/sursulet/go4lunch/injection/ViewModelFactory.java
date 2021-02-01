package com.sursulet.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.sursulet.go4lunch.MainApplication;
import com.sursulet.go4lunch.MainViewModel;
import com.sursulet.go4lunch.repository.AutocompleteRepository;
import com.sursulet.go4lunch.repository.ChatRepository;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.DetailPlaceRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;
import com.sursulet.go4lunch.repository.RestaurantRepository;
import com.sursulet.go4lunch.repository.UserRepository;
import com.sursulet.go4lunch.ui.detail.DetailPlaceViewModel;
import com.sursulet.go4lunch.ui.chat.ChatViewModel;
import com.sursulet.go4lunch.ui.list.ListViewModel;
import com.sursulet.go4lunch.ui.map.MapViewModel;
import com.sursulet.go4lunch.ui.workmates.WorkmatesViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory sFactory;

    private final UserRepository userRepository;
    private final CurrentLocationRepository currentLocationRepository;
    private final NearbyPlacesRepository nearbyPlacesRepository;
    private final DetailPlaceRepository detailPlaceRepository;
    private final RestaurantRepository restaurantRepository;
    private final AutocompleteRepository autocompleteRepository;
    private final ChatRepository chatRepository;

    private ViewModelFactory(
            UserRepository userRepository,
            CurrentLocationRepository currentLocationRepository,
            NearbyPlacesRepository nearByPlacesRepository,
            DetailPlaceRepository detailPlaceRepository,
            RestaurantRepository restaurantRepository,
            AutocompleteRepository autocompleteRepository,
            ChatRepository chatRepository) {
        this.userRepository = userRepository;
        this.currentLocationRepository = currentLocationRepository;
        this.nearbyPlacesRepository = nearByPlacesRepository;
        this.detailPlaceRepository = detailPlaceRepository;
        this.autocompleteRepository = autocompleteRepository;
        this.restaurantRepository = restaurantRepository;
        this.chatRepository = chatRepository;
    }

    public static ViewModelFactory getInstance() {
        if (sFactory == null) {
            synchronized (ViewModelFactory.class) {
                if (sFactory == null) {
                    sFactory = new ViewModelFactory(
                            new UserRepository(MainApplication.getApplication(), FirebaseAuth.getInstance()),
                            new CurrentLocationRepository(MainApplication.getApplication()),
                            new NearbyPlacesRepository(),
                            new DetailPlaceRepository(),
                            new RestaurantRepository(FirebaseAuth.getInstance()),
                            new AutocompleteRepository(),
                            new ChatRepository(MainApplication.getApplication(), FirebaseAuth.getInstance()));
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
                    currentLocationRepository,
                    nearbyPlacesRepository,
                    userRepository,
                    restaurantRepository
            );
        } else if (modelClass.isAssignableFrom(ListViewModel.class)) {
            return (T) new ListViewModel(
                    currentLocationRepository,
                    nearbyPlacesRepository,
                    detailPlaceRepository,
                    userRepository,
                    restaurantRepository);
        } else if (modelClass.isAssignableFrom(WorkmatesViewModel.class)) {
            return (T) new WorkmatesViewModel(
                    userRepository,
                    restaurantRepository);
        } else if (modelClass.isAssignableFrom(DetailPlaceViewModel.class)) {
            return (T) new DetailPlaceViewModel(
                    detailPlaceRepository,
                    restaurantRepository
            );
        }else if (modelClass.isAssignableFrom(ChatViewModel.class)) {
            return (T) new ChatViewModel(
                    chatRepository,
                    userRepository);
        } else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(
                    MainApplication.getApplication(),
                    currentLocationRepository,
                    autocompleteRepository,
                    userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
