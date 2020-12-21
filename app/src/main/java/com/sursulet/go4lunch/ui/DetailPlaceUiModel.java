package com.sursulet.go4lunch.ui;

import android.content.res.ColorStateList;

import com.sursulet.go4lunch.ui.workmates.WorkmatesUiModel;

import java.util.List;

public class DetailPlaceUiModel {

    String name;
    String urlPhoto;
    int isGoing;
    String txt;
    String ratingBar;
    String phoneNumber;
    int isLike;
    String urlWebsite;
    List<WorkmatesUiModel> workmates;


    public DetailPlaceUiModel(
            String name,
            String urlPhoto,
            int isGoing,
            String txt,
            String ratingBar,
            String phoneNumber,
            int isLike,
            String urlWebsite,
            List<WorkmatesUiModel> workmates
    ) {
        this.name = name;
        this.urlPhoto = urlPhoto;
        this.isGoing = isGoing;
        this.txt = txt;
        this.ratingBar = ratingBar;
        this.phoneNumber = phoneNumber;
        this.isLike = isLike;
        this.urlWebsite = urlWebsite;
        this.workmates = workmates;
    }

    public String getName() {
        return name;
    }
    public String getUrlPhoto() { return urlPhoto; }
    public int getIsGoing() { return isGoing; }
    public String getTxt() {
        return txt;
    }
    public String getRatingBar() {
        return ratingBar;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public int getIsLike() { return isLike; }
    public String getUrlWebsite() {
        return urlWebsite;
    }
    public List<WorkmatesUiModel> getWorkmates() { return workmates; }
}
