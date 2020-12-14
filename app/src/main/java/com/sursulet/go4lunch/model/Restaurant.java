package com.sursulet.go4lunch.model;

public class Restaurant {

    String id;
    String name;

    public Restaurant() {}

    public Restaurant(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // -- GETTERS
    public String getId() { return id; }
    public String getName() { return name; }

    // -- SETTERS
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}
