package com.sursulet.go4lunch.ui;

public class DetailPlaceUiModel {

    String name;
    String txt_type_address;
    //String rating;
    //String favorite;
    //String call;
    //String like;
    //String website;


    public DetailPlaceUiModel(String name, String txt_type_address) {
        this.name = name;
        this.txt_type_address = txt_type_address;
    }

    public String getName() {
        return name;
    }

    public String getTxt_type_address() {
        return txt_type_address;
    }
}
