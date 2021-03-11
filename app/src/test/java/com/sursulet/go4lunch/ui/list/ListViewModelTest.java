package com.sursulet.go4lunch.ui.list;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.sursulet.go4lunch.BuildConfig;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.model.nearby.Result;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.DetailPlaceRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;
import com.sursulet.go4lunch.repository.RestaurantRepository;
import com.sursulet.go4lunch.repository.UserRepository;
import com.sursulet.go4lunch.utils.DetailTestUtils;
import com.sursulet.go4lunch.utils.LiveDataTestUtils;
import com.sursulet.go4lunch.utils.NearbyTestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ListViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    UserRepository userRepository;

    @Mock
    CurrentLocationRepository currentLocationRepository;

    @Mock
    RestaurantRepository restaurantRepository;

    @Mock
    DetailPlaceRepository detailPlaceRepository;

    @Mock
    NearbyPlacesRepository nearbyPlacesRepository;

    @Mock
    Location location;

    private MutableLiveData<Location> currentLocation;
    private MutableLiveData<List<Result>> nearbyPlaces;
    private MutableLiveData<GooglePlacesDetailResult> detailPlaceLiveData;
    private MutableLiveData<List<User>> workmatesLiveData;

    ListViewModel viewModel;

    private static final double LATITUDE = 48.85838489;
    private static final double LONGITUDE = 2.350088;

    @Before
    public void setUp() {
        currentLocation = new MutableLiveData<>();
        nearbyPlaces = new MutableLiveData<>();
        detailPlaceLiveData = new MutableLiveData<>();
        workmatesLiveData = new MutableLiveData<>();

        doReturn(LATITUDE).when(location).getLatitude();
        doReturn(LONGITUDE).when(location).getLongitude();

        doReturn(currentLocation).when(currentLocationRepository).getLastLocationLiveData();
        doReturn(nearbyPlaces).when(nearbyPlacesRepository).getNearByPlaces(anyDouble(), anyDouble());
        doReturn(detailPlaceLiveData).when(detailPlaceRepository).getDetailPlace(any());
        doReturn(workmatesLiveData).when(restaurantRepository).getActiveRestaurantAllBookings(any());

        viewModel = new ListViewModel(
                currentLocationRepository,
                nearbyPlacesRepository,
                detailPlaceRepository,
                userRepository,
                restaurantRepository
        );
    }

    @Test
    public void displayBasic() throws InterruptedException {
        //Given
        currentLocation.setValue(location);
        nearbyPlaces.setValue(getRestaurants());
        detailPlaceLiveData.setValue(getGooglePlacesDetailResult(0));
        workmatesLiveData.setValue(get2Users());

        //When
        List<ListUiModel> result = LiveDataTestUtils.getOrAwaitValue(viewModel.getUiModelMediator());

        // Then
        assertEquals(1, result.size());
        assertFirstRestaurantIsInPosition(result);
    }

    //Region mock
    private List<Result> getRestaurants() {
        List<Result> restaurants = new ArrayList<>();

        Result nearbyResultA;
        nearbyResultA = NearbyTestUtils.buildResult(
                48.858397, 2.3501027,
                48.8598099302915, 2.351359930291502, 48.8571119697085, 2.348661969708498,
                "Benoit Paris", false, "ChIJQ0bNfR5u5kcR9Z0i41",
                4.1, "ChIJQ0bNfR5u5kcR9Z0i41-E7sg", "20 Rue Saint-Martin, Paris"
        );

        Result nearbyResultB;
        nearbyResultB = NearbyTestUtils.buildResult(
                48.857556, 2.34964,
                48.8588130302915, 2.350793730291501,
                48.85611506970851, 2.348095769708498,
                "Compose Hôtel de Ville", true, "ChIJM-MmbR5u5kcRYfCWNoZ6YOc",
                4.2, "ChIJM-MmbR5u5kcRYfCWNoZ6YOc", "8 Rue Saint-Martin, Paris"
        );

        restaurants.add(nearbyResultA);
        //restaurants.add(nearbyResultB);

        return restaurants;
    }

    private GooglePlacesDetailResult getGooglePlacesDetailResult(int position) {
        GooglePlacesDetailResult googlePlacesDetailResult = new GooglePlacesDetailResult();
        com.sursulet.go4lunch.model.details.Result detailResult = null;
        if(position == 0) {
            detailResult = DetailTestUtils.buildDetailResult(
                    "20 Rue Saint-Martin, 75004 Paris, France", "01 42 72 25 76", "Benoit Paris",
                    false, "ATtYBwLpXhMNGQ2d7MLf2xQ7OLZLJfnpYw2ZgTaXctClkoABb0CWjVBQzAQcqsTACZxX912_b1YXYbUSfuBqjZDcmoSxvxud38Yvy6pYpojHvhdj_rn1upQSC1UB2pYzOXYw5MRRo",
                    "ChIJQ0bNfR5u5kcR9Z0i41", 4.1, "ChIJQ0bNfR5u5kcR9Z0i41-E7sg", "20 Rue Saint-Martin, Paris", "http://www.benoit-paris.com/"
            );
        } else if(position == 1) {
            detailResult = DetailTestUtils.buildDetailResult(
                    "20 Rue Saint-Martin, 75004 Paris, France", "01 42 72 25 76", "BABABABAB",
                    false, "ATtYBwLpXhMNGQ2d7MLf2xQ7OLZLJfnpYw2ZgTaXctClkoABb0CWjVBQzAQcqsTACZxX912_b1YXYbUSfuBqjZDcmoSxvxud38Yvy6pYpojHvhdj_rn1upQSC1UB2pYzOXYw5MRRo",
                    "ChIJM-MmbR5u5kcRYfCWNoZ6YOc", 4.1, "ChIJQ0bNfR5u5kcR9Z0i41-E7sg", "20 Rue Saint-Martin, Paris", "http://www.benoit-paris.com/"
            );
        }

        googlePlacesDetailResult.setResult(detailResult);
        return googlePlacesDetailResult;
    }

    private List<User> get2Users() {
        List<User> users= new ArrayList<>();
        users.add(new User("0", "Peach", "https://unsplash.com/photos/gKXKBY-C-Dk"));
        users.add(new User("1", "Yoshi", "https://unsplash.com/photos/gjlMT52gy5M"));
        return users;
    }

    // region Assert
    private void assertFirstRestaurantIsInPosition(@NonNull List<ListUiModel> result) {
        assertEquals(result.get(0).getId(), "ChIJQ0bNfR5u5kcR9Z0i41");
        assertEquals(result.get(0).getName(), "Benoit Paris");
        assertEquals(result.get(0).getPhotoUrl(), "https://maps.googleapis.com/maps/api/place/photo?maxwidth=500&photoreference=photoRef&key="+ BuildConfig.GOOGLE_PLACES_KEY);
        assertEquals(result.get(0).getTxt(), "20 Rue Saint-Martin, Paris");
        assertEquals(result.get(0).getOpening(), "Close");
        assertEquals(result.get(0).getDistance(), "2m");
        assertEquals(2.46, result.get(0).getRating(),0.001);
        assertEquals("(2)", result.get(0).getNbWorkmates());
    }

    private void assertSecondRestaurantIsInPosition(@NonNull List<ListUiModel> result, int position) {
        assertEquals(result.get(position).getId(), "1");
        //assertEquals(result.get(position).getNbWorkmates(), "(2)");
    }
}