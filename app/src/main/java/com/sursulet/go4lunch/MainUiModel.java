package com.sursulet.go4lunch;

public class MainUiModel {

    final String username;
    final String email;
    final String photoUrl;

    public MainUiModel(String username, String email, String photoUrl) {
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

    public String getPhotoUrl() {
        return photoUrl;
    }
}
