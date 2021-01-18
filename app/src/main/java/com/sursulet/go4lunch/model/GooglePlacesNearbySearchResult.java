
package com.sursulet.go4lunch.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GooglePlacesNearbySearchResult {

    @SerializedName("next_page_token")
    @Expose
    private String nextPageToken;
    @SerializedName("nearbyResults")
    @Expose
    private List<NearbyResult> nearbyResults = null;
    @SerializedName("status")
    @Expose
    private String status;

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<NearbyResult> getNearbyResults() {
        return nearbyResults;
    }

    public void setNearbyResults(List<NearbyResult> nearbyResults) {
        this.nearbyResults = nearbyResults;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
