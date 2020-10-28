package com.sursulet.go4lunch.model;

public class Results {

    private String id;
    private String place_id;
    private String name;
    private String icon;
    private String vicinity;
    private String reference;
    private Geometry geometry;
    private Photos[] photos;
    private String[] types;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Photos[] getPhotos() {
        return photos;
    }

    public void setPhotos(Photos[] photos) {
        this.photos = photos;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return "ClassPojo [" +
                "id = " +id+
                "name = " +name+
                "icon = " +icon +
                "place_id = " +place_id+
                "reference = " +reference+
                "vicinity = " +vicinity +
                "geometry =" +geometry+
                "types = " +types+
                "photos=" +photos +
                "]";
    }
}
