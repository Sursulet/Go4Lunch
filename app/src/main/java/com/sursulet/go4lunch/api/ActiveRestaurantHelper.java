package com.sursulet.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;

public class ActiveRestaurantHelper {
    private static final String COLLECTION_NAME = "activeRestaurants";
    private static final String SUB_COLLECTION_NAME = "bookings";

    public static CollectionReference getActiveRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static CollectionReference getBookingsCollection(String restaurantId) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(restaurantId)
                .collection(SUB_COLLECTION_NAME);
    }

    // --- CREATE ---
    public static void createActiveRestaurant(String id, String name, String uid, String username, String urlPicture) {
        Restaurant activeRestaurantToCreate = new Restaurant(id, name);
        User userToCreate = new User(uid, username, urlPicture);

        ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(id)
                .collection(SUB_COLLECTION_NAME)
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

    public static Task<DocumentSnapshot> getActiveRestaurantBooking(
            String restaurantId,
            String userId
    ) {
        return ActiveRestaurantHelper.getBookingsCollection(restaurantId)
                .document(userId)
                .get();
    }

    public static CollectionReference getActiveRestaurantAllBookings(String restaurantId) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection().document(restaurantId)
                .collection(SUB_COLLECTION_NAME);
    }

    // --- DELETE ---

    public static Task<Void> deleteActiveRestaurantBooking(String restaurantId, String userId) {
        return ActiveRestaurantHelper.getBookingsCollection(restaurantId)
                .document(userId)
                .delete();
    }
}
