package com.sursulet.go4lunch.model;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    @Nullable private String avatarUrl;

    public User() {}

    public User(String uid, String username, @Nullable String avatarUrl) {
        this.uid = uid;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    public String getUid() { return uid; }

    public String getUsername() { return username; }

    @Nullable
    public String getAvatarUrl() { return avatarUrl; }

}
