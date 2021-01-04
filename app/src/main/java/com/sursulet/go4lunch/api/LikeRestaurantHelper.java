package com.sursulet.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;

public class LikeRestaurantHelper {

    private static final String COLLECTION_NAME = "likeRestaurants";

    public static CollectionReference getLikeRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static void createLikeRestaurant(String id, String name, String uid, String username, String urlPicture) {
        Restaurant likeRestaurantToCreate = new Restaurant(id, name);
        User userToCreate = new User(uid, username, urlPicture);

        LikeRestaurantHelper.getLikeRestaurantsCollection()
                .document(id)
                .collection("users")
                .document(uid)
                .set(userToCreate);

        LikeRestaurantHelper.getLikeRestaurantsCollection()
                .document(id)
                .set(likeRestaurantToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getLikeRestaurant(String id){
        return LikeRestaurantHelper.getLikeRestaurantsCollection().document(id).get();
    }

    public static Task<DocumentSnapshot> getUserLikeRestaurant(String id, String uid) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(id)
                .collection("users")
                .document(uid)
                .get();
    }

    // --- UPDATE ---
    public static Task<Void> updateLike(String userId, String id, String restaurantName) {
        return LikeRestaurantHelper.getLikeRestaurantsCollection().document(id).update("username", restaurantName);
    }

    // --- DELETE ---
    public static Task<Void> deleteUserLikeRestaurant(String id, String uid) {
        return LikeRestaurantHelper.getLikeRestaurantsCollection()
                .document(id)
                .collection("users")
                .document(uid).delete();
    }
}
