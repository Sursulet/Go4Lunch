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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User movie = (User) o;
        return uid.equals(movie.uid) &&
                placeId.equals(movie.placeId) &&
                username.equals(movie.username) &&
                avatarUrl.equals(movie.avatarUrl);
    }

    public static final DiffUtil.ItemCallback<User> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<User>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull User oldUser, @NonNull User newUser) {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    return oldUser.getUid().equals(newUser.getUid());
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull User oldUser, @NonNull User newUser) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldUser.equals(newUser);
                }
            };
}
