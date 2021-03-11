package com.sursulet.go4lunch.model;

public class Lockup {

    String restaurantId;

    public Lockup() {}

    public Lockup(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
}
