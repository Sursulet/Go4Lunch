package com.sursulet.go4lunch.model;

import java.time.LocalDate;
import java.util.Date;

public class Message {

    private String message;
    private LocalDate dateCreated;
    private User userSender;
    private User userReceiver;
    private String urlImage;

    public Message() {}

    public Message(String message, User userSender) {
        this.message = message;
        this.dateCreated = dateCreated;
        this.userSender = userSender;
        this.urlImage = urlImage;
    }

    public Message(String message, User userSender, String urlImage) {
        this.message = message;
        this.dateCreated = dateCreated;
        this.userSender = userSender;
        this.urlImage = urlImage;
    }

    // --- GETTERS ---
    public String getMessage() { return message; }
    public LocalDate getDateCreated() { return dateCreated; }
    public User getUserSender() { return userSender; }
    public String getUrlImage() { return urlImage; }

    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(LocalDate dateCreated) { this.dateCreated = dateCreated; }
    public void setUserSender(User userSender) { this.userSender = userSender; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
}
