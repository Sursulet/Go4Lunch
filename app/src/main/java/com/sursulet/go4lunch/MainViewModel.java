package com.sursulet.go4lunch;

import android.location.Location;
import android.os.AsyncTask;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.model.autocomplete.Prediction;
import com.sursulet.go4lunch.repository.AutocompleteRepository;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.UserRepository;
import com.sursulet.go4lunch.utils.SingleLiveEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class MainViewModel extends ViewModel {

    private final AutocompleteRepository autocompleteRepository;
    private final UserRepository userRepository;

    private AutocompleteAsyncTask myCurrentAutocompleteAsyncTask;

    final LiveData<Map<String, String>> mapLiveData;
    final LiveData<Location> currentLocationLiveData;
    private final MutableLiveData<String> selectedQueryLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Prediction>> predictionsLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<String> singleLiveEventLaunchDetailActivity = new SingleLiveEvent<>();

    public MainViewModel(
            CurrentLocationRepository currentLocationRepository,
            AutocompleteRepository autocompleteRepository,
            UserRepository userRepository
    ) {
        this.autocompleteRepository = autocompleteRepository;
        this.userRepository = userRepository;

        mapLiveData = userRepository.getCurrentUserInstance();
        currentLocationLiveData = currentLocationRepository.getLastLocationLiveData();

    }

    public LiveData<MainUiModel> getUiModelLiveData() {
        return Transformations.map(mapLiveData, map ->
                new MainUiModel(
                        map.get("name"),
                        map.get("email"),
                        map.get("url")
                ));
    }

    public void onQueryTextChange(String newText) {
        if (myCurrentAutocompleteAsyncTask != null && !myCurrentAutocompleteAsyncTask.isCancelled()) {
            myCurrentAutocompleteAsyncTask.cancel(true);
        }

        myCurrentAutocompleteAsyncTask = new AutocompleteAsyncTask(
                newText,
                currentLocationLiveData.getValue(),
                autocompleteRepository,
                new WeakReference<>(this)
        );

        myCurrentAutocompleteAsyncTask.execute();
    }

    public void onQuerySelected(String text) {
        selectedQueryLiveData.setValue(text);
        userRepository.setSelectedQuery(text);
        onQueryTextChange("");
    }

    public void openDetailActivity(String id) {
        singleLiveEventLaunchDetailActivity.setValue(id);
    }

    public SingleLiveEvent<String> getSingleLiveEventOpenDetailActivity() {
        return singleLiveEventLaunchDetailActivity;
    }

    public LiveData<String> getCurrentUserRestaurant() {
        return userRepository.getCurrentUserRestaurant();
    }

    @SuppressWarnings("deprecation")
    private static class AutocompleteAsyncTask extends AsyncTask<Void, Void, List<Prediction>> {

        private final String query;
        private final AutocompleteRepository autocompleteRepository;
        private final WeakReference<MainViewModel> mainViewModelWeakReference;
        private final Location location;

        private AutocompleteAsyncTask(
                String query,
                Location location,
                AutocompleteRepository autocompleteRepository,
                WeakReference<MainViewModel> mainViewModelWeakReference
        ) {
            this.query = query;
            this.location = location;
            this.autocompleteRepository = autocompleteRepository;
            this.mainViewModelWeakReference = mainViewModelWeakReference;
        }

        @Override
        protected List<Prediction> doInBackground(Void... voids) {
            return autocompleteRepository.getAutocompleteByLocation(query, location);
        }

        @Override
        protected void onPostExecute(List<Prediction> predictions) {
            if (mainViewModelWeakReference.get() != null) {
                mainViewModelWeakReference.get().onPredictionsChange(predictions);
            }
        }
    }

    @VisibleForTesting
    void onPredictionsChange(List<Prediction> predictions) {
        predictionsLiveData.setValue(predictions);
    }

    public LiveData<List<String>> getPredictionsLiveData() {
        return Transformations.map(predictionsLiveData, predictions -> {
            List<String> results = new ArrayList<>();
            for (Prediction prediction : predictions) {
                String text = prediction.getStructuredFormatting().getMainText();
                results.add(text);
            }
            return results;
        });
    }
}
