package com.sursulet.go4lunch.ui.detail;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.model.details.DetailResult;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
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
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class DetailPlaceViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    RestaurantRepository restaurantRepository;

    @Mock
    DetailPlaceRepository detailPlaceRepository;

    @Mock
    FirebaseAuth auth;

    private MutableLiveData<GooglePlacesDetailResult> placeDetailResult;
    private MutableLiveData<List<WorkmatesUiModel>> workmatesLiveData;
    private MutableLiveData<Boolean> isGoingLiveData;
    private MutableLiveData<Boolean> isLikePlaceLiveData;

    private DetailPlaceViewModel viewModel;

    private static final String PLACE_ID = "ChIJQ0bNfR5u5kcR9Z0i41";
    private static final String CURRENT_USER_ID = "ChIJQ0bNfR5u5kcR9Z0i41";
    String uid;

    @Before
    public void setUp() {
        auth = Mockito.mock(FirebaseAuth.class);

        placeDetailResult = new MutableLiveData<>();
        workmatesLiveData = new MutableLiveData<>();
        isGoingLiveData = new MutableLiveData<>();
        isLikePlaceLiveData = new MutableLiveData<>();

        given(detailPlaceRepository.getDetailPlace(isA(String.class))).willReturn(placeDetailResult);
        //given(restaurantRepository.getAllBookings(isA(String.class))).willReturn(workmatesLiveData);
        given(restaurantRepository.isBooking(isA(String.class))).willReturn(isGoingLiveData);
        given(restaurantRepository.isFollower(isA(String.class))).willReturn(isLikePlaceLiveData);

        viewModel = new DetailPlaceViewModel(
                detailPlaceRepository,
                restaurantRepository
        );
    }

    @Test
    public void given_repository_has_2_restaurants_liveData_should_expose_restaurant_detail() throws InterruptedException {
        Mockito.doReturn(getDefaultUser()).when(auth).getCurrentUser();

        GooglePlacesDetailResult googlePlacesDetailResult = getGooglePlacesDetailResult();
        placeDetailResult.setValue(googlePlacesDetailResult);

        List<WorkmatesUiModel> workmates = get2Users();
        //workmatesLiveData.setValue(workmates);

        isGoingLiveData.setValue(true);
        isLikePlaceLiveData.setValue(true);

        viewModel.startDetailPlace("ChIJQ0bNfR5u5kcR9Z0i41");
        viewModel.setWorkmatesUiLiveData(workmates);

        DetailPlaceUiModel result = LiveDataTestUtils.getOrAwaitValue(viewModel.getUiModelLiveData());

        assertEquals("20 Rue Saint-Martin, 75004 Paris, France", result.getSentence());
    }

    private GooglePlacesDetailResult getGooglePlacesDetailResult() {
        GooglePlacesDetailResult googlePlacesDetailResult = new GooglePlacesDetailResult();
        googlePlacesDetailResult.setDetailResult(getDetailResult());
        return googlePlacesDetailResult;
    }

    private DetailResult getDetailResult() {
        return DetailTestUtils.buildDetailResult(
                "20 Rue Saint-Martin, 75004 Paris, France", "01 42 72 25 76", "Benoit Paris",
                false, "ATtYBwLpXhMNGQ2d7MLf2xQ7OLZLJfnpYw2ZgTaXctClkoABb0CWjVBQzAQcqsTACZxX912_b1YXYbUSfuBqjZDcmoSxvxud38Yvy6pYpojHvhdj_rn1upQSC1UB2pYzOXYw5MRRo",
                "ChIJQ0bNfR5u5kcR9Z0i41", 4.1,
                "ChIJQ0bNfR5u5kcR9Z0i41-E7sg", "20 Rue Saint-Martin, Paris", "http://www.benoit-paris.com/"
        );
    }

    // --- Region mock
    private List<WorkmatesUiModel> get2Users() {
        List<WorkmatesUiModel> users = new ArrayList<>();
        users.add(new WorkmatesUiModel("0", "Peach is joining", "https://unsplash.com/photos/gKXKBY-C-Dk", 0));
        users.add(new WorkmatesUiModel("1", "Yoshi is joining", "https://unsplash.com/photos/gjlMT52gy5M", 0));
        return users;
    }

    private FirebaseUser getDefaultUser() {
        FirebaseUser firebaseUser = Mockito.mock(FirebaseUser.class);
        Mockito.doReturn("id1").when(firebaseUser).getUid();
        return firebaseUser;
    }

}