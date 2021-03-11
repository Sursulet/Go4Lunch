package com.sursulet.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LikeRestaurantHelper {

    private static final String COLLECTION_NAME = "likeRestaurants";
    private static final String SUB_COLLECTION_NAME = "followers";

    public static CollectionReference getLikeRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static CollectionReference getFollowersCollection(String restaurantId) {
        return LikeRestaurantHelper.getLikeRestaurantsCollection()
                .document(restaurantId)
                .collection(SUB_COLLECTION_NAME);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getLikeRestaurant(String id){
        return LikeRestaurantHelper.getLikeRestaurantsCollection().document(id).get();
    }

    public static Task<DocumentSnapshot> getLikeRestaurantFollower(
            String restaurantId,
            String userId
    ) {
        return LikeRestaurantHelper.getFollowersCollection(restaurantId)
                .document(userId)
                .get();
    }

}
