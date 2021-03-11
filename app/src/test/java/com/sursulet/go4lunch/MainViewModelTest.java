package com.sursulet.go4lunch;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

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
    private MutableLiveData<Map<String,String>> userMap;

    MainViewModel viewModel;

    private static final double LATITUDE = 48.85838489;
    private static final double LONGITUDE = 2.350088;

    @Before
    public void setUp() {
        userMap = new MutableLiveData<>();
        currentLocation = new MutableLiveData<>();
        predictions = getPredictions();

        doReturn(LATITUDE).when(location).getLatitude();
        doReturn(LONGITUDE).when(location).getLongitude();

        doReturn(currentLocation).when(currentLocationRepository).getLastLocationLiveData();
        doReturn(userMap).when(userRepository).getCurrentUserInstance();
        doReturn(true).when(userRepository).isCurrentUserLogged();
        doReturn(predictions).when(autocompleteRepository).getAutocompleteByLocation(Mockito.any(), Mockito.any());


        viewModel = new MainViewModel(
                currentLocationRepository,
                autocompleteRepository,
                userRepository
        );
    }

    @Test
    public void displayBasic() throws InterruptedException {
        Map<String,String> map = new HashMap<>();
        map.put("name","Peach");
        map.put("email","peach@mario.com");
        map.put("url", "https://unsplash.com/photos/gKXKBY-C-Dk");

        userMap.setValue(map);
        currentLocation.setValue(location);

        //When
        MainUiModel result = LiveDataTestUtils.getOrAwaitValue(viewModel.getUiModelLiveData());

        assertEquals("Peach", result.getUsername());
        assertEquals("peach@mario.com", result.getEmail());
        assertEquals("https://unsplash.com/photos/gKXKBY-C-Dk", result.getPhotoUrl());
    }

    @Test
    public void displayPredictions() throws InterruptedException {
        currentLocation.setValue(location);
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