package com.sursulet.go4lunch;

import android.app.Application;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sursulet.go4lunch.api.UserHelper;
import com.sursulet.go4lunch.model.autocomplete.Prediction;
import com.sursulet.go4lunch.repository.AutocompleteRepository;
import com.sursulet.go4lunch.repository.CurrentLocationRepository;
import com.sursulet.go4lunch.repository.UserRepository;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final Application application;
    private final FirebaseAuth firebaseAuth;
    private final CurrentLocationRepository currentLocationRepository;
    private final AutocompleteRepository autocompleteRepository;
    private final UserRepository userRepository;

    private AutocompleteAsyncTask myCurrentAutocompleteAsyncTask;

    private final MutableLiveData<MainUiModel> uiModelMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Prediction>> predictionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> selectedQueryLiveData = new MutableLiveData<>();
    MediatorLiveData<List<String>> queriesMediatorLiveData = new MediatorLiveData<>();
    LiveData<Location> currentLocationLiveData;

    public MainViewModel(
            Application application,
            FirebaseAuth firebaseAuth,
            CurrentLocationRepository currentLocationRepository,
            AutocompleteRepository autocompleteRepository,
            UserRepository userRepository) {
        this.firebaseAuth = firebaseAuth;
        this.application = application;
        this.currentLocationRepository = currentLocationRepository;
        this.autocompleteRepository = autocompleteRepository;
        this.userRepository = userRepository;

        if (firebaseAuth.getCurrentUser() != null) {
            uiModelMutableLiveData.setValue(
                    new MainUiModel(
                            getCurrentUserName(firebaseAuth.getCurrentUser().getDisplayName()),
                            getCurrentUserEmail(firebaseAuth.getCurrentUser().getEmail()),
                            getCurrentUserPhoto(firebaseAuth.getCurrentUser().getPhotoUrl())
                    )
            );
        }

        currentLocationLiveData = currentLocationRepository.getLocationLiveData();

        /*
        queriesMediatorLiveData.addSource(selectedQueryLiveData, s -> combine(s, predictionsLiveData.getValue()));
        queriesMediatorLiveData.addSource(predictionsLiveData, predictions -> combine(selectedQueryLiveData.getValue(), predictions));

         */
    }

    /*
    private void combine(String query, List<Prediction> predictions) {
        if (query == null || predictions == null) return;

        List<String> results = new ArrayList<>();
        for (Prediction prediction : predictions) {
            results.add(prediction.getStructuredFormatting().getMainText());
        }

        queriesMediatorLiveData.setValue(results);
    }

    public LiveData<List<String>> getQueriesMediatorLiveData() { return queriesMediatorLiveData; }

     */

    public Boolean isCurrentUserLogged() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public LiveData<MainUiModel> getUiModelLiveData() {
        return uiModelMutableLiveData;
    }

    private String getCurrentUserName(String name) {
        return TextUtils.isEmpty(name) ? application.getString(R.string.info_no_username_found) : name;
    }

    private String getCurrentUserEmail(String email) {
        return TextUtils.isEmpty(email) ? application.getString(R.string.info_no_email_found) : email;
    }

    private Uri getCurrentUserPhoto(Uri photo) {
        Uri url = null;
        if (photo != null) url = photo;
        return url;
    }

    public void createUser() {
        FirebaseUser userValue = firebaseAuth.getCurrentUser();

        if (userValue != null) {
            String urlPicture = (userValue.getPhotoUrl() != null) ? userValue.getPhotoUrl().toString() : null;
            String username = userValue.getDisplayName();
            String uid = userValue.getUid();

            UserHelper.createUser(uid, username, urlPicture).addOnFailureListener(this.onFailureListener());
        }
    }

    protected OnFailureListener onFailureListener() {
        return e -> Toast.makeText(
                application,
                application.getString(R.string.error_unknown_error),
                Toast.LENGTH_LONG
        ).show();
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
