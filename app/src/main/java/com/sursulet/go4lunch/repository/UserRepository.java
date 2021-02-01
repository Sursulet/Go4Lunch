package com.sursulet.go4lunch.repository;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.api.UserHelper;
import com.sursulet.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    //private static final String TAG = UserRepository.class.getSimpleName();

    private final Application application;
    private final FirebaseAuth firebaseAuth;

    MutableLiveData<String> selectedQuery = new MutableLiveData<>();

    public UserRepository(Application application, FirebaseAuth firebaseAuth) {
        this.application = application;
        this.firebaseAuth = firebaseAuth;
    }

    public Boolean isCurrentUserLogged() {
        return firebaseAuth.getCurrentUser() != null;
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

    public LiveData<User> getCurrentUser() {
        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();
        FirebaseUser userValue = firebaseAuth.getCurrentUser();

        if (userValue != null) {
            String urlPicture = (userValue.getPhotoUrl() != null) ? userValue.getPhotoUrl().toString() : null;
            String username = userValue.getDisplayName();
            String uid = userValue.getUid();

            mutableLiveData.postValue(new User(uid, username, urlPicture));
        }

        return mutableLiveData;
    }

    public LiveData<User> getUser(String uid) {
        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();
        /*
        UserHelper.getUsersCollection().document(uid)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (value != null && value.exists()) {
                        Log.d(TAG, "Current data: " + value.getData());
                        User user = value.toObject(User.class);
                        mutableLiveData.postValue(user);
                    } else {
                        Log.d(TAG, "Current data: null");
                    }

                });

         */
        UserHelper.getUser(uid).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        User user = document.toObject(User.class);
                        mutableLiveData.postValue(user);
                    }
                });

        return mutableLiveData;
    }

    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> mutableLiveData = new MutableLiveData<>();

        UserHelper.getUsersCollection()
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        User user = documentSnapshot.toObject(User.class);
                        if (firebaseAuth.getCurrentUser() != null
                                && !(user.getUid().equals(firebaseAuth.getCurrentUser().getUid()))
                        ) { users.add(user); }
                    }

                    mutableLiveData.setValue(users);
                });

        return mutableLiveData;
    }

    public LiveData<String> getCurrentUserName() {
        FirebaseUser userValue = firebaseAuth.getCurrentUser();
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        if(userValue != null){
            String name = TextUtils.isEmpty(userValue.getDisplayName()) ? application.getString(R.string.info_no_username_found) : userValue.getDisplayName();
            mutableLiveData.setValue(name);
        }
        return mutableLiveData;
    }

    public LiveData<String> getCurrentUserEmail() {
        FirebaseUser userValue = firebaseAuth.getCurrentUser();
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        if(userValue != null){
            String email = TextUtils.isEmpty(userValue.getEmail()) ? application.getString(R.string.info_no_email_found) : userValue.getEmail();
            mutableLiveData.setValue(email);
        }
        return mutableLiveData;
    }

    public LiveData<String> getCurrentUserPhoto() {
        FirebaseUser userValue = firebaseAuth.getCurrentUser();
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

        if(userValue != null){
            Uri photo = userValue.getPhotoUrl();
            String url = null;
            if (photo != null) url = photo.toString();
            mutableLiveData.setValue(url);
        }
        return mutableLiveData;
    }

    public void setSelectedQuery(String text) {
        selectedQuery.setValue(text);
    }

    public LiveData<String> getSelectedQuery() {
        return selectedQuery;
    }

    protected OnFailureListener onFailureListener() {
        return e -> Toast.makeText(
                application,
                application.getString(R.string.error_unknown_error),
                Toast.LENGTH_LONG
        ).show();
    }
}
