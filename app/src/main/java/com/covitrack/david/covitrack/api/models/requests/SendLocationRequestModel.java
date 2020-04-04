package com.covitrack.david.covitrack.api.models.requests;

/**
 * Rest model for sending client location
 */
public class SendLocationRequestModel extends BaseRequestModel {
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
