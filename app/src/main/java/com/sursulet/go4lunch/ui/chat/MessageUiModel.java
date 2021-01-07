package com.sursulet.go4lunch.ui.chat;

import com.sursulet.go4lunch.model.User;

import java.time.LocalDate;
import java.util.Date;

public class MessageUiModel {

    private String message;
    private LocalDate dateCreated;
    private User userSender;
    private String urlImage;

    public MessageUiModel(String message, LocalDate dateCreated, User userSender, String urlImage) {
        this.message = message;
        this.dateCreated = dateCreated;
        this.userSender = userSender;
        this.urlImage = urlImage;
    }

    public String getMessage() { return message; }
    public LocalDate getDateCreated() { return dateCreated; }
    public User getUserSender() { return userSender; }
    public String getUrlImage() { return urlImage; }
}
