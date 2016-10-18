package com.igniva.indiecore.model;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-05 on 11/7/16.
 */
public class UsersPojo {

    private String userId;
    private String mobileNo;
    private String badges;
    private ProfilePojo profile;
    private CountryCodePojo location;

    public String getBadges() {
        return badges;
    }
    public void setBadges(String badges) {
        this.badges = badges;
    }
    public CountryCodePojo getLocation() {
        return location;
    }

    public void setLocation(CountryCodePojo location) {
        this.location = location;
    }


    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ProfilePojo getProfile() {
        return profile;
    }

    public void setProfile(ProfilePojo profile) {
        this.profile = profile;
    }



}


