package com.sursulet.go4lunch;

import android.net.Uri;

public class MainUiModel {

    String username;
    String mail;
    Uri photo;

    public MainUiModel(String username, String mail, Uri photo) {
        this.username = username;
        this.mail = mail;
        this.photo = photo;
    }
}
