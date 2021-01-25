package com.sursulet.go4lunch.ui.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.model.nearby.Result;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.NearbyPlacesRepository;
import com.sursulet.go4lunch.repository.RestaurantRepository;
import com.sursulet.go4lunch.repository.UserRepository;
import com.sursulet.go4lunch.utils.LiveDataTestUtils;
import com.sursulet.go4lunch.utils.NearbyTestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class MapViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    Location location;

    @Mock
    CurrentLocationRepository currentLocationRepository;

    @Mock
    NearbyPlacesRepository nearbyPlacesRepository;

    @Mock
    RestaurantRepository restaurantRepository;

    @Mock
    UserRepository userRepository;

    private MutableLiveData<Location> currentLocation;
    MutableLiveData<List<Result>> nearbyPlaceLiveData;
    MutableLiveData<String> userQueryLiveData;
    MutableLiveData<List<String>> activeRestaurantsLiveData;

    MapViewModel viewModel;

    private static final double LATITUDE = 48.85838489;
    private static final double LONGITUDE = 2.350088;

    @Before
    public void setUp() {
        currentLocation = new MutableLiveData<>();
        nearbyPlaceLiveData = new MutableLiveData<>();
        userQueryLiveData = new MutableLiveData<>();
        activeRestaurantsLiveData = new MutableLiveData<>();

        doReturn(LATITUDE).when(location).getLatitude();
        doReturn(LONGITUDE).when(location).getLongitude();

        doReturn(currentLocation).when(currentLocationRepository).getLastLocationLiveData();
        doReturn(nearbyPlaceLiveData).when(nearbyPlacesRepository).getNearByPlaces(Mockito.anyDouble(), Mockito.anyDouble());
        doReturn(userQueryLiveData).when(userRepository).getSelectedQuery();
        doReturn(activeRestaurantsLiveData).when(restaurantRepository).getAllActiveRestaurantsIds();

        viewModel = new MapViewModel(
                currentLocationRepository,
                nearbyPlacesRepository,
                userRepository,
                restaurantRepository
        );
    }

    @Test
    public void displayLastLocation() throws InterruptedException {
        currentLocation.setValue(location);

        Location result = LiveDataTestUtils.getOrAwaitValue(viewModel.getLastLocation());

        // Then
        assertEquals(LATITUDE, result.getLatitude(), 0.1);
        assertEquals(LONGITUDE, result.getLongitude(), 0.1);
    }

    //Sans recherche ni de activesRestaurants
    @Test
    public void displayBasic() throws InterruptedException {
        currentLocation.setValue(location);
        nearbyPlaceLiveData.setValue(getRestaurants());
        //userQueryLiveData.setValue("Benoit Paris");
        //activeRestaurantsLiveData.setValue(getRestaurantIds());

        viewModel.onMapReady();

        List<MapUiModel> result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMapUiModelLiveData());

        // Then
        assertEquals(2, result.size());
        assertFirstRestaurant(result);
        assertSecondRestaurant(result);
    }

    @Test
    public void displayActiveRestaurants() throws InterruptedException {
        currentLocation.setValue(location);
        nearbyPlaceLiveData.setValue(getRestaurants());
        activeRestaurantsLiveData.setValue(getRestaurantIds());

        viewModel.onMapReady();

        List<MapUiModel> result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMapUiModelLiveData());

        // Then
        assertEquals(2, result.size());
        assertEquals(R.drawable.ic_map_marker_24,result.get(0).getIcon());
        assertEquals(R.drawable.ic_map_marker_48dp,result.get(1).getIcon());
    }

    @Test
    public void displaySearch() throws InterruptedException {
        currentLocation.setValue(location);
        nearbyPlaceLiveData.setValue(getRestaurants());
        userQueryLiveData.setValue("Benoit Paris");

        viewModel.onMapReady();

        List<MapUiModel> result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMapUiModelLiveData());

        // Then
        assertEquals(2, result.size());
        assertFirstRestaurant(result);
        assertSecondRestaurant(result);
    }

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
        restaurants.add(nearbyResultB);

        return restaurants;
    }

    private List<String> getRestaurantIds() {
        List<String> ids = new ArrayList<>();
        ids.add("ChIJQ0bNfR5u5kcR9Z0i41");
        return ids;
    }

    // region Assert
    private void assertFirstRestaurant(@NonNull List<MapUiModel> result) {
        assertEquals("ChIJQ0bNfR5u5kcR9Z0i41",result.get(0).getPlaceId());
        assertEquals("Benoit Paris",result.get(0).getName());
        assertEquals(R.drawable.ic_map_marker_48dp,result.get(0).getIcon());
        assertEquals(48.858397, result.get(0).getLat(), 0);
        assertEquals(2.3501027, result.get(0).getLng(), 0);
    }

    private void assertSecondRestaurant(@NonNull List<MapUiModel> result) {
        assertEquals("ChIJM-MmbR5u5kcRYfCWNoZ6YOc", result.get(1).getPlaceId());
        assertEquals("Compose Hôtel de Ville", result.get(1).getName());
        assertEquals(R.drawable.ic_map_marker_48dp, result.get(1).getIcon());
        assertEquals(48.857556, result.get(1).getLat(), 0);
        assertEquals(2.34964, result.get(1).getLng(), 0);
    }
}