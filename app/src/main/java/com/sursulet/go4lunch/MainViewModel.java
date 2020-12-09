package com.sursulet.go4lunch;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.sursulet.go4lunch.repository.UserRepository;

public class MainViewModel extends ViewModel {

    private final Application application;
    UserRepository userRepository;
    private MutableLiveData<MainUiModel> mutableLiveData;

    public MainViewModel(Application application, UserRepository userRepository) {
        this.application = application;
        this.userRepository = userRepository;
    }

    public void launchSingInActivity() {
        application.startActivity(new Intent(application, SignInActivity.class));
    }

    public LiveData<MainUiModel> getUiModelLiveData() {

        return Transformations.map(userRepository.getCurrentUser(),
                new Function<FirebaseUser, MainUiModel>() {
            @Override
            public MainUiModel apply(FirebaseUser result) {

                return new MainUiModel(
                        getCurrentUserName(result.getDisplayName()),
                        getCurrentUserEmail(result.getEmail()),
                        getCurrentUserPhoto(result.getPhotoUrl())
                );
            }
        });
    }

    private String getCurrentUserName(String name) {
        String username = TextUtils.isEmpty(name) ?
                application.getString(R.string.info_no_username_found) : name;

        return username;
    }

    private String getCurrentUserEmail(String email) {
        String userEmail = TextUtils.isEmpty(email) ?
                application.getString(R.string.info_no_email_found) : email;

        return userEmail;
    }

    private Uri getCurrentUserPhoto(Uri photo) {
        Uri url = null;

        if(photo != null) {
            url = photo;
        }

        return url;
    }

    public boolean isLogged() {
        return userRepository.isCurrentUserLogged();
    }
}
