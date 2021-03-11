package com.sursulet.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sursulet.go4lunch.model.User;

import java.time.LocalDate;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String LOCKUP = LocalDate.now() + "_UsersActiveRestaurants";

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static CollectionReference getLockupCollection() {
        return FirebaseFirestore.getInstance().collection(LOCKUP);
    }

    // --- CREATE ---
    public static Task<Void> createUser(String uid, String username, String urlPicture) {
        User userToCreate = new User(uid, username, urlPicture);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Task<DocumentSnapshot> getLockupUser(String uid){
        return UserHelper.getLockupCollection().document(uid).get();
    }

}
