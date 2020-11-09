package com.sursulet.go4lunch.model;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    @Nullable private String avatarUrl;
    private String placeId;

    public User(String uid, String username, @Nullable String avatarUrl/*, String placeId*/) {
        this.uid = uid;
        this.username = username;
        this.avatarUrl = avatarUrl;
        //this.placeId = placeId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(@Nullable String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
