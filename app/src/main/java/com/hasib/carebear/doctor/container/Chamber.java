package com.hasib.carebear.doctor.container;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Chamber {
    //Container for storing chamberName & address
    private String chamberName;
    private String chamberFess;
    private String chamberAddress;
    private LatLng chamberLatLng;

    public Chamber() {}

    public Chamber(String chamberName, String chamberFess, String chamberAddress, LatLng chamberLatLng) {
        this.chamberName = chamberName;
        this.chamberFess = chamberFess;
        this.chamberAddress = chamberAddress;
        this.chamberLatLng = chamberLatLng;
    }

    public String getChamberName() {
        return chamberName;
    }

    public void setChamberName(String chamberName) {
        this.chamberName = chamberName;
    }

    public String getChamberFess() {
        return chamberFess;
    }

    public void setChamberFess(String chamberFess) {
        this.chamberFess = chamberFess;
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
}
