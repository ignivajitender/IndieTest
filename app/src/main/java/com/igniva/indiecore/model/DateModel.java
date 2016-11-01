package com.igniva.indiecore.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by igniva-andriod-05 on 3/10/16.
 */
public class DateModel implements Parcelable {
    public String get$date() {
        return $date;
    }

    public void set$date(String $date) {
        this.$date = $date;
    }

    private String  $date;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.$date);
    }

    public DateModel() {
    }

    protected DateModel(Parcel in) {
        this.$date = in.readString();
    }

    public static final Parcelable.Creator<DateModel> CREATOR = new Parcelable.Creator<DateModel>() {
        @Override
        public DateModel createFromParcel(Parcel source) {
            return new DateModel(source);
        }

        @Override
        public DateModel[] newArray(int size) {
            return new DateModel[size];
        }
    };
}
