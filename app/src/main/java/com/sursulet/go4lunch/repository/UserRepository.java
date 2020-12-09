package com.sursulet.go4lunch.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sursulet.go4lunch.api.UserHelper;
import com.sursulet.go4lunch.model.User;

public class UserRepository {

    @Nullable
    public FirebaseUser currentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        MutableLiveData<FirebaseUser> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.postValue(currentUser());
        return mutableLiveData;
    }

    public LiveData<User> getUser(String uid) {
        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();

        UserHelper.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mutableLiveData.postValue(documentSnapshot.toObject(User.class));
            }
        });

        return mutableLiveData;
    }
}
