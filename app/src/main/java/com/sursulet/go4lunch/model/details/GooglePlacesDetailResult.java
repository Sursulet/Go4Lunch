
package com.sursulet.go4lunch.model.details;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GooglePlacesDetailResult {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("placeDetailResult")
    @Expose
    private PlaceDetailResult placeDetailResult;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public PlaceDetailResult getPlaceDetailResult() {
        return placeDetailResult;
    }

    public void setPlaceDetailResult(PlaceDetailResult placeDetailResult) {
        this.placeDetailResult = placeDetailResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
