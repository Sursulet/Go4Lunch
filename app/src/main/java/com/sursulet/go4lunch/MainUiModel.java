package com.sursulet.go4lunch;

import android.net.Uri;

public class MainUiModel {

    String username;
    String email;
    Uri photoUrl;

    public MainUiModel(String username, String email, Uri photoUrl) {
        this.username = username;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }
}
