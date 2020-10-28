package com.sursulet.go4lunch.model;

public class Reviews {

    private String time;
    private String text;
    private String profile_time_desc;
    private String author_url;
    private String author_name;
    private String rating;
    private String language;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getProfile_time_desc() {
        return profile_time_desc;
    }

    public void setProfile_time_desc(String profile_time_desc) {
        this.profile_time_desc = profile_time_desc;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String toString() {
        return "ClassPojo ["+
                "time = " +time+
                "text = " +text+
                "profile_time_desc =" +profile_time_desc+
                "author_url = " +author_url+
                "author_name = " +author_name+
                "rating = " +rating+
                "language =" +language+
                "]";
    }
}
