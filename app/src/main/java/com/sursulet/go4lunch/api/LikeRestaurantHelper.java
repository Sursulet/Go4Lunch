package com.sursulet.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;

public class LikeRestaurantHelper {

    private static final String COLLECTION_NAME = "likeRestaurants";
    private static final String SUB_COLLECTION_NAME = "followers";

    public static CollectionReference getLikeRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createLikeRestaurant(
            String id, String name,
            String uid, String username, String urlPicture
    ) {
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        Restaurant likeRestaurantToCreate = new Restaurant(id, name);
        User userToCreate = new User(uid, username, urlPicture);

        batch.set(LikeRestaurantHelper.getLikeRestaurantsCollection().document(id)
                        .collection(SUB_COLLECTION_NAME).document(uid),
                userToCreate);

        batch.set(LikeRestaurantHelper.getLikeRestaurantsCollection().document(id),
                likeRestaurantToCreate);

        return batch.commit();
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getLikeRestaurant(String id) {
        return LikeRestaurantHelper.getLikeRestaurantsCollection().document(id).get();
    }

    public static Task<DocumentSnapshot> getFollower(String id, String uid) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(id)
                .collection(SUB_COLLECTION_NAME)
                .document(uid)
                .get();
    }

    // --- UPDATE ---

    // --- DELETE ---
    public static Task<Void> deleteFollower(String id, String uid) {
        return LikeRestaurantHelper.getLikeRestaurantsCollection()
                .document(id)
                .collection(SUB_COLLECTION_NAME)
                .document(uid).delete();
    }
}
