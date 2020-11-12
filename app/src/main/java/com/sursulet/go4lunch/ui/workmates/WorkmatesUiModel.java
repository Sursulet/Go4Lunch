package com.sursulet.go4lunch.ui.workmates;

import android.net.Uri;

public class WorkmatesUiModel {

    String txt;
    Uri photo;

    public WorkmatesUiModel(String txt, Uri photo) {
        this.txt = txt;
        this.photo = photo;
    }

    public String getTxt() {
        return txt;
    }

    public Uri getPhoto() {
        return photo;
    }
}
