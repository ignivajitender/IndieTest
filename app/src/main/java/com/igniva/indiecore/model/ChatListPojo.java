package com.igniva.indiecore.model;

/**
 * Created by igniva-andriod-05 on 26/9/16.
 */
public class ChatListPojo {

    private String _id ;
    private int type ;
    private String created_at ;
    private String date_updated ;
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


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public String getLast_message_type() {
        return last_message_type;
    }

    public void setLast_message_type(String last_message_type) {
        this.last_message_type = last_message_type;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDate_updated() {
        return date_updated;
    }

    public void setDate_updated(String date_updated) {
        this.date_updated = date_updated;
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
