package com.sursulet.go4lunch;

import android.app.Application;
import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.model.autocomplete.Prediction;
import com.sursulet.go4lunch.model.autocomplete.StructuredFormatting;
import com.sursulet.go4lunch.repository.AutocompleteRepository;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
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
public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    Application application;

    @Mock
    UserRepository userRepository;

    @Mock
    CurrentLocationRepository currentLocationRepository;

    @Mock
    AutocompleteRepository autocompleteRepository;

    @Mock
    Location location;

    private MutableLiveData<Location> currentLocation;
    private List<Prediction> predictions;
    private MutableLiveData<User> uiModelMutableLiveData;
    private MutableLiveData<String> name;
    private MutableLiveData<String> email;
    private MutableLiveData<String> photo;

    MainViewModel viewModel;

    private static final double LATITUDE = 48.85838489;
    private static final double LONGITUDE = 2.350088;

    @Before
    public void setUp() {
        name = new MutableLiveData<>();
        email = new MutableLiveData<>();
        photo = new MutableLiveData<>();
        currentLocation = new MutableLiveData<>();
        uiModelMutableLiveData = new MutableLiveData<>();
        predictions = getPredictions();

        doReturn(LATITUDE).when(location).getLatitude();
        doReturn(LONGITUDE).when(location).getLongitude();

        doReturn(currentLocation).when(currentLocationRepository).getLastLocationLiveData();
        doReturn(name).when(userRepository).getCurrentUserName();
        doReturn(email).when(userRepository).getCurrentUserEmail();
        doReturn(photo).when(userRepository).getCurrentUserPhoto();
        doReturn(true).when(userRepository).isCurrentUserLogged();
        doReturn(predictions).when(autocompleteRepository).getAutocompleteByLocation(Mockito.any(), Mockito.any());


        viewModel = new MainViewModel(
                application,
                currentLocationRepository,
                autocompleteRepository,
                userRepository
        );
    }

    @Test
    public void displayBasic() throws InterruptedException {
        name.setValue("Steffy");
        email.setValue("zer@gjk.com");
        String url = "https://unsplash.com/photos/gKXKBY-C-Dk";
        photo.setValue(url);
        currentLocation.setValue(location);

        //When
        MainUiModel result = LiveDataTestUtils.getOrAwaitValue(viewModel.getUiModelLiveData());

        assertEquals("Steffy", result.getUsername());
    }

    @Test
    public void displayPredictions() throws InterruptedException {
        currentLocation.setValue(location);
        viewModel.onQueryTextChange("Benoit");
        viewModel.onPredictionsChange(predictions);

        //When
        List<String> result = LiveDataTestUtils.getOrAwaitValue(viewModel.getPredictionsLiveData());

        assertEquals(1, result.size());
        assertEquals("Benoit Paris", result.get(0));
    }

    List<Prediction> getPredictions() {
        List<Prediction> predictions = new ArrayList<>();
        Prediction prediction = new Prediction();
        StructuredFormatting structuredFormatting = new StructuredFormatting();
        structuredFormatting.setMainText("Benoit Paris");
        prediction.setStructuredFormatting(structuredFormatting);

        predictions.add(prediction);
        return predictions;
    }
}