package com.sursulet.go4lunch.ui;

public class DetailPlaceUiModel {

    String name;
    String urlPhoto;
    //String favorite;
    String txt;
    String ratingBar;
    String phoneNumber;
    String like;
    String urlWebsite;


    public DetailPlaceUiModel(String name, String urlPhoto, String txt) {
        this.name = name;
        this.urlPhoto = urlPhoto;
        this.txt = txt;
    }

    public String getName() {
        return name;
    }

    public String getUrlPhoto() { return urlPhoto; }

    public String getTxt() {
        return txt;
    }

    public String getRatingBar() {
        return ratingBar;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLike() {
        return like;
    }

    public String getUrlWebsite() {
        return urlWebsite;
    }
}
