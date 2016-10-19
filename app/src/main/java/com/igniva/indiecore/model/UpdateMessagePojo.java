package com.igniva.indiecore.model;

/**
 * Created by igniva-andriod-05 on 19/10/16.
 */
public class UpdateMessagePojo {
    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    //    +{"date_updated":{"$date":1476882859600},"unreadCount":0}
    int unreadCount;

}
