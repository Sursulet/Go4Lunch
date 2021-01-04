package com.sursulet.go4lunch.ui.chat;

import com.sursulet.go4lunch.model.User;

import java.util.Date;

public class MessageUiModel {

    private String message;
    private Date dateCreated;
    private User userSender;
    private String urlImage;

    public MessageUiModel(String message, Date dateCreated, User userSender, String urlImage) {
        this.message = message;
        this.dateCreated = dateCreated;
        this.userSender = userSender;
        this.urlImage = urlImage;
    }

    public String getMessage() { return message; }
    public Date getDateCreated() { return dateCreated; }
    public User getUserSender() { return userSender; }
    public String getUrlImage() { return urlImage; }
}
