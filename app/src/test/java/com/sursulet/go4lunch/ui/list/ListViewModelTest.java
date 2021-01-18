package com.sursulet.go4lunch.ui.list;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.sursulet.go4lunch.model.NearbyResult;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.model.details.DetailResult;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.DetailPlaceRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;
import com.sursulet.go4lunch.repository.RestaurantRepository;
import com.sursulet.go4lunch.repository.UserRepository;
import com.sursulet.go4lunch.utils.DataUtils;
import com.sursulet.go4lunch.utils.DetailTestUtils;
import com.sursulet.go4lunch.utils.LiveDataTestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;

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

    MutableLiveData<Location> currentLocationLiveData;
    MutableLiveData<List<NearbyResult>> nearbyPlacesLiveData;
    MutableLiveData<GooglePlacesDetailResult> detailPlaceLiveData;
    MutableLiveData<List<User>> workmatesLiveData;

    private ListViewModel viewModel;

    private static final String PLACE_ID = "ChIJQ0bNfR5u5kcR9Z0i41";
    private static final double LATITUDE = 48.85838489;
    private static final double LONGITUDE = 2.350088;

    @Before
    public void setUp() {
        currentLocationLiveData = new MutableLiveData<>();
        nearbyPlacesLiveData = new MutableLiveData<>();
        detailPlaceLiveData = new MutableLiveData<>();
        workmatesLiveData = new MutableLiveData<>();

        given(location.getLatitude()).willReturn(LATITUDE);
        given(location.getLongitude()).willReturn(LONGITUDE);

        given(currentLocationRepository.getLastLocationLiveData()).willReturn(currentLocationLiveData);
        given(detailPlaceRepository.getDetailPlace(isA(String.class))).willReturn(detailPlaceLiveData);
        given(restaurantRepository.getAllBookings(isA(String.class))).willReturn(workmatesLiveData);

        viewModel = new ListViewModel(
                currentLocationRepository,
                nearbyPlacesRepository,
                detailPlaceRepository,
                userRepository,
                restaurantRepository
        );
    }

    @Test
    public void given_repository_has_1_restaurants_liveData_should_expose_1_restaurants() throws InterruptedException {
        //Given
        currentLocationLiveData.setValue(location);

        List<NearbyResult> restaurants = getRestaurant();

        GooglePlacesDetailResult googlePlacesDetailResult = getGooglePlacesDetailResult(0);
        List<User> mates = get2Users();

        //When
        viewModel.setNearbyPlacesDependingOnGps(restaurants);
        viewModel.setDetailPlaceMediator("ChIJQ0bNfR5u5kcR9Z0i41", googlePlacesDetailResult);
        viewModel.setWorkmatesMediator(PLACE_ID, mates);

        List<ListUiModel> result = LiveDataTestUtils.getOrAwaitValue(viewModel.getUiModelMediator());

        // Then
        assertEquals(1, result.size());
        assertFirstRestaurantIsInPosition(result, 0);
    }

    //Region mock
    private List<NearbyResult> getRestaurant() {
        List<NearbyResult> restaurants = new ArrayList<>();

        NearbyResult nearbyResultA;
        nearbyResultA = DataUtils.buildResult(
                48.858397, 2.3501027,
                48.8598099302915, 2.351359930291502, 48.8571119697085, 2.348661969708498,
                "Benoit Paris", false, "ChIJQ0bNfR5u5kcR9Z0i41",
                4.1, "ChIJQ0bNfR5u5kcR9Z0i41-E7sg", "20 Rue Saint-Martin, Paris"
        );

        NearbyResult nearbyResultB;
        nearbyResultB = DataUtils.buildResult(
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
        DetailResult detailResult = null;
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
                    "AAAAAAAAAAAA", 4.1, "ChIJQ0bNfR5u5kcR9Z0i41-E7sg", "20 Rue Saint-Martin, Paris", "http://www.benoit-paris.com/"
            );
        }

        googlePlacesDetailResult.setDetailResult(detailResult);
        return googlePlacesDetailResult;
    }

    private Location getCurrentLocation() {
        Location location = new Location("");
        location.setLatitude(48.85838489);
        location.setLongitude(2.350088);
        return location;
    }

    private List<User> get2Users() {
        List<User> users= new ArrayList<>();
        users.add(new User("0", "Peach", "https://unsplash.com/photos/gKXKBY-C-Dk"));
        users.add(new User("1", "Yoshi", "https://unsplash.com/photos/gjlMT52gy5M"));
        return users;
    }

    // region Assert
    private void assertFirstRestaurantIsInPosition(@NonNull List<ListUiModel> result, int position) {
        assertEquals(result.get(position).getId(), "ChIJQ0bNfR5u5kcR9Z0i41");
        assertEquals(result.get(position).getName(), "Benoit Paris");
        assertEquals(result.get(position).getPhotoUrl(), "https://unsplash.com/photos/gjlMT52gy5M");
        assertEquals(result.get(position).getSentence(), "20 Rue Saint-Martin, Paris");
        assertEquals(result.get(position).getOpening(), "Close");
        assertEquals(result.get(position).getDistance(), "98878m");
        //assertEquals(result.get(position).getRating(), 4.4,0.001);
        assertEquals(result.get(position).getNbWorkmates(), "(2)");
    }

    private void assertSecondRestaurantIsInPosition(@NonNull List<ListUiModel> result, int position) {
        assertEquals(result.get(position).getId(), "1");
        //assertEquals(result.get(position).getNbWorkmates(), "(2)");
    }
}