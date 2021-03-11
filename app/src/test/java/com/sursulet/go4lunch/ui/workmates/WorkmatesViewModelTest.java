package com.sursulet.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

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
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class WorkmatesViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private UserRepository userRepository;

    @Mock
    RestaurantRepository restaurantRepository;

    private MutableLiveData<List<User>> usersLiveData;
    private MutableLiveData<Restaurant> restaurantLiveData;
    private MutableLiveData<Restaurant> restaurantLiveData2;

    private WorkmatesViewModel viewModel;

    @Before
    public void SetUp() {
        usersLiveData = new MutableLiveData<>();
        restaurantLiveData = new MutableLiveData<>();
        restaurantLiveData2 = new MutableLiveData<>();

        doReturn(usersLiveData).when(userRepository).getAllUsers();
        doReturn(restaurantLiveData).when(restaurantRepository).getActiveRestaurantFromLockup(Mockito.eq("0"));
        doReturn(restaurantLiveData2).when(restaurantRepository).getActiveRestaurantFromLockup(Mockito.eq("1"));

        viewModel = new WorkmatesViewModel(userRepository, restaurantRepository);
    }

    // -- BASIC
    @Test
    public void displayBasic() throws InterruptedException {
        //Given
        // Mock LiveData returned from Repository
        usersLiveData.setValue(get2Users());
        restaurantLiveData.setValue(getRestaurants(0));
        restaurantLiveData2.setValue(getRestaurants(1));

        // When
        List<WorkmatesUiModel> result = LiveDataTestUtils.getOrAwaitValue(viewModel.getWorkmatesUiModelLiveData());

        // Then
        assertEquals(2, result.size());

        assertFirstWorkmateIsInPosition(result);
        assertSecondWorkmateIsInPosition(result);
    }

    // --- Region mock
    private List<User> get2Users() {
        List<User> users= new ArrayList<>();
        users.add(new User("0", "Peach", "https://unsplash.com/photos/gjlMT52gy5M"));
        users.add(new User("1", "Yoshi", "https://unsplash.com/photos/WO-t5wT_zSw"));
        return users;
    }

    private Restaurant getRestaurants(int i) {
        List<Restaurant> restaurants= new ArrayList<>();
        restaurants.add(new Restaurant("0", "Peach", "https://unsplash.com/photos/gjlMT52gy5M"));
        restaurants.add(new Restaurant("1", "Yoshi", "https://unsplash.com/photos/WO-t5wT_zSw"));

        return restaurants.get(i);
    }

    // region Assert
    private void assertFirstWorkmateIsInPosition(@NonNull List<WorkmatesUiModel> result) {
        assertEquals(result.get(0).getUid(), "0");
        assertEquals(result.get(0).getSentence(), "Peach hasn't decided yet");
        assertNull(result.get(0).getMap().get("id"));
        assertEquals(result.get(0).getPhoto(), "https://unsplash.com/photos/gjlMT52gy5M");
    }

    private void assertSecondWorkmateIsInPosition(@NonNull List<WorkmatesUiModel> result) {
        assertEquals(result.get(1).getUid(), "1");
        assertEquals(result.get(1).getSentence(), "Yoshi hasn't decided yet");
        assertNull(result.get(1).getMap().get("id"));
        assertEquals(result.get(1).getPhoto(), "https://unsplash.com/photos/WO-t5wT_zSw");
    }
}