package com.sursulet.go4lunch.ui.detail;

import com.sursulet.go4lunch.ui.workmates.WorkmatesUiModel;

import java.util.List;

public class DetailPlaceUiModel {

    final String name;
    final String urlPhoto;
    final int isGoing;
    final String sentence;
    final float rating;
    final String phoneNumber;
    final int isLike;
    final String urlWebsite;
    final List<WorkmatesUiModel> workmates;
    final String openingHours;


    public DetailPlaceUiModel(
            String name,
            String urlPhoto,
            int isGoing,
            String sentence,
            float rating,
            String phoneNumber,
            int isLike,
            String urlWebsite,
            List<WorkmatesUiModel> workmates,
            String openingHours
    ) {
        this.name = name;
        this.urlPhoto = urlPhoto;
        this.isGoing = isGoing;
        this.sentence = sentence;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.isLike = isLike;
        this.urlWebsite = urlWebsite;
        this.workmates = workmates;
        this.openingHours = openingHours;
    }

    public String getName() {
        return name;
    }
    public String getUrlPhoto() { return urlPhoto; }
    public int getIsGoing() { return isGoing; }
    public String getSentence() {
        return sentence;
    }
    public float getRating() {
        return rating;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public int getIsLike() { return isLike; }
    public String getUrlWebsite() {
        return urlWebsite;
    }
    public List<WorkmatesUiModel> getWorkmates() { return workmates; }

    public String getOpeningHours() {
        return openingHours;
    }
}
