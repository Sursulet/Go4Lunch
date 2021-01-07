package com.sursulet.go4lunch;

import android.app.Application;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sursulet.go4lunch.api.UserHelper;
import com.sursulet.go4lunch.model.autocomplete.Prediction;
import com.sursulet.go4lunch.repository.AutocompleteRepository;
import com.sursulet.go4lunch.repository.UserRepository;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final Application application;
    private final UserRepository userRepository;
    private final AutocompleteRepository autocompleteRepository;
    private final FirebaseAuth firebaseAuth;

    private final MutableLiveData<MainUiModel> uiModelMutableLiveData = new MutableLiveData<>();
    private AutocompleteAsyncTask myCurrentAutocompleteAsyncTask;

    public MainViewModel(
            Application application,
            UserRepository userRepository,
            AutocompleteRepository autocompleteRepository,
            FirebaseAuth firebaseAuth
    ) {
        this.application = application;
        this.userRepository = userRepository;
        this.autocompleteRepository = autocompleteRepository;
        this.firebaseAuth = firebaseAuth;

        if (firebaseAuth.getCurrentUser() != null) {
            uiModelMutableLiveData.setValue(
                new MainUiModel(
                    getCurrentUserName(firebaseAuth.getCurrentUser().getDisplayName()),
                    getCurrentUserEmail(firebaseAuth.getCurrentUser().getEmail()),
                    getCurrentUserPhoto(firebaseAuth.getCurrentUser().getPhotoUrl())
                )
            );
        }
    }

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
        if(photo != null) url = photo;
        return url;
    }

    public void createUser() {
        FirebaseUser userValue = firebaseAuth.getCurrentUser();

        if(userValue != null) {
            String urlPicture = (userValue.getPhotoUrl() != null) ? userValue.getPhotoUrl().toString() : null;
            String username = userValue.getDisplayName();
            String uid = userValue.getUid();

            UserHelper.createUser(uid, username, urlPicture).addOnFailureListener(this.onFailureListener());
        }
    }

    // --------------------
    // ERROR HANDLER
    // --------------------

    protected OnFailureListener onFailureListener() {
        return e -> Toast.makeText(
                application,
                application.getString(R.string.error_unknown_error),
                Toast.LENGTH_LONG
        ).show();
    }

    public void onQueryTextChange(String newText) {
        if(myCurrentAutocompleteAsyncTask != null && !myCurrentAutocompleteAsyncTask.isCancelled()) {
            myCurrentAutocompleteAsyncTask.cancel(true);
        }
        myCurrentAutocompleteAsyncTask = new AutocompleteAsyncTask(newText, autocompleteRepository, new WeakReference<>(this));
        myCurrentAutocompleteAsyncTask.execute();
    }

    private static class AutocompleteAsyncTask extends AsyncTask<Void, Void, List<Prediction>> {

        private final String query;
        private final AutocompleteRepository autocompleteRepository;
        private final WeakReference<MainViewModel> mainViewModelWeakReference;

        private AutocompleteAsyncTask(String query,
                                      AutocompleteRepository autocompleteRepository,
                                      WeakReference<MainViewModel> mainViewModelWeakReference) {
            this.query = query;
            this.autocompleteRepository = autocompleteRepository;
            this.mainViewModelWeakReference = mainViewModelWeakReference;
        }

        @Override
        protected List<Prediction> doInBackground(Void... voids) {
            //TODO Rajouter currentLocation
            return null; //autocompleteRepository.getAutocompleteByLocation(query);
        }

        @Override
        protected void onPostExecute(List<Prediction> predictions) {
            if(mainViewModelWeakReference.get() != null) {
                mainViewModelWeakReference.get().onPredictionsChange(predictions);
            }
        }
    }

    private void onPredictionsChange(List<Prediction> predictions) {

    }
}
