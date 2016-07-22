package com.igniva.indiecore.model;

import android.graphics.drawable.Drawable;

/**
 * Created by igniva-andriod-05 on 22/7/16.
 */
public class PremiumBadgePojo {

    private Drawable badgeIcon;
    private String badgeName;
    private String badgePrice;

    public Drawable getBadgeIcon() {
        return badgeIcon;
    }

    public void setBadgeIcon(Drawable badgeIcon) {
        this.badgeIcon = badgeIcon;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getBadgePrice() {
        return badgePrice;
    }

    public void setBadgePrice(String badgePrice) {
        this.badgePrice = badgePrice;
    }




}
