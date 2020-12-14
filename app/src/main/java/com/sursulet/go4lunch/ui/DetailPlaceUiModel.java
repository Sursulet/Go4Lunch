package com.sursulet.go4lunch.ui;

import com.sursulet.go4lunch.ui.workmates.WorkmatesUiModel;

import java.util.List;

public class DetailPlaceUiModel {

    String name;
    String urlPhoto;
    //String favorite;
    String txt;
    String ratingBar;
    String phoneNumber;
    String like;
    String urlWebsite;
    List<WorkmatesUiModel> workmates;


    public DetailPlaceUiModel(String name, String urlPhoto, String txt, List<WorkmatesUiModel> workmates) {
        this.name = name;
        this.urlPhoto = urlPhoto;
        this.txt = txt;
        this.workmates = workmates;
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

    public List<WorkmatesUiModel> getWorkmates() { return workmates; }
}
