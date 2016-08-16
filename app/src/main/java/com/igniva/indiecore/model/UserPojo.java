package com.igniva.indiecore.model;

/**
 * Created by igniva-andriod-05 on 16/8/16.
 */
public class UserPojo {

    private int badgeLimit;
    private int selectedBadgeCount;
    private int  badgesGot;

    public int getSelectedBadgeCount() {
        return selectedBadgeCount;
    }

    public void setSelectedBadgeCount(int selectedBadgeCount) {
        this.selectedBadgeCount = selectedBadgeCount;
    }

    public int getBadgeLimit() {
        return badgeLimit;
    }

    public void setBadgeLimit(int badgeLimit) {
        this.badgeLimit = badgeLimit;
    }

    public int getBadgesGot() {
        return badgesGot;
    }

    public void setBadgesGot(int badgesGot) {
        this.badgesGot = badgesGot;
    }




}
