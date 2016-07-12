package com.igniva.indiecore.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by igniva-andriod-05 on 5/7/16.
 */
public class LocationPojo implements Serializable{

    private ArrayList<String> address;
    private Coordinate coordinate;


    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }


    public ArrayList<String> getAddress() {
        return address;
    }

    public void setAddress(ArrayList<String> address) {
        this.address = address;
    }

}
