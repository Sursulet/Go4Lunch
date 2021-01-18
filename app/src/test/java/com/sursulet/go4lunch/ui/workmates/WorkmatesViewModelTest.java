package com.sursulet.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.repository.RestaurantRepository;
import com.sursulet.go4lunch.repository.UserRepository;
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
public class WorkmatesViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private FirebaseAuth firebaseAuth;

    private MutableLiveData<List<User>> usersLiveData;
    private MutableLiveData<String> restaurantNameLiveData;

    private WorkmatesViewModel viewModel;

    private static final String PLACE_ID = "ChIJQ0bNfR5u5kcR9Z0i41";

    @Before
    public void SetUp() {
        usersLiveData = new MutableLiveData<>();
        restaurantNameLiveData = new MutableLiveData<>();

        given(userRepository.getAllUsers()).willReturn(usersLiveData);
        given(restaurantRepository.getFakeName(isA(String.class))).willReturn(restaurantNameLiveData);

        viewModel = new WorkmatesViewModel(userRepository, restaurantRepository);
    }

    // -- BASIC
    @Test
    public void given_repository_has_2_users_liveData_should_expose_2_users() throws InterruptedException {
        //Given
        // Mock LiveData returned from Repository
        List<User> users = get2Users();
        usersLiveData.setValue(users);

        viewModel.setRestaurantNameMediatorLiveData("0", "Benoit Paris");

        // When
        List<WorkmatesUiModel> result = LiveDataTestUtils.getOrAwaitValue(viewModel.getWorkmatesUiModelLiveData());

        // Then
        assertEquals(2, result.size());
        assertFirstWorkmateIsInPosition(result, 0);
        assertSecondWorkmateIsInPosition(result, 1);
    }

    @Test
    public void given_1_user_has_choose_1_restaurant() throws InterruptedException {
        //Given
        //
        List<User> users = get2Users();
        usersLiveData.setValue(users);

        viewModel.setRestaurantNameMediatorLiveData("0", "Benoit Paris");

        // When
        List<WorkmatesUiModel> result = LiveDataTestUtils.getOrAwaitValue(viewModel.getWorkmatesUiModelLiveData());

        // Then
        assertEquals(result.get(0).getSentence(), "Peach is eating (Benoit Paris)");
    }

    // --- Region mock
    private List<User> get2Users() {
        List<User> users= new ArrayList<>();
        users.add(new User("0", "Peach", "https://unsplash.com/photos/gKXKBY-C-Dk"));
        users.add(new User("1", "Yoshi", "https://unsplash.com/photos/gjlMT52gy5M"));
        return users;
    }

    private Restaurant getRestaurant() {
        return new Restaurant("ChIJQ0bNfR5u5kcR9Z0i41", "Benoit Paris");
    }

    // region Assert
    private void assertFirstWorkmateIsInPosition(@NonNull List<WorkmatesUiModel> result, int position) {
        assertEquals(result.get(position).getUid(), "0");
        assertEquals(result.get(position).getSentence(), "Peach is eating (Benoit Paris)");
        assertEquals(result.get(position).getPhoto(), "https://unsplash.com/photos/gKXKBY-C-Dk");
    }

    private void assertSecondWorkmateIsInPosition(@NonNull List<WorkmatesUiModel> result, int position) {
        assertEquals(result.get(position).getUid(), "1");
        assertEquals(result.get(position).getSentence(), "Yoshi hasn't decided yet");
        assertEquals(result.get(position).getPhoto(), "https://unsplash.com/photos/gjlMT52gy5M");
    }
}