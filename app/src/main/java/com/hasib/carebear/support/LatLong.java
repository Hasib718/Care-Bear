package com.hasib.carebear.support;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class LatLong implements Parcelable {
    private Double latitude;
    private Double longitude;

    public LatLong() {}

    public LatLong(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLong(Parcel parcel) {
        this.latitude = parcel.readDouble();
        this.longitude = parcel.readDouble();
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @NonNull
    @Override
    public String toString() {
        return latitude+","+longitude;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public static final Parcelable.Creator<LatLong> CREATOR = new Creator<LatLong>() {
        @Override
        public LatLong createFromParcel(Parcel source) {
            return new LatLong(source);
        }

        @Override
        public LatLong[] newArray(int size) {
            return new LatLong[size];
        }
    };
}
