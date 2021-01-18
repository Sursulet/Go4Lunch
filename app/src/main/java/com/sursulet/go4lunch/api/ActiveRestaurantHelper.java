package com.sursulet.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;

public class ActiveRestaurantHelper {
    private static final String COLLECTION_NAME = "activeRestaurants";
    private static final String SUB_COLLECTION_NAME = "bookings";

    public static CollectionReference getActiveRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static CollectionReference getBookingsCollection(String id) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection().document(id)
                .collection(SUB_COLLECTION_NAME);
    }

    public static Query getAllBookingsCollection() {
        return FirebaseFirestore.getInstance().collectionGroup(SUB_COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createActiveRestaurant(
            String id, String name,
            String uid, String username, String urlPicture
    ) {
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        Restaurant activeRestaurantToCreate = new Restaurant(id, name);
        User userToCreate = new User(uid, username, urlPicture);

        batch.set(ActiveRestaurantHelper.getActiveRestaurantsCollection().document(id),
                activeRestaurantToCreate);

        batch.set(ActiveRestaurantHelper.getActiveRestaurantsCollection().document(id)
                        .collection(SUB_COLLECTION_NAME).document(uid),
                userToCreate);

        return batch.commit();
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getActiveRestaurant(String id) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection().document(id).get();
    }

    public static Task<DocumentSnapshot> getBooking(String id, String uid) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(id)
                .collection(SUB_COLLECTION_NAME)
                .document(uid)
                .get();
    }

    // --- UPDATE ---

    // --- DELETE ---

    public static Task<Void> deleteBooking(String id, String uid) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(id)
                .collection(SUB_COLLECTION_NAME)
                .document(uid).delete();
    }
}
