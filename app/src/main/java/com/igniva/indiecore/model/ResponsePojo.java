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

    public ArrayList<PostPojo> getPostList() {
        return postList;
    }

    public void setPostList(ArrayList<PostPojo> postList) {
        this.postList = postList;
    }

    private ArrayList<PostPojo> postList;
    private String postId;
    private ArrayList<UsersPojo> users;
    private int total_badges;
    private int active;
    private int friends_invited;
    private String error;
    private String userId;
    private String token;
    private String newUser;
    private ProfilePojo profile;
    private ArrayList<BusinessPojo> business_list;
    private ArrayList<CommentPojo> commentList;


    private int like;

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getNeutral() {
        return neutral;
    }

    public void setNeutral(int neutral) {
        this.neutral = neutral;
    }

    private int dislike;
    private int neutral;



    public ArrayList<CommentPojo> getCommentList() {
        return commentList;
    }

    public void setCommentList(ArrayList<CommentPojo> commentList) {
        this.commentList = commentList;
    }





    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public ArrayList<UsersPojo> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UsersPojo> users) {
        this.users = users;
    }
    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
    public int getBadge_status() {
        return badge_status;
    }

    public void setBadge_status(int badge_status) {
        this.badge_status = badge_status;
    }

    private  int badge_status;

    public ArrayList<BusinessPojo> getBusiness_list() {
        return business_list;
    }

    public void setBusiness_list(ArrayList<BusinessPojo> business_list) {
        this.business_list = business_list;
    }








    public String getError_text() {
        return error_text;
    }

    public void setError_text(String error_text) {
        this.error_text = error_text;
    }

    private String error_text;

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
