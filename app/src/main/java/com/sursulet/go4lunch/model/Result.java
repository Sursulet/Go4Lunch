package com.sursulet.go4lunch.model;

public class Result {

    private String id;
    private String name;
    private String icon;
    private String place_id;
    private Reviews[] reviews;
    private String scope;
    private String website;
    private String int_phone_nb;
    private String address;
    private String url;
    private String reference;
    private Geometry geometry;
    private Opening_Hours opening_hours;
    private Photos[] photos;
    private String vicinity;
    private String ratings;
    private String[] types;
    private String[] formatted_phone_number;
    private String[] formatted_address;
    private Address_components[] address_components;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public Reviews[] getReviews() {
        return reviews;
    }

    public void setReviews(Reviews[] reviews) {
        this.reviews = reviews;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getInt_phone_nb() {
        return int_phone_nb;
    }

    public void setInt_phone_nb(String int_phone_nb) {
        this.int_phone_nb = int_phone_nb;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Opening_Hours getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(Opening_Hours opening_hours) {
        this.opening_hours = opening_hours;
    }

    public Photos[] getPhotos() {
        return photos;
    }

    public void setPhotos(Photos[] photos) {
        this.photos = photos;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String[] getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public void setFormatted_phone_number(String[] formatted_phone_number) {
        this.formatted_phone_number = formatted_phone_number;
    }

    public String[] getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String[] formatted_address) {
        this.formatted_address = formatted_address;
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
