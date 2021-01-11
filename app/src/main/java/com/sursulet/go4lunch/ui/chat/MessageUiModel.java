package com.sursulet.go4lunch.ui.chat;

import com.sursulet.go4lunch.model.User;

public class MessageUiModel {

    private final String message;
    private final String dateCreated;
    private final User userSender;

    public MessageUiModel(String message, String dateCreated, User userSender) {
        this.message = message;
        this.dateCreated = dateCreated;
        this.userSender = userSender;
    }

    public String getMessage() { return message; }
    public String getDateCreated() { return dateCreated; }
    public User getUserSender() { return userSender; }
}
