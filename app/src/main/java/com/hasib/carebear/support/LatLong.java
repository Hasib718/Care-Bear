package com.hasib.carebear.support;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LatLong implements Parcelable {

    private static final String TAG = "LatLong";

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

    public static LatLong castLatLngToCustomLatLongClass(LatLng latLng) {
        return new LatLong(latLng.latitude, latLng.longitude);
    }

    public static LatLng castCustomLatLongClassToLatLng(LatLong latLong) {
        return new LatLng(latLong.latitude, latLong.longitude);
    }

    public static String geoCoding(Context context, LatLng latLng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        String addressLine = "";

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                Log.d(TAG, "geoCoding: " + addressList.get(0).toString());

                addressLine = addressList.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressLine;
    }

    public static String geoCoding(Context context, LatLong latLng) {
        return geoCoding(context, new LatLng(latLng.getLatitude(),latLng.getLongitude()));
    }

    public static String geoCoding(Context context, double latitude, double longitude) {
        return geoCoding(context, new LatLng(latitude, longitude));
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
