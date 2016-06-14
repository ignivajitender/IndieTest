package com.igniva.indiecore.model;

import java.util.ArrayList;

/**
 * Wrapper class -  to wrap ant response from webservice
 */
public class ResponsePojo {

    private int status;
    private String description;
    private String success;
    private ArrayList<BadgesPojo> badges;
    private int total_badges;
    private int friends_invited;
    private String error;
    private String userId;
    private String token;
    private String newUser;
    private ProfilePojo profile;

    public String getNewUser() {
        return newUser;
    }

    public void setNewUser(String newUser) {
        this.newUser = newUser;
    }

    public ProfilePojo getProfile() {
        return profile;
    }

    public void setProfile(ProfilePojo profile) {
        this.profile = profile;
    }







    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public int getTotal_badges() {
        return total_badges;
    }

    public void setTotal_badges(int total_badges) {
        this.total_badges = total_badges;
    }

    public int getFriends_invited() {
        return friends_invited;
    }

    public void setFriends_invited(int friends_invited) {
        this.friends_invited = friends_invited;
    }

    public ArrayList<BadgesPojo> getBadges() {
        return badges;
    }
    public void setBadges(ArrayList<BadgesPojo> badges) {
        this.badges = badges;
    }
    public String getError() {
        return error;
    }
    public String getSuccess() {
        return success;
    }
    public void setSuccess(String success) {
        this.success = success;
    }
    public void setError(String error) {
        this.error = error;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }


}
