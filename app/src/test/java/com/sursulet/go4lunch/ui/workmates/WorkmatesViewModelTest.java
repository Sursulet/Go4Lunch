package com.sursulet.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

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
    private MutableLiveData<String> nameRestaurantLiveData;

    private WorkmatesViewModel viewModel;

    @Before
    public void SetUp() {
        usersLiveData = new MutableLiveData<>();
        nameRestaurantLiveData = new MutableLiveData<>();

        doReturn(usersLiveData).when(userRepository).getAllUsers();
        doReturn(nameRestaurantLiveData).when(restaurantRepository).getNameActiveRestaurant(Mockito.any());

        viewModel = new WorkmatesViewModel(userRepository, restaurantRepository);
    }

    // -- BASIC
    @Test
    public void displayBasic() throws InterruptedException {
        //Given
        // Mock LiveData returned from Repository
        usersLiveData.setValue(get2Users());
        nameRestaurantLiveData.setValue("Benoit Paris");

        // When
        List<WorkmatesUiModel> result = LiveDataTestUtils.getOrAwaitValue(viewModel.getWorkmatesUiModelLiveData());

        // Then
        assertEquals(2, result.size());

        assertFirstWorkmateIsInPosition(result, 0);
        assertSecondWorkmateIsInPosition(result, 1);
    }

    // --- Region mock
    private List<User> get2Users() {
        List<User> users= new ArrayList<>();
        users.add(new User("0", "Peach", "https://unsplash.com/photos/gjlMT52gy5M"));
        users.add(new User("1", "Yoshi", "https://unsplash.com/photos/WO-t5wT_zSw"));
        return users;
    }

    // region Assert
    private void assertFirstWorkmateIsInPosition(@NonNull List<WorkmatesUiModel> result, int position) {
        assertEquals(result.get(position).getUid(), "0");
        assertEquals(result.get(position).getSentence(), "Peach hasn't decided yet");
        assertEquals(result.get(position).getPhoto(), "https://unsplash.com/photos/gjlMT52gy5M");
    }

    private void assertSecondWorkmateIsInPosition(@NonNull List<WorkmatesUiModel> result, int position) {
        assertEquals(result.get(position).getUid(), "1");
        assertEquals(result.get(position).getSentence(), "Yoshi hasn't decided yet");
        assertEquals(result.get(position).getPhoto(), "https://unsplash.com/photos/WO-t5wT_zSw");
    }
}