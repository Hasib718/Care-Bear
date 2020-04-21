package com.hasib.carebear.doctor.container;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

public class Chamber {
    //Container for storing chamberName & address
    private String chamberName;
    private String chamberFees;
    private String chamberAddress;
    private LatLng chamberLatLng;
    private String chamberTime;
    private Map<String, Boolean> chamberOpenDays;

    public Chamber() {}

    public Chamber(String chamberName, String chamberFees, String chamberAddress, LatLng chamberLatLng, String chamberTime, Map<String, Boolean> chamberOpenDays) {
        this.chamberName = chamberName;
        this.chamberFees = chamberFees;
        this.chamberAddress = chamberAddress;
        this.chamberLatLng = chamberLatLng;
        this.chamberTime = chamberTime;
        this.chamberOpenDays = chamberOpenDays;
    }

    public Chamber(String chamberName, String chamberFees, String chamberAddress, LatLng chamberLatLng, String chamberTime) {
        this.chamberName = chamberName;
        this.chamberFees = chamberFees;
        this.chamberAddress = chamberAddress;
        this.chamberLatLng = chamberLatLng;
        this.chamberTime = chamberTime;
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

    public LatLng getChamberLatLng() {
        return chamberLatLng;
    }

    public void setChamberLatLng(LatLng chamberLatLng) {
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
        this.chamberOpenDays = chamberOpenDays;
    }

    @NonNull
    @Override
    public String toString() {
        return ("Name "+getChamberName()+"\n"+
                "Fees "+getChamberFees()+"\n"+
                "Address "+getChamberAddress()+"\n"+
                "Latlng "+getChamberLatLng()+"\n"+
                "Time "+getChamberTime()+"\n"+
                "Active days "+getChamberOpenDays().toString());
    }
}
