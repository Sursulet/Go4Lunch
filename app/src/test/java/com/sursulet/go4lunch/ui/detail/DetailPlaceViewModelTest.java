package com.sursulet.go4lunch.ui.detail;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.model.details.Result;
import com.sursulet.go4lunch.repository.DetailPlaceRepository;
import com.sursulet.go4lunch.repository.RestaurantRepository;
import com.sursulet.go4lunch.ui.workmates.WorkmatesUiModel;
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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class DetailPlaceViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    DetailPlaceRepository detailPlaceRepository;

    @Mock
    RestaurantRepository restaurantRepository;

    private MutableLiveData<GooglePlacesDetailResult> detailPlaceLiveData;
    private MutableLiveData<Boolean> isGoingLiveData;
    private MutableLiveData<Boolean> isLikeLiveData;
    private MutableLiveData<List<User>> workmatesLiveData;

    DetailPlaceViewModel viewModel;

    @Before
    public void setUp() {
        detailPlaceLiveData = new MutableLiveData<>();
        isGoingLiveData = new MutableLiveData<>();
        isLikeLiveData = new MutableLiveData<>();
        workmatesLiveData = new MutableLiveData<>();

        doReturn(detailPlaceLiveData).when(detailPlaceRepository).getDetailPlace(any());
        doReturn(isGoingLiveData).when(restaurantRepository).isBooking(any());
        doReturn(isLikeLiveData).when(restaurantRepository).isFollowing(any());
        doReturn(workmatesLiveData).when(restaurantRepository).getActiveRestaurantAllBookings(any());

        viewModel = new DetailPlaceViewModel(detailPlaceRepository, restaurantRepository);
    }

    @Test
    public void basicDisplay() throws InterruptedException {
        //Given
        detailPlaceLiveData.setValue(getGooglePlacesDetailResult());
        isGoingLiveData.setValue(true);
        isLikeLiveData.setValue(true);
        workmatesLiveData.setValue(get2Users());

        viewModel.startDetailPlace("ChIJQ0bNfR5u5kcR9Z0i41");

        DetailPlaceUiModel result = LiveDataTestUtils.getOrAwaitValue(viewModel.getUiModelLiveData());

        assertDetailRestaurant(result);
        assertEquals(2, result.getWorkmates().size());
        assertFirstWorkmatesIsInPosition(result.getWorkmates(),0);
        assertSecondWorkmatesIsInPosition(result.getWorkmates(),1);
    }

    // --- Region mock
    private List<User> get2Users() {
        List<User> users = new ArrayList<>();
        users.add(new User("0", "Peach", "https://unsplash.com/photos/gKXKBY-C-Dk"));
        users.add(new User("1", "Yoshi", "https://unsplash.com/photos/gjlMT52gy5M"));
        return users;
    }

    private GooglePlacesDetailResult getGooglePlacesDetailResult() {
        GooglePlacesDetailResult googlePlacesDetailResult = new GooglePlacesDetailResult();
        googlePlacesDetailResult.setResult(getDetailResult());
        return googlePlacesDetailResult;
    }

    private Result getDetailResult() {
        return DetailTestUtils.buildDetailResult(
                "20 Rue Saint-Martin, 75004 Paris, France",
                "01 42 72 25 76", "Benoit Paris",
                false,
                "ATtYBwLpXhMNGQ2d7MLf2xQ7OLZLJfnpYw2ZgTaXctClkoABb0CWjVBQzAQcqsTACZxX912_b1YXYbUSfuBqjZDcmoSxvxud38Yvy6pYpojHvhdj_rn1upQSC1UB2pYzOXYw5MRRo",
                "ChIJQ0bNfR5u5kcR9Z0i41", 4.1,
                "ChIJQ0bNfR5u5kcR9Z0i41-E7sg",
                "20 Rue Saint-Martin, Paris",
                "http://www.benoit-paris.com/"
        );
    }

    // region Assert
    private void assertDetailRestaurant(@NonNull DetailPlaceUiModel result) {
        assertEquals("Benoit Paris", result.getName());
        assertEquals("https://maps.googleapis.com/maps/api/place/photo?maxwidth=1000&photoreference=ATtYBwLpXhMNGQ2d7MLf2xQ7OLZLJfnpYw2ZgTaXctClkoABb0CWjVBQzAQcqsTACZxX912_b1YXYbUSfuBqjZDcmoSxvxud38Yvy6pYpojHvhdj_rn1upQSC1UB2pYzOXYw5MRRo&key=", result.getUrlPhoto());
        assertEquals("20 Rue Saint-Martin, 75004 Paris, France", result.getSentence());
        assertEquals("01 42 72 25 76", result.getPhoneNumber());
        assertEquals("http://www.benoit-paris.com/", result.getUrlWebsite());
        assertEquals(2.46, result.getRating(),0.1);
        assertEquals(2, result.getWorkmates().size());
    }

    private void assertFirstWorkmatesIsInPosition(@NonNull List<WorkmatesUiModel> result, int position) {
        assertEquals("0", result.get(position).getUid());
        assertEquals("Peach is joining!", result.get(position).getSentence());
        assertEquals("https://unsplash.com/photos/gKXKBY-C-Dk", result.get(position).getPhoto());
        assertEquals(1, result.get(position).getTxtStyle());
    }

    private void assertSecondWorkmatesIsInPosition(@NonNull List<WorkmatesUiModel> result, int position) {
        assertEquals("1", result.get(position).getUid());
        assertEquals("Yoshi is joining!", result.get(position).getSentence());
        assertEquals("https://unsplash.com/photos/gjlMT52gy5M", result.get(position).getPhoto());
        assertEquals(1, result.get(position).getTxtStyle());
    }
}