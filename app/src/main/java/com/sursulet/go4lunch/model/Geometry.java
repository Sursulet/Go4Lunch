package com.sursulet.go4lunch.model;

public class Geometry {

    private Viewport viewport;
    private Location location;

    public Viewport getViewport() { return viewport; }
    public void setViewport(Viewport viewport) { this.viewport = viewport; }

    public Location getLocation() { return  location; }
    public void setLocation(Location location) { this.location = location; }

    public String toString() {
        return "ClassPojo [viewport = "+viewport+", location = "+location+"]";
    }
}
