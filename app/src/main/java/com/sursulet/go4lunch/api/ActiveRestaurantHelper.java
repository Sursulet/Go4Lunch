package com.sursulet.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;

public class ActiveRestaurantHelper {
    private static final String COLLECTION_NAME = "activeRestaurants";

    public static CollectionReference getActiveRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static void createActiveRestaurant(String id, String name, String uid, String username, String urlPicture) {
        Restaurant activeRestaurantToCreate = new Restaurant(id, name);
        User userToCreate = new User(uid, username, urlPicture);

        ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(id)
                .collection("users")
                .document(uid)
                .set(userToCreate);

        ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(id)
                .set(activeRestaurantToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getActiveRestaurant(String id) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection().document(id).get();
    }

    public static Task<DocumentSnapshot> getUserActiveRestaurant(String id, String uid) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(id)
                .collection("users")
                .document(uid)
                .get();
    }

    public static CollectionReference getUsersCollectionActiveRestaurant(String id) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection().document(id)
                .collection("users");
    }

    // --- UPDATE ---
    public static Task<Void> updateUserIds(String id, String userId) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(id)
                .update("users", FieldValue.arrayUnion(userId));
    }

    public static Task<Void> deleteUserId(String id, String userId) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(id)
                .update("users", FieldValue.arrayRemove(userId));
    }

    // --- DELETE ---
    public static Task<Void> deleteActiveRestaurant(String id) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection().document(id).delete();
    }

    public static Task<Void> deleteUserActiveRestaurant(String id, String uid) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(id)
                .collection("users")
                .document(uid).delete();
    }
}
