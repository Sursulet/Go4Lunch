package com.sursulet.go4lunch.model;

public class Restaurant {

    String id;
    String userId;
    boolean isLike = false;
    boolean isGoing = false;

    public Restaurant(String id, String userId) {
        this.id = id;
        this.userId = userId;
        this.isLike = isLike;
    }

    // -- GETTERS
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public boolean isLike() { return isLike; }
    public boolean isGoing() { return isGoing; }

    // -- SETTERS
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setLike(boolean like) { isLike = like; }
    public void setGoing(boolean going) { isGoing = going; }
}
