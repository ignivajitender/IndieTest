package com.igniva.indiecore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by igniva-andriod-05 on 4/7/16.
 */
public class BusinessPojo implements Serializable {

    private String name;
    private int badge_status;
    private LocationPojo location;
    private String image_url;
    private double distance;
    private String business_id;
    private String user_count;
    private String phone;
    private String display_phone;
    private String snippet_text;
    private String rating_img_url;

    public String getRating_img_url() {
        return rating_img_url;
    }

    public void setRating_img_url(String rating_img_url) {
        this.rating_img_url = rating_img_url;
    }


    public String getSnippet_text() {
        return snippet_text;
    }

    public void setSnippet_text(String snippet_text) {
        this.snippet_text = snippet_text;
    }



    public String getDisplay_phone() {
        return display_phone;
    }

    public void setDisplay_phone(String display_phone) {
        this.display_phone = display_phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    //    private String distance;


    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }





    public int getBadge_status() {
        return badge_status;
    }

    public void setBadge_status(int badge_status) {
        this.badge_status = badge_status;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }


    public String getUser_count() {
        return user_count;
    }

    public void setUser_count(String user_count) {
        this.user_count = user_count;
    }

    public LocationPojo getLocation() {
        return location;
    }

    public void setLocation(LocationPojo location) {
        this.location = location;
    }


}
