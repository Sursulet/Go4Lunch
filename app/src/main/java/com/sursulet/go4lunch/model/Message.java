package com.sursulet.go4lunch.model;

import java.util.Date;

public class Message {

    private String message;
    private Date dateCreated;
    private User userSender;
    private String urlImage;

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
    public Date getDateCreated() { return dateCreated; }
    public User getUserSender() { return userSender; }
    public String getUrlImage() { return urlImage; }

    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUserSender(User userSender) { this.userSender = userSender; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
}
