package com.sursulet.go4lunch;

import android.app.Application;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.model.autocomplete.Prediction;
import com.sursulet.go4lunch.repository.AutocompleteRepository;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.UserRepository;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final Application application;
    private final CurrentLocationRepository currentLocationRepository;
    private final AutocompleteRepository autocompleteRepository;
    private final UserRepository userRepository;

    private AutocompleteAsyncTask myCurrentAutocompleteAsyncTask;

    private final MediatorLiveData<MainUiModel> uiModelMutableLiveData = new MediatorLiveData<>();
    private final MutableLiveData<List<Prediction>> predictionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> selectedQueryLiveData = new MutableLiveData<>();
    LiveData<Location> currentLocationLiveData;

    public MainViewModel(
            Application application,
            CurrentLocationRepository currentLocationRepository,
            AutocompleteRepository autocompleteRepository,
            UserRepository userRepository
    ) {
        this.application = application;
        this.currentLocationRepository = currentLocationRepository;
        this.autocompleteRepository = autocompleteRepository;
        this.userRepository = userRepository;

        currentLocationLiveData = currentLocationRepository.getLastLocationLiveData();

        LiveData<String> nameLiveData = userRepository.getCurrentUserName();
        LiveData<String> emailLiveData = userRepository.getCurrentUserEmail();
        LiveData<Uri> photoLiveData = userRepository.getCurrentUserPhoto();

        uiModelMutableLiveData.addSource(nameLiveData, name -> combine(name, emailLiveData.getValue(), photoLiveData.getValue()));
        uiModelMutableLiveData.addSource(emailLiveData, email -> combine(nameLiveData.getValue(), email, photoLiveData.getValue()));
        uiModelMutableLiveData.addSource(photoLiveData, uri -> combine(nameLiveData.getValue(), emailLiveData.getValue(), uri));

    }

    private void combine(String name, String email, Uri uri) {
        if (name == null || email == null || uri == null) {
            return;
        }

        if (isCurrentUserLogged()) {
            uiModelMutableLiveData.setValue(new MainUiModel(name, email, uri));
        }
    }


    public Boolean isCurrentUserLogged() {
        return userRepository.isCurrentUserLogged();
    }

    public LiveData<MainUiModel> getUiModelLiveData() {
        return uiModelMutableLiveData;
    }

    public void createUser() {
        userRepository.createUser();
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
    }

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

    private void onPredictionsChange(List<Prediction> predictions) {
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
