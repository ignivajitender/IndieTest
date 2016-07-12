package com.igniva.indiecore.model;

import java.io.Serializable;

/**
 * Created by igniva-andriod-05 on 12/7/16.
 */


public class Coordinate implements Serializable{

    private String latitude;
    private String longitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


}
