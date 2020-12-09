package com.sursulet.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sursulet.go4lunch.model.Restaurant;

public class RestaurantHelper {
    private static final String COLLECTION_NAME = "restaurants";

    public static CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createRestaurant(String id, String userId) {
        Restaurant restaurantToCreate = new Restaurant(id, userId);
        return RestaurantHelper.getRestaurantsCollection().document(id).set(restaurantToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getRestaurant(String id) {
        return RestaurantHelper.getRestaurantsCollection().document(id).get();
    }

    // --- DELETE ---
    public static Task<Void> deleteRestaurant(String id) {
        return RestaurantHelper.getRestaurantsCollection().document(id).delete();
    }
}
