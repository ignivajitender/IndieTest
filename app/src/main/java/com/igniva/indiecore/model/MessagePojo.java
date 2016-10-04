package com.igniva.indiecore.model;

/**
 * Created by igniva-andriod-05 on 22/9/16.
 */
public class MessagePojo {
    String type;
    String last_message_Id;
    String last_message_by;
    String last_message_type;
    String last_message;
    int unreadCount;
    String media;
    String thumb;
    String roomId;
    String userId;
    String name;
    String badges;
    String icon;
    String relation;
    String text;
    int status;
    String created_at;
    DateModel date_updated;

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public DateModel getDate_updated() {
        return date_updated;
    }

    public void setDate_updated(DateModel date_updated) {
        this.date_updated = date_updated;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getBadges() {
        return badges;
    }

    public void setBadges(String badges) {
        this.badges = badges;
    }


    public String getLast_message_type() {
        return last_message_type;
    }

    public void setLast_message_type(String last_message_type) {
        this.last_message_type = last_message_type;
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

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
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


}
