package com.sursulet.go4lunch.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

public class User {

    private String uid;
    private String placeId;
    private String username;
    @Nullable private String avatarUrl;

    public User(String placeId, String username, @Nullable String avatarUrl) {
        this.placeId = placeId;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Nullable
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(@Nullable String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }
}
