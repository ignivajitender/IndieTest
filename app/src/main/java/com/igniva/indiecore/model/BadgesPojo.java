package com.igniva.indiecore.model;

import java.io.Serializable;

/**
 * Created by igniva-andriod-05 on 17/6/16.
 */public class BadgesPojo implements Serializable {

    private String category;
    private String sku;
    private String price;
    private String icon;
    private String badgeId;
    private String name;
    private String description;
    private boolean isSelected;
    private int isSelectedAsMyBadge;
    private int isPremium;
    private int iSActive;
    private int sr_no;

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    private boolean newUser;

    public int getSr_no() {
        return sr_no;
    }

    public void setSr_no(int sr_no) {
        this.sr_no = sr_no;
    }



    public int getiSActive() {
        return iSActive;
    }

    public void setiSActive(int iSActive) {
        this.iSActive = iSActive;
    }

    public int getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(int isPremium) {
        this.isPremium = isPremium;
    }

    public int getIsSelectedAsMyBadge() {
        return isSelectedAsMyBadge;
    }

    public void setIsSelectedAsMyBadge(int isSelectedAsMyBadge) {
        this.isSelectedAsMyBadge = isSelectedAsMyBadge;
    }




    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSku() {
        return sku;
    }
public BadgesPojo(String some, String some1, String some2, int s, String some3, String some4, int  s1) {
    }
    public BadgesPojo(){

    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

