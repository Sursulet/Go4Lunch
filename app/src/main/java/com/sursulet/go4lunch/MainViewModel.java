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
import com.google.firebase.auth.FirebaseUser;
import com.sursulet.go4lunch.api.UserHelper;
import com.sursulet.go4lunch.repository.UserRepository;

public class MainViewModel extends ViewModel {

    private final Application application;
    UserRepository userRepository;

    // -- DATA
    private  final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();

    public MainViewModel(Application application, UserRepository userRepository) {
        this.application = application;
        this.userRepository = userRepository;

        currentUser.setValue(userRepository.getCurrentUser());
    }

    public Boolean isCurrentUserLogged() {
        return (currentUser.getValue() != null);
    }

    public LiveData<MainUiModel> getUiModelLiveData() {
        return Transformations.map(currentUser,
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

    public void launchSingInActivity() {
        application.startActivity(new Intent(application, SignInActivity.class));
    }

    public void createUser() {
        FirebaseUser userValue = currentUser.getValue();

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
