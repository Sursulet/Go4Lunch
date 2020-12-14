package com.sursulet.go4lunch;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sursulet.go4lunch.api.UserHelper;
import com.sursulet.go4lunch.repository.UserRepository;

public class MainViewModel extends ViewModel {

    private final Application application;
    private final UserRepository userRepository;
    private final FirebaseAuth firebaseAuth;

    private final MutableLiveData<MainUiModel> uiModelMutableLiveData = new MutableLiveData<>();

    public MainViewModel(Application application, UserRepository userRepository, FirebaseAuth firebaseAuth) {
        this.application = application;
        this.userRepository = userRepository;
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
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(
                        application,
                        application.getString(R.string.error_unknown_error),
                        Toast.LENGTH_LONG
                ).show();
            }
        };
    }

}
