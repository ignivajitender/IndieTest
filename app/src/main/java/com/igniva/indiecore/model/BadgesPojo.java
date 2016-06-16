package com.igniva.indiecore.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.StringBuilderPrinter;

import java.io.Serializable;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class BadgesPojo implements Parcelable {

    private String category;
    private String sku;
    private String price;
    private String icon;
    private String badgeId;
    private String name;
    private String description;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSku() {
        return sku;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(category);
        dest.writeString(sku);
        dest.writeString(price);
        dest.writeString(icon);
        dest.writeString(badgeId);
        dest.writeString(name);
        dest.writeString(description);

    }

    public BadgesPojo(String icon,String name,String description){

//        this.category= category;
//        this.sku=sku;
//        this.price=price;
        this.icon=icon;
//        this.badgeId=badgeId;
        this.name=name;
        this.description=description;
    }

    private BadgesPojo(Parcel in){
//        this.category= in.readString();
//        this.sku=in.readString();
//        this.price=in.readString();
        this.icon=in.readString();
//        this.badgeId=in.readString();
        this.name=in.readString();
        this.description=in.readString();
    }

    public static final Parcelable.Creator<BadgesPojo> CREATOR = new Parcelable.Creator<BadgesPojo>() {

        @Override
        public BadgesPojo createFromParcel(Parcel source) {
            return new BadgesPojo(source);
        }

        @Override
        public BadgesPojo[] newArray(int size) {
            return new BadgesPojo[size];
        }
    };
}
