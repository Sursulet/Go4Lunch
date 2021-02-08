package com.sursulet.go4lunch.model;

public class Restaurant {

    String id;
    String name;
    String address;

    public Restaurant() {}

    public Restaurant(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    // -- GETTERS
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }

    // -- SETTERS
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
}
