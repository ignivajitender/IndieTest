package com.igniva.indiecore.model;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-05 on 17/10/16.
 */
public class PeoplesPojo {

    private String userId;
    private String mobileNo;
    private String relation;
    private String  badges;
    private ProfilePojo profile;
    private CountryCodePojo location;

    public ProfilePojo getProfile() {
        return profile;
    }

    public void setProfile(ProfilePojo profile) {
        this.profile = profile;
    }

    public CountryCodePojo getLocation() {
        return location;
    }

    public void setLocation(CountryCodePojo location) {
        this.location = location;
    }






    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }


    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getBadges() {
        return badges;
    }

    public void setBadges(String badges) {
        this.badges = badges;
    }




}
