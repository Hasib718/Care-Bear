package com.tinvodai.carebear.doctor.container;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.tinvodai.carebear.support.LatLong;

import java.util.HashMap;
import java.util.Map;

public class Chamber implements Parcelable {
    //Container for storing chamberName & address
    private String chamberName;
    private String chamberFees;
    private String chamberAddress;
    private LatLong chamberLatLng;
    private String chamberTime;
    private HashMap chamberOpenDays;
    private String doctorUserProfileId;
    private String chamberDatabaseId;
    private String chamberDatabaseIdKeyInDoctorProfile;

    public Chamber() {}

    public Chamber(String chamberName, String chamberFees, String chamberAddress,
                   LatLong chamberLatLng, String chamberTime, Map<String, Boolean> chamberOpenDays) {
        this.chamberName = chamberName;
        this.chamberFees = chamberFees;
        this.chamberAddress = chamberAddress;
        this.chamberLatLng = chamberLatLng;
        this.chamberTime = chamberTime;
        this.chamberOpenDays = (HashMap) chamberOpenDays;
    }

    public Chamber(String chamberName, String chamberFees, String chamberAddress,
                   LatLong chamberLatLng, String chamberTime, Map<String, Boolean> chamberOpenDays,
                   String doctorUserProfileId, String chamberDatabaseId, String chamberDatabaseIdKeyInDoctorProfile) {
        this.chamberName = chamberName;
        this.chamberFees = chamberFees;
        this.chamberAddress = chamberAddress;
        this.chamberLatLng = chamberLatLng;
        this.chamberTime = chamberTime;
        this.chamberOpenDays = (HashMap) chamberOpenDays;
        this.doctorUserProfileId = doctorUserProfileId;
        this.chamberDatabaseId = chamberDatabaseId;
        this.chamberDatabaseIdKeyInDoctorProfile = chamberDatabaseIdKeyInDoctorProfile;
    }

    public Chamber(Parcel parcel) {
        this.chamberName = parcel.readString();
        this.chamberFees = parcel.readString();
        this.chamberAddress = parcel.readString();
        Bundle bundle = parcel.readBundle();
        this.chamberLatLng = bundle.getParcelable("latlng");
        this.chamberTime = parcel.readString();
        this.chamberOpenDays = parcel.readHashMap(HashMap.class.getClassLoader());
        this.doctorUserProfileId = parcel.readString();
        this.chamberDatabaseId = parcel.readString();
        this.chamberDatabaseIdKeyInDoctorProfile = parcel.readString();
    }

    public String getChamberName() {
        return chamberName;
    }

    public void setChamberName(String chamberName) {
        this.chamberName = chamberName;
    }

    public String getChamberFees() {
        return chamberFees;
    }

    public void setChamberFees(String chamberFees) {
        this.chamberFees = chamberFees;
    }

    public String getChamberAddress() {
        return chamberAddress;
    }

    public void setChamberAddress(String chamberAddress) {
        this.chamberAddress = chamberAddress;
    }

    public LatLong getChamberLatLng() {
        return chamberLatLng;
    }

    public void setChamberLatLng(LatLong chamberLatLng) {
        this.chamberLatLng = chamberLatLng;
    }

    public String getChamberTime() {
        return chamberTime;
    }

    public void setChamberTime(String chamberTime) {
        this.chamberTime = chamberTime;
    }

    public Map<String, Boolean> getChamberOpenDays() {
        return chamberOpenDays;
    }

    public void setChamberOpenDays(Map<String, Boolean> chamberOpenDays) {
        this.chamberOpenDays = (HashMap) chamberOpenDays;
    }

    public String getDoctorUserProfileId() {
        return doctorUserProfileId;
    }

    public void setDoctorUserProfileId(String doctorUserProfileId) {
        this.doctorUserProfileId = doctorUserProfileId;
    }

    public String getChamberDatabaseId() {
        return chamberDatabaseId;
    }

    public void setChamberDatabaseId(String chamberDatabaseId) {
        this.chamberDatabaseId = chamberDatabaseId;
    }

    public String getChamberDatabaseIdKeyInDoctorProfile() {
        return chamberDatabaseIdKeyInDoctorProfile;
    }

    public void setChamberDatabaseIdKeyInDoctorProfile(String chamberDatabaseIdKeyInDoctorProfile) {
        this.chamberDatabaseIdKeyInDoctorProfile = chamberDatabaseIdKeyInDoctorProfile;
    }

    @NonNull
    @Override
    public String toString() {
        return ("Name "+getChamberName()+"\n"+
                "Fees "+getChamberFees()+"\n"+
                "Address "+getChamberAddress()+"\n"+
                "Latlng "+getChamberLatLng().toString()+"\n"+
                "Time "+getChamberTime()+"\n"+
                "Active days "+getChamberOpenDays().toString()+"\n"+
                "doctorUserProfileId "+getDoctorUserProfileId()+"\n"+
                "chamberDatabaseId "+getChamberDatabaseId()+"\n"+
                "chamberDatabaseIdKeyInDoctorProfile "+ getChamberDatabaseIdKeyInDoctorProfile());
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chamberName);
        dest.writeString(chamberFees);
        dest.writeString(chamberAddress);
        Bundle bundle = new Bundle();
        bundle.putParcelable("latlng", chamberLatLng);
        dest.writeBundle(bundle);
        dest.writeString(chamberTime);
        dest.writeMap(chamberOpenDays);
        dest.writeString(doctorUserProfileId);
        dest.writeString(chamberDatabaseId);
        dest.writeString(chamberDatabaseIdKeyInDoctorProfile);
    }

    public static final Parcelable.Creator<Chamber> CREATOR = new Creator<Chamber>() {
        @Override
        public Chamber createFromParcel(Parcel source) {
            return new Chamber(source);
        }

        @Override
        public Chamber[] newArray(int size) {
            return new Chamber[size];
        }
    };
}
