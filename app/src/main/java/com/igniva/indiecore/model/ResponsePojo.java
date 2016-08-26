package com.igniva.indiecore.model;

import java.util.ArrayList;

/**
 * Wrapper class -  to wrap ant response from webservice
 */
public class ResponsePojo {

    private boolean  is_favourite;
    private boolean  is_block;
    private int status;
    private int total_badges;
    private int active;
    private int friends_invited;
    private int like;
    private int dislike;
    private int neutral;
    private int business_count;
    private int total_count;
    private int totalPosts;
    private int badgeLimit;
    private int totalComments;
    private int badgesGot;
    private String  relation;
    private String description;
    private String success;
    private String postId;
    private String error;
    private String userId;
    private String token;
    private String newUser;
    private ProfilePojo profile;
    private UserPojo user;
    private CountryCodePojo location;
    private ArrayList<BadgesPojo> badges;
    private ArrayList<PostPojo> postList;
    private ArrayList<UsersPojo> users;
    private ArrayList<BusinessPojo> business_list;
    private ArrayList<CommentPojo> commentList;
    private ArrayList<RepliesPojo> repliesList;

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public UserPojo getUser() {
        return user;
    }

    public void setUser(UserPojo user) {
        this.user = user;
    }

    public int getBadgesGot() {
        return badgesGot;
    }

    public void setBadgesGot(int badgesGot) {
        this.badgesGot = badgesGot;
    }

    public int getBadgeLimit() {
        return badgeLimit;
    }

    public void setBadgeLimit(int badgeLimit) {
        this.badgeLimit = badgeLimit;
    }

    public int getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(int totalPosts) {
        this.totalPosts = totalPosts;
    }

    public int getBusiness_count() {
        return business_count;
    }

    public void setBusiness_count(int business_count) {
        this.business_count = business_count;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public CountryCodePojo getLocation() {
        return location;
    }

    public void setLocation(CountryCodePojo location) {
        this.location = location;
    }

    public boolean is_favourite() {
        return is_favourite;
    }

    public void setIs_favourite(boolean is_favourite) {
        this.is_favourite = is_favourite;
    }

    public boolean is_block() {
        return is_block;
    }

    public void setIs_block(boolean is_block) {
        this.is_block = is_block;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public ArrayList<PostPojo> getPostList() {
        return postList;
    }

    public void setPostList(ArrayList<PostPojo> postList) {
        this.postList = postList;
    }

    public ArrayList<RepliesPojo> getRepliesList() {
        return repliesList;
    }

    public void setRepliesList(ArrayList<RepliesPojo> repliesList) {
        this.repliesList = repliesList;
    }

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
