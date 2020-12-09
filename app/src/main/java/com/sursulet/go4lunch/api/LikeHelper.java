package com.sursulet.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.sursulet.go4lunch.model.Restaurant;

public class LikeHelper {

    private static final String COLLECTION_NAME = "likes";

    public static CollectionReference getLikeRestaurantsCollection(String userId) {
        return UserHelper.getUsersCollection().document(userId).collection(COLLECTION_NAME);
    }

    public Query getAllLikesForUser(String userId) {
        return UserHelper.getUsersCollection()
                .document(userId)
                .collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createLikeRestaurant(String id, String userId, String urlPicture) {
        Restaurant likeRestaurantToCreate = new Restaurant(id, userId);
        return LikeHelper.getLikeRestaurantsCollection(userId)
                .document(id)
                .set(likeRestaurantToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getLike(String userId, String id){
        return LikeHelper.getLikeRestaurantsCollection(userId).document(id).get();
    }

    // --- UPDATE ---
    public static Task<Void> updateLike(String userId, String id, String restaurantName) {
        return LikeHelper.getLikeRestaurantsCollection(userId).document(id).update("username", restaurantName);
    }

    // --- DELETE ---
    public static Task<Void> deleteLike(String userId, String id) {
        return LikeHelper.getLikeRestaurantsCollection(userId).document(id).delete();
    }
}
