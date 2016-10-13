package com.igniva.indiecore.model;

/**
 * Created by igniva-andriod-05 on 13/10/16.
 */
public class CollectionChatPojo {

    private int type;
    private String last_message_Id;
    private String last_message_by;
    private String last_message_type;
    private String last_message;
    private String unreadCount;
    private String roomId;
    private String userId;
    private String name;
    private String icon;
    private String relation;
    private String badges;
    DateModel date_updated;


    public DateModel getDate_updated() {
        return date_updated;
    }

    public void setDate_updated(DateModel date_updated) {
        this.date_updated = date_updated;
    }


    public String getLast_message_type() {
        return last_message_type;
    }

    public void setLast_message_type(String last_message_type) {
        this.last_message_type = last_message_type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLast_message_Id() {
        return last_message_Id;
    }

    public void setLast_message_Id(String last_message_Id) {
        this.last_message_Id = last_message_Id;
    }

    public String getLast_message_by() {
        return last_message_by;
    }

    public void setLast_message_by(String last_message_by) {
        this.last_message_by = last_message_by;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(String unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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
