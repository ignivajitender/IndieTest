package com.igniva.indiecore.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by igniva-andriod-05 on 27/9/16.
 */
public class ChatPojo implements Parcelable, Comparable<ChatPojo> {


    public static final Parcelable.Creator<ChatPojo> CREATOR = new Parcelable.Creator<ChatPojo>() {
        @Override
        public ChatPojo createFromParcel(Parcel source) {
            return new ChatPojo(source);
        }

        @Override
        public ChatPojo[] newArray(int size) {
            return new ChatPojo[size];
        }
    };
    private int id_chat;
    private String _id;
    private String roomId;
    private String userId;
    private String type;
    private String text;
    private String media;
    private String thumb;
    private int status;
    private String created_at;
    private String date_updated;
    private String messageId;
    private String name;
    private String icon;
    private String relation;
    private String badges;
    private String imagePath;
    private boolean isVideoDownload;  // this field is only used for video.

    public ChatPojo() {
    }

    protected ChatPojo(Parcel in) {
        this.id_chat = in.readInt();
        this._id = in.readString();
        this.roomId = in.readString();
        this.userId = in.readString();
        this.type = in.readString();
        this.text = in.readString();
        this.media = in.readString();
        this.thumb = in.readString();
        this.status = in.readInt();
        this.created_at = in.readString();
        this.date_updated = in.readString();
        this.messageId = in.readString();
        this.name = in.readString();
        this.icon = in.readString();
        this.relation = in.readString();
        this.badges = in.readString();
        this.imagePath = in.readString();
    }

    public int getId_chat() {
        return id_chat;
    }

    public void setId_chat(int id_chat) {
        this.id_chat = id_chat;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public boolean isVideoDownload() {
        return isVideoDownload;
    }

    public void setVideoDownload(boolean videoDownload) {
        isVideoDownload = videoDownload;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id_chat);
        dest.writeString(this._id);
        dest.writeString(this.roomId);
        dest.writeString(this.userId);
        dest.writeString(this.type);
        dest.writeString(this.text);
        dest.writeString(this.media);
        dest.writeString(this.thumb);
        dest.writeInt(this.status);
        dest.writeString(this.created_at);
        dest.writeString(this.date_updated);
        dest.writeString(this.messageId);
        dest.writeString(this.name);
        dest.writeString(this.icon);
        dest.writeString(this.relation);
        dest.writeString(this.badges);
        dest.writeString(this.imagePath);
    }

    @Override
    public int compareTo(ChatPojo chatPojo) {
        if (id_chat > chatPojo.getId_chat()) {
            return 1;
        } else if (id_chat < chatPojo.getId_chat()) {
            return -1;
        } else {
            return 0;
        }
    }
}
