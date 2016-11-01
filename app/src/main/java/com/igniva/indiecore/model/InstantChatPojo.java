package com.igniva.indiecore.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by igniva-andriod-05 on 4/10/16.
 */
public class InstantChatPojo implements Parcelable {

    private String roomId;
    private String userId;
    private String type;
    private String text;
    private String media;
    private String thumb;
    private int status;
    private String messageId;
    private String name;
    private String icon;
    private String relation;
    private String badges;
    DateModel date_updated;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
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

    public DateModel getDate_updated() {
        return date_updated;
    }

    public void setDate_updated(DateModel date_updated) {
        this.date_updated = date_updated;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomId);
        dest.writeString(this.userId);
        dest.writeString(this.type);
        dest.writeString(this.text);
        dest.writeString(this.media);
        dest.writeString(this.thumb);
        dest.writeInt(this.status);
        dest.writeString(this.messageId);
        dest.writeString(this.name);
        dest.writeString(this.icon);
        dest.writeString(this.relation);
        dest.writeString(this.badges);
        dest.writeParcelable(this.date_updated, flags);
    }

    public InstantChatPojo() {
    }

    protected InstantChatPojo(Parcel in) {
        this.roomId = in.readString();
        this.userId = in.readString();
        this.type = in.readString();
        this.text = in.readString();
        this.media = in.readString();
        this.thumb = in.readString();
        this.status = in.readInt();
        this.messageId = in.readString();
        this.name = in.readString();
        this.icon = in.readString();
        this.relation = in.readString();
        this.badges = in.readString();
        this.date_updated = in.readParcelable(DateModel.class.getClassLoader());
    }

    public static final Parcelable.Creator<InstantChatPojo> CREATOR = new Parcelable.Creator<InstantChatPojo>() {
        @Override
        public InstantChatPojo createFromParcel(Parcel source) {
            return new InstantChatPojo(source);
        }

        @Override
        public InstantChatPojo[] newArray(int size) {
            return new InstantChatPojo[size];
        }
    };
}

