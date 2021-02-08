package com.sursulet.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;

public class ActiveRestaurantHelper {
    private static final String COLLECTION_NAME = "activeRestaurants";
    private static final String SUB_COLLECTION_NAME = "bookings";

    public static CollectionReference getActiveRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(LocalDate.now() + "_" + COLLECTION_NAME);
    }

    public static CollectionReference getBookingsCollection(String restaurantId) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(restaurantId)
                .collection(SUB_COLLECTION_NAME);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getActiveRestaurant(String id) {
        return ActiveRestaurantHelper.getActiveRestaurantsCollection().document(id).get();
    }

    public static Task<QuerySnapshot> getBooking(String userId) {
        return FirebaseFirestore.getInstance()
                .collectionGroup("bookings")
                .whereEqualTo("uid", userId)
                .get();
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
        return ActiveRestaurantHelper.getActiveRestaurantsCollection()
                .document(restaurantId)
                .collection(SUB_COLLECTION_NAME);
    }

}
